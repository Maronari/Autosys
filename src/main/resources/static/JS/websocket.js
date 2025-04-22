// Используем уже существующую переменную websocket или создаем новую
// если она еще не была объявлена
if (typeof websocket === 'undefined') {
    var websocket;
}

// Карта соответствия nodeId и типов данных
const NODE_MAPPINGS = {
    // Температура
    "ns=2;i=6": {
        type: "temperature",
        displayId: "current-temp",
        chartId: "temperatureChart",
        formatValue: (value) => `${parseFloat(value).toFixed(1)}°C`
    },
    // Время этапа
    "ns=1;s=StageTime": {
        type: "stageTime",
        displayId: "stage-time",
        formatValue: (value) => formatTime(parseInt(value))
    },
    // Общее время
    "ns=1;s=TotalTime": {
        type: "totalTime",
        displayId: "total-time",
        formatValue: (value) => formatTime(parseInt(value))
    },
    // Этап процесса
    "ns=1;s=ProcessStage": {
        type: "processStage",
        handler: (value) => updateProcessStage(parseInt(value))
    },
    // Статус системы
    "ns=1;s=SystemStatus": {
        type: "systemStatus",
        handler: (value) => updateSystemStatus(value)
    },
    // Целевая температура
    "ns=1;s=TargetTemperature": {
        type: "targetTemperature",
        displayId: "target-temp",
        inputId: "temp-setpoint",
        formatValue: (value) => `${parseFloat(value).toFixed(1)}°C`
    },
    // Целевой расход
    "ns=1;s=TargetFlow": {
        type: "targetFlow",
        inputId: "flow-setpoint"
    }
};

let reconnectAttempts = 0;
const maxReconnectAttempts = 5;
const reconnectInterval = 3000; // 3 секунды

document.addEventListener('DOMContentLoaded', function() {
    // Получаем функции из charts.js
    if (typeof window.updateChartData === 'function') {
        updateChartData = window.updateChartData;
    } else {
        console.error('Функция updateChartData не найдена. Убедитесь, что charts.js загружен до websocket.js');
    }
    
    if (typeof window.updateChartTimeRange === 'function') {
        updateChartTimeRange = window.updateChartTimeRange;
    } else {
        console.error('Функция updateChartTimeRange не найдена.');
    }
    
    if (typeof window.addAlert === 'function') {
        addAlert = window.addAlert;
    } else {
        console.error('Функция addAlert не найдена.');
    }

    if (typeof window.updateSystemStatus === 'function') {
        updateSystemStatus = window.updateSystemStatus;
    } else {
        console.error('Функция updateSystemStatus не найдена.');
    }

    if (typeof window.updateProcessStage === 'function') {
        updateProcessStage = window.updateProcessStage;
    } else {
        console.error('Функция updateProcessStage не найдена.');
    }
    
    // Инициализация WebSocket соединения
    initWebSocket();
});

// Инициализация WebSocket соединения
function initWebSocket() {
    // Обновленный адрес WebSocket сервера согласно вашей конфигурации
    const wsUrl = 'ws://localhost:8080/ws';
    
    try {
        websocket = new WebSocket(wsUrl);
        
        // Обработчики событий WebSocket
        websocket.onopen = onWebSocketOpen;
        websocket.onmessage = onWebSocketMessage;
        websocket.onclose = onWebSocketClose;
        websocket.onerror = onWebSocketError;
    } catch (error) {
        console.error('Ошибка при создании WebSocket соединения:', error);
        addAlert('error', 'Ошибка соединения', 'Не удалось установить соединение с сервером');
    }
}

// Обработчик открытия соединения
function onWebSocketOpen(event) {
    console.log('WebSocket соединение установлено');
    addAlert('info', 'Соединение установлено', 'Соединение с сервером установлено успешно');
    
    // Сброс счетчика попыток переподключения
    reconnectAttempts = 0;
    
    // Обновление статуса системы
    updateSystemStatus('online');
    
    // Запрос начальных данных
    sendCommand('getInitialData');
}

// Обработчик получения сообщения
function onWebSocketMessage(event) {
    try {
        const data = JSON.parse(event.data);
        console.log('Получены данные:', data);
        
        // Проверяем наличие необходимых полей
        if (!data.endpointUrl || !data.nodeId) {
            console.warn('Получено сообщение без endpointUrl или nodeId:', data);
            return;
        }
        
        // Обработка сообщения в зависимости от типа
        if (data.messageType === 'alert') {
            // Обработка оповещения
            processAlertData(data);
        } else {
            // Обработка телеметрии или других типов данных
            processNodeData(data);
        }
    } catch (error) {
        console.error('Ошибка при обработке сообщения WebSocket:', error);
    }
}

