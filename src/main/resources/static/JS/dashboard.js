// Инициализация панели управления
document.addEventListener('DOMContentLoaded', function() {
    // Обновление текущего времени
    updateCurrentTime();
    setInterval(updateCurrentTime, 1000);
    
    // Инициализация обработчиков событий
    initEventListeners();
    
    // Добавление тестовых оповещений
    addSampleAlerts();
    
    // Загрузка текущего статуса системы
    loadSystemStatus();
});

// Загрузка текущего статуса системы
function loadSystemStatus() {
    fetch('/api/pasteurization/system-status')
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка при загрузке статуса системы');
            }
            return response.json();
        })
        .then(data => {
            console.log('Получен статус системы:', data);
            
            // Обновление статуса системы
            if (data.systemStatus) {
                updateSystemStatus(data.systemStatus);
            }
            
            // Обновление этапа процесса
            if (data.processStage) {
                updateProcessStage(data.processStage);
            }
            
            // Обновление целевых значений
            if (data.targetTemperature) {
                document.getElementById('target-temp').textContent = `${data.targetTemperature.toFixed(1)}°C`;
                document.getElementById('temp-setpoint').value = data.targetTemperature;
            }
            
            if (data.targetFlow) {
                document.getElementById('flow-setpoint').value = data.targetFlow;
            }
            
            // Обновление времени процесса
            if (data.stageTime) {
                document.getElementById('stage-time').textContent = formatTime(data.stageTime);
            }
            
            if (data.totalTime) {
                document.getElementById('total-time').textContent = formatTime(data.totalTime);
            }
        })
        .catch(error => {
            console.error('Ошибка при загрузке статуса системы:', error);
            addAlert('error', 'Ошибка загрузки', 'Не удалось загрузить текущий статус системы.');
        });
}

// Обновление текущего времени
function updateCurrentTime() {
    const now = new Date();
    const timeString = now.toLocaleTimeString();
    document.getElementById('current-time').textContent = timeString;
}

// Инициализация обработчиков событий
function initEventListeners() {
    // Кнопки управления
    document.getElementById('start-btn').addEventListener('click', function() {
        sendCommand('start');
        addAlert('info', 'Процесс запущен', 'Система начала процесс пастеризации');
    });
    
    document.getElementById('stop-btn').addEventListener('click', function() {
        sendCommand('stop');
        addAlert('error', 'Процесс остановлен', 'Система остановила процесс пастеризации');
    });
    
    document.getElementById('pause-btn').addEventListener('click', function() {
        sendCommand('pause');
        addAlert('warning', 'Процесс приостановлен', 'Система приостановила процесс пастеризации');
    });
    
    // Изменение целевых значений
    document.getElementById('temp-setpoint').addEventListener('change', function() {
        const value = this.value;
        document.getElementById('target-temp').textContent = `${value}°C`;
        sendCommand('setTemperature', value);
    });
    
    document.getElementById('flow-setpoint').addEventListener('change', function() {
        const value = this.value;
        document.getElementById('current-flow').textContent = `${value} л/ч`;
        sendCommand('setFlow', value);
    });
    
    // Очистка оповещений
    document.getElementById('clear-alerts').addEventListener('click', function() {
        document.getElementById('alerts-container').innerHTML = '';
    });
    
    // Изменение временного диапазона графиков
    document.getElementById('temp-time-range').addEventListener('change', function() {
        const hours = parseInt(this.value);
        updateChartTimeRange('temperatureChart', hours);
    });
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

// Добавление тестовых оповещений
function addSampleAlerts() {
    addAlert('info', 'Система запущена', 'Система пастеризации вина запущена и готова к работе');
    
    setTimeout(() => {
        addAlert('warning', 'Низкий уровень жидкости', 'Уровень жидкости в резервуаре ниже рекомендуемого');
    }, 2000);
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
    document.getElementById('current-stage').textContent = stages[stage];
    
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
    const statusDot = statusIndicator.querySelector('.status-dot');
    const statusText = statusIndicator.querySelector('.status-text');
    
    statusIndicator.className = 'status-indicator';
    
    switch (status) {
        case 'online':
            statusIndicator.classList.add('online');
            statusText.textContent = 'Система активна';
            break;
        case 'offline':
            statusIndicator.classList.add('offline');
            statusText.textContent = 'Система отключена';
            break;
        case 'warning':
            statusIndicator.classList.add('warning');
            statusText.textContent = 'Внимание';
            break;
        default:
            statusIndicator.classList.add('online');
            statusText.textContent = 'Система активна';
    }
}

// Обработка данных телеметрии
function processTelemetryData(data) {
    // Обновление значений на панели управления
    if (data.temperature) {
        document.getElementById('current-temp').textContent = `${data.temperature.toFixed(1)}°C`;
        updateChartData('temperatureChart', data.temperature);
    }
    
    if (data.pressure) {
        document.getElementById('current-pressure').textContent = `${data.pressure.toFixed(1)} бар`;
        updateChartData('pressureChart', data.pressure);
    }
    
    if (data.flow) {
        document.getElementById('current-flow').textContent = `${data.flow.toFixed(0)} л/ч`;
        updateChartData('flowChart', data.flow);
    }
    
    if (data.totalFlow) {
        document.getElementById('total-flow').textContent = `${data.totalFlow.toFixed(0)} л`;
    }
    
    // Обновление времени процесса
    if (data.stageTime) {
        document.getElementById('stage-time').textContent = formatTime(data.stageTime);
    }
    
    if (data.totalTime) {
        document.getElementById('total-time').textContent = formatTime(data.totalTime);
    }
}

// Обработка данных оповещений
function processAlertData(data) {
    addAlert(data.level, data.title, data.message);
}

// Обработка данных статуса
function processStatusData(data) {
    // Обновление статуса системы
    if (data.systemStatus) {
        updateSystemStatus(data.systemStatus);
    }
    
    // Обновление этапа процесса
    if (data.processStage) {
        updateProcessStage(data.processStage);
    }
}

// Обработка начальных данных
function processInitialData(data) {
    // Обновление всех значений на панели управления
    processTelemetryData(data.telemetry);
    processStatusData(data.status);
    
    // Обновление целевых значений
    if (data.targetValues) {
        if (data.targetValues.temperature) {
            document.getElementById('target-temp').textContent = `${data.targetValues.temperature.toFixed(1)}°C`;
            document.getElementById('temp-setpoint').value = data.targetValues.temperature;
        }
        
        if (data.targetValues.flow) {
            document.getElementById('flow-setpoint').value = data.targetValues.flow;
        }
    }
}

// Форматирование времени в формат ЧЧ:ММ:СС
function formatTime(seconds) {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
}