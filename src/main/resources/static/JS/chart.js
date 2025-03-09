document.addEventListener('DOMContentLoaded', function () {
    // Маппинг nodeId на идентификаторы графиков
    const nodeIdToChartId = {
        'ns=2;s=Dynamic/RandomDouble': 'chart1',
        'ns=2;s=Dynamic/RandomFloat': 'chart2',
        'chart3': 'chart3'
    };

    const charts = {
        chart1: createChart('RandomDoubleChart', 'ns=2;s=Dynamic/RandomDouble'),
        chart2: createChart('RandomFloatChart', 'ns=2;s=Dynamic/RandomFloat'),
        chart3: createChart('chart3', 'Node 3')
    };

    // Подключение к WebSocket
    const socket = new WebSocket('ws://localhost:8080/ws'); // Подключаемся к WebSocket

    socket.onmessage = function (event) {
        const data = JSON.parse(event.data); // Парсим JSON
        const nodeId = data.nodeId; // Извлекаем nodeId
        const value = data.value; // Извлекаем value

        console.log(`Received data: nodeId=${nodeId}, value=${value}`);

        // Находим идентификатор графика по nodeId
        const chartId = nodeIdToChartId[nodeId];
        if (chartId && charts[chartId]) {
            const chart = charts[chartId];
            const timestamp = Date.now(); // Текущее время в миллисекундах
            chart.data.datasets[0].data.push({
                x: timestamp, // Временная метка (ось X)
                y: value // Значение (ось Y)
            });

            // Ограничиваем количество точек на графике (например, последние 20 значений)
            if (chart.data.datasets[0].data.length > 20) {
                chart.data.datasets[0].data.shift(); // Удаляем первую точку
            }

            chart.update(); // Обновляем график
        }
    };

    socket.onopen = function () {
        console.log('WebSocket connection established');
    };

    socket.onclose = function () {
        console.log('WebSocket connection closed');
    };

    socket.onerror = function (error) {
        console.error('WebSocket error:', error);
    };
});

// Функция для создания графика
function createChart(canvasId, label) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) {
        console.error(`Canvas element with id ${canvasId} not found!`);
        return null;
    }

    const ctx = canvas.getContext('2d');
    return new Chart(ctx, {
        type: 'line', // Тип графика (линейный)
        data: {
            datasets: [{
                label: label, // Название графика
                data: [], // Данные (ось Y)
                borderColor: getRandomColor(), // Случайный цвет линии
                fill: false // Не заливать область под графиком
            }]
        },
        options: {
            animation: false, // Отключаем анимацию
            scales: {
                x: {
                    type: 'time', // Используем временную шкалу для оси X
                    time: {
                        unit: 'second', // Единица времени (секунды)
                        displayFormats: {
                            second: 'HH:mm:ss' // Формат отображения времени
                        }
                    },
                    title: {
                        display: true,
                        text: 'Time' // Название оси X
                    }
                },
                y: {
                    beginAtZero: true, // Начинать ось Y с нуля
                    title: {
                        display: true,
                        text: 'Value' // Название оси Y
                    }
                }
            }
        }
    });
}

// Функция для генерации случайного цвета
function getRandomColor() {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}