// Обработка данных узла OPC
function processNodeData(data) {
    const nodeMapping = NODE_MAPPINGS[data.nodeId];
    
    if (!nodeMapping) {
        console.warn(`Неизвестный nodeId: ${data.nodeId}`);
        return;
    }
    
    // Если есть обработчик для этого типа узла, вызываем его
    if (nodeMapping.handler) {
        nodeMapping.handler(data.value);
        return;
    }
    
    // Обновляем отображаемое значение, если указан displayId
    if (nodeMapping.displayId) {
        const displayElement = document.getElementById(nodeMapping.displayId);
        if (displayElement) {
            displayElement.textContent = nodeMapping.formatValue ? 
                nodeMapping.formatValue(data.value) : data.value;
        }
    }
    
    // Обновляем значение ввода, если указан inputId
    if (nodeMapping.inputId) {
        const inputElement = document.getElementById(nodeMapping.inputId);
        if (inputElement) {
            inputElement.value = data.value;
        }
    }
    
    // Обновляем график, если указан chartId
    if (nodeMapping.chartId) {
        updateChartData(nodeMapping.chartId, parseFloat(data.value));
    }
}

// Обработка данных оповещений
function processAlertData(data) {
    // Определяем уровень оповещения (info, warning, error)
    const level = data.level || 'info';
    
    // Добавляем оповещение
    addAlert(level, data.title || 'Оповещение', data.message || data.value);
}

// Обработчик закрытия соединения
function onWebSocketClose(event) {
    console.log('WebSocket соединение закрыто:', event.code, event.reason);
    
    // Обновление статуса системы
    updateSystemStatus('offline');
    
    // Попытка переподключения
    if (reconnectAttempts < maxReconnectAttempts) {
        reconnectAttempts++;
        addAlert('warning', 'Соединение потеряно', `Попытка переподключения ${reconnectAttempts}/${maxReconnectAttempts}...`);
        
        setTimeout(initWebSocket, reconnectInterval);
    } else {
        addAlert('error', 'Соединение потеряно', 'Превышено максимальное количество попыток переподклю��ения');
    }
}

// Обработчик ошибки соединения
function onWebSocketError(event) {
    console.error('Ошибка WebSocket:', event);
    addAlert('error', 'Ошибка соединения', 'Произошла ошибка в соединении WebSocket');
}

// Отправка команды на сервер
function sendCommand(command, value = null) {
    if (websocket && websocket.readyState === WebSocket.OPEN) {
        const message = {
            command: command,
            value: value,
            timestamp: new Date().toISOString()
        };
        
        websocket.send(JSON.stringify(message));
        console.log(`Отправлена команда: ${command}`, value ? `со значением: ${value}` : '');
    } else {
        console.error('WebSocket не подключен');
        addAlert('error', 'Ошибка соединения', 'Невозможно отправить команду: соединение потеряно');
    }
}

// Добавление оповещения
function addAlert(type, title, message) {
    const alertsContainer = document.getElementById('alerts-container');
    if (!alertsContainer) return;
    
    const now = new Date();
    const timeString = now.toLocaleTimeString();
    
    const alertElement = document.createElement('div');
    alertElement.className = `alert-item ${type}`;
    alertElement.innerHTML = `
        <div class="alert-content">
            <span class="alert-title">${title}</span>
            <span class="alert-message">${message}</span>
            <span class="alert-time">${timeString}</span>
        </div>
        <button class="btn-close">×</button>
    `;
    
    // Добавление обработчика для закрытия оповещения
    alertElement.querySelector('.btn-close').addEventListener('click', function() {
        alertsContainer.removeChild(alertElement);
    });
    
    // Добавление оповещения в начало списка
    alertsContainer.insertBefore(alertElement, alertsContainer.firstChild);
    
    // Автоматическое удаление оповещения через 10 секунд
    setTimeout(() => {
        if (alertElement.parentNode === alertsContainer) {
            alertsContainer.removeChild(alertElement);
        }
    }, 10000);
}

// Обновление этапа процесса
function updateProcessStage(stage) {
    const stages = {
        1: 'Подготовка',
        2: 'Нагрев',
        3: 'Пастеризация',
        4: 'Охлаждение',
        5: 'Завершение'
    };
    
    // Обновление текущего этапа
    const currentStageElement = document.getElementById('current-stage');
    if (currentStageElement) {
        currentStageElement.textContent = stages[stage] || `Этап ${stage}`;
    }
    
    // Обновление визуального отображения этапов
    const timelineSteps = document.querySelectorAll('.timeline-step');
    timelineSteps.forEach(step => {
        const stepNumber = parseInt(step.getAttribute('data-step'));
        
        if (stepNumber < stage) {
            step.classList.add('active');
            step.classList.remove('current');
        } else if (stepNumber === stage) {
            step.classList.add('active', 'current');
        } else {
            step.classList.remove('active', 'current');
        }
    });
}

// Обновление статуса системы
function updateSystemStatus(status) {
    const statusIndicator = document.querySelector('.status-indicator');
    if (!statusIndicator) return;
    
    const statusText = statusIndicator.querySelector('.status-text');
    
    statusIndicator.className = 'status-indicator';
    
    switch (status) {
        case 'online':
            statusIndicator.classList.add('online');
            if (statusText) statusText.textContent = 'Система активна';
            break;
        case 'offline':
            statusIndicator.classList.add('offline');
            if (statusText) statusText.textContent = 'Система отключена';
            break;
        case 'warning':
            statusIndicator.classList.add('warning');
            if (statusText) statusText.textContent = 'Внимание';
            break;
        default:
            statusIndicator.classList.add('online');
            if (statusText) statusText.textContent = 'Система активна';
    }
}

// Форматирование времени в формат ЧЧ:ММ:СС
function formatTime(seconds) {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
}