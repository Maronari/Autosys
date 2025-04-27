// Инициализация графиков
document.addEventListener('DOMContentLoaded', function () {
    // Загрузка исторических данных из БД
    loadHistoricalData();
});

// Глобальные настройки для всех графиков
Chart.defaults.font.family = "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif";
Chart.defaults.color = '#666';
Chart.defaults.plugins.tooltip.backgroundColor = 'rgba(0, 0, 0, 0.7)';
Chart.defaults.plugins.legend.display = false;

// Глобальные переменные для графиков
let pasteurizationTempChart, pasteurizationPressureChart, pasteurizationFlowChart;
let heatingTempChart, heatingPressureChart, heatingFlowChart;
let coolingTempChart, coolingPressureChart, coolingFlowChart;

// Функция для отображения уведомлений (alerts)
function addAlert(type, title, message) {
    const alertContainer = document.getElementById('alert-container');
    if (!alertContainer) {
        console.error('Alert container not found.');
        return;
    }

    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        <strong>${title}</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    alertContainer.appendChild(alertDiv);
}

// Загрузка исторических данных из БД
function loadHistoricalData(hours = 24) {
    fetch(`/api/pasteurization/historical-data/pasteurization?hours=${hours}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка при загрузке данных');
            }
            return response.json();
        })
        .then(data => {
            console.log('Получены исторические данные:', data);

            // Инициализация графиков для секции пастеризации
            initTemperatureChart('pasteurizationTempChart', data.pasteurization.temperature);
            initPressureChart('pasteurizationPressureChart', data.pasteurization.pressure);
            initFlowChart('pasteurizationFlowChart', data.pasteurization.flow);
        })
        .catch(error => {
            console.error('Ошибка при загрузке исторических данных:', error);
        });

    fetch(`/api/pasteurization/historical-data/heating?hours=${hours}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка при загрузке данных');
            }
            return response.json();
        })
        .then(data => {
            console.log('Получены исторические данные:', data);

            // Инициализация графиков для секции нагрева
            initTemperatureChart('heatingTempChart', data.heating.temperature);
            initPressureChart('heatingPressureChart', data.heating.pressure);
            initFlowChart('heatingFlowChart', data.heating.flow);
        })
        .catch(error => {
            console.error('Ошибка при загрузке исторических данных:', error);
        });

    fetch(`/api/pasteurization/historical-data/cooling?hours=${hours}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка при загрузке данных');
            }
            return response.json();
        })
        .then(data => {
            console.log('Получены исторические данные:', data);

            // Инициализация графиков для секции охлаждения
            initTemperatureChart('coolingTempChart', data.cooling.temperature);
            initPressureChart('coolingPressureChart', data.cooling.pressure);
            initFlowChart('coolingFlowChart', data.cooling.flow);
        })
        .catch(error => {
            console.error('Ошибка при загрузке исторических данных:', error);
        });
}

// Показать индикаторы загрузки
function showLoadingIndicators() {
    const chartContainers = document.querySelectorAll('.card-body canvas');
    chartContainers.forEach(container => {
        container.style.opacity = '0.5';

        // Добавляем текст "Загрузка..." рядом с каждым графиком
        const loadingText = document.createElement('div');
        loadingText.className = 'loading-indicator';
        loadingText.textContent = 'Загрузка...';
        loadingText.style.position = 'absolute';
        loadingText.style.top = '50%';
        loadingText.style.left = '50%';
        loadingText.style.transform = 'translate(-50%, -50%)';
        loadingText.style.color = '#666';
        loadingText.style.fontWeight = 'bold';

        container.parentNode.style.position = 'relative';
        container.parentNode.appendChild(loadingText);
    });
}

// Скрыть индикаторы загрузки
function hideLoadingIndicators() {
    const chartContainers = document.querySelectorAll('.card-body canvas');
    chartContainers.forEach(container => {
        container.style.opacity = '1';

        // Удаляем индикаторы загрузки
        const loadingIndicator = container.parentNode.querySelector('.loading-indicator');
        if (loadingIndicator) {
            container.parentNode.removeChild(loadingIndicator);
        }
    });
}

// Обновление текущих значений на панели управления
function updateCurrentValues(data) {
    if (data.currentTemperature) {
        document.getElementById('current-temp').textContent = `${data.currentTemperature.toFixed(1)}°C`;
    }

    if (data.currentPressure) {
        document.getElementById('current-pressure').textContent = `${data.currentPressure.toFixed(1)} бар`;
    }

    if (data.currentFlow) {
        document.getElementById('current-flow').textContent = `${data.currentFlow.toFixed(0)} л/ч`;
    }

    if (data.totalFlow) {
        document.getElementById('total-flow').textContent = `${data.totalFlow.toFixed(0)} л`;
    }
}

// Инициализация графика температуры с данными из БД
function initTemperatureChart(chartId, data) {
    const ctx = document.getElementById(chartId).getContext('2d');

    const targetTemp = 75; // Целевая температура

    // Создаем массив целевой температуры той же длины, что и данные
    const targetTempData = Array(data.labels.length).fill(targetTemp);

    temperatureChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [
                {
                    label: 'Температура (°C)',
                    data: data.values,
                    borderColor: '#8c1c13',
                    backgroundColor: 'rgba(140, 28, 19, 0.1)',
                    borderWidth: 2,
                    pointRadius: 0,
                    pointHoverRadius: 4,
                    tension: 0.4,
                    fill: true
                },
                {
                    label: 'Целевая температура',
                    data: targetTempData,
                    borderColor: 'rgba(40, 167, 69, 0.7)',
                    borderWidth: 2,
                    borderDash: [5, 5],
                    pointRadius: 0,
                    pointHoverRadius: 0,
                    tension: 0
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    grid: {
                        display: false
                    }
                },
                y: {
                    min: 0,
                    max: 100,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        callback: function (value) {
                            return value + '°C';
                        }
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return context.dataset.label + ': ' + context.parsed.y.toFixed(1) + '°C';
                        }
                    }
                }
            }
        }
    });
}

// Инициализация графика давления с данными из БД
function initPressureChart(chartId, data) {
    const ctx = document.getElementById(chartId).getContext('2d');

    pressureChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Давление (бар)',
                data: data.values,
                borderColor: '#bf4342',
                backgroundColor: 'rgba(191, 67, 66, 0.1)',
                borderWidth: 2,
                pointRadius: 0,
                pointHoverRadius: 4,
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    grid: {
                        display: false
                    }
                },
                y: {
                    min: 0,
                    max: 4,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        callback: function (value) {
                            return value + ' бар';
                        }
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return context.dataset.label + ': ' + context.parsed.y.toFixed(2) + ' бар';
                        }
                    }
                }
            }
        }
    });
}

// Инициализация графика расхода с данными из БД
function initFlowChart(chartId, data) {
    const ctx = document.getElementById(chartId).getContext('2d');

    flowChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Расход (л/ч)',
                data: data.values,
                borderColor: '#17a2b8',
                backgroundColor: 'rgba(23, 162, 184, 0.1)',
                borderWidth: 2,
                pointRadius: 0,
                pointHoverRadius: 4,
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    grid: {
                        display: false
                    }
                },
                y: {
                    min: 0,
                    max: 10,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        callback: function (value) {
                            return value + ' л/ч';
                        }
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return context.dataset.label + ': ' + context.parsed.y.toFixed(1) + ' л/ч';
                        }
                    }
                }
            }
        }
    });
}

// Обновление временного диапазона графика
function updateChartTimeRange(chartId, hours) {
    // Показываем индикатор загрузки
    showLoadingIndicators();

    // Запрос к API для получения исторических данных за указанный период
    if (chartId === 'pasteurizationTempChart' || chartId === 'pasteurizationpressureChart' || chartId === 'pasteurizationflowChart') {
        fetch(`/api/pasteurization/historical-data/pasteurization?hours=${hours}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке данных');
                }
                return response.json();
            })
            .then(data => {
                console.log('Получены исторические данные:', data);

                // Обновляем графики с новыми данными
                if (chartId === 'pasteurizationTempChart' && temperatureChart) {
                    updateTemperatureChart(temperatureChart, data.pasteurization.temperature);
                } else if (chartId === 'pasteurizationpressureChart' && pressureChart) {
                    updatePressureChart(pressureChart, data.pasteurization.pressure);
                } else if (chartId === 'pasteurizationflowChart' && flowChart) {
                    updateFlowChart(flowChart, data.pasteurization.flow);
                }

                // Скрываем индикатор загрузки
                hideLoadingIndicators();
            })
            .catch(error => {
                console.error('Ошибка при загрузке исторических данных:', error);

                // Скрываем индикатор загрузки
                hideLoadingIndicators();

                // Показываем сообщение об ошибке
                addAlert('error', 'Ошибка загрузки данных', 'Не удалось загрузить исторические данные.');
            });
    }
    else if (chartId === 'heatingTempChart' || chartId === 'heatingpressureChart' || chartId === 'heatingflowChart') {
        fetch(`/api/pasteurization/historical-data/heating?hours=${hours}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке данных');
                }
                return response.json();
            })
            .then(data => {
                console.log('Получены исторические данные:', data);

                // Обновляем графики с новыми данными
                if (chartId === 'heatingTempChart' && temperatureChart) {
                    updateTemperatureChart(temperatureChart, data.heating.temperature);
                } else if (chartId === 'heatingpressureChart' && pressureChart) {
                    updatePressureChart(pressureChart, data.heating.pressure);
                } else if (chartId === 'heatingflowChart' && flowChart) {
                    updateFlowChart(flowChart, data.heating.flow);
                }

                // Скрываем индикатор загрузки
                hideLoadingIndicators();
            })
            .catch(error => {
                console.error('Ошибка при загрузке исторических данных:', error);

                // Скрываем индикатор загрузки
                hideLoadingIndicators();

                // Показываем сообщение об ошибке
                addAlert('error', 'Ошибка загрузки данных', 'Не удалось загрузить исторические данные.');
            });
    }
    if (chartId === 'coolingTempChart' || chartId === 'coolingChart' || chartId === 'coolingflowChart') {
        fetch(`/api/pasteurization/historical-data/cooling?hours=${hours}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке данных');
                }
                return response.json();
            })
            .then(data => {
                console.log('Получены исторические данные:', data);

                // Обновляем графики с новыми данными
                if (chartId === 'coolingTempChart' && temperatureChart) {
                    updateTemperatureChart(temperatureChart, data.cooling.temperature);
                } else if (chartId === 'coolingChart' && pressureChart) {
                    updatePressureChart(pressureChart, data.cooling.pressure);
                } else if (chartId === 'coolingflowChart' && flowChart) {
                    updateFlowChart(flowChart, data.cooling.flow);
                }

                // Скрываем индикатор загрузки
                hideLoadingIndicators();
            })
            .catch(error => {
                console.error('Ошибка при загрузке исторических данных:', error);

                // Скрываем индикатор загрузки
                hideLoadingIndicators();

                // Показываем сообщение об ошибке
                addAlert('error', 'Ошибка загрузки данных', 'Не удалось загрузить исторические данные.');
            });
    }
}

// Обновление графика температуры
function updateTemperatureChart(chart, data) {
    const targetTemp = 75; // Целевая температура

    chart.data.labels = data.labels;
    chart.data.datasets[0].data = data.values;
    //chart.data.datasets[1].data = Array(data.labels.length).fill(targetTemp);

    chart.update();
}

// Обновление графика давления
function updatePressureChart(chart, data) {
    chart.data.labels = data.labels;
    chart.data.datasets[0].data = data.values;

    chart.update();
}

// Обновление графика расхода
function updateFlowChart(chart, data) {
    chart.data.labels = data.labels;
    chart.data.datasets[0].data = data.values;

    chart.update();
}

// Обновление данных графиков в реальном времени
function updateChartData(chartId, newValue) {
    // Получаем объект графика по ID
    const chart = window[chartId];

    // Проверяем, существует ли график и его данные
    if (!chart || !chart.data || !chart.data.datasets || !chart.data.datasets[0]) {
        console.warn(`График ${chartId} не инициализирован или не имеет данных`);
        return;
    }

    // Удаление первой точки и добавление новой в конец
    chart.data.datasets[0].data.shift();
    chart.data.datasets[0].data.push(newValue);

    // Обновление временных меток
    const now = new Date();
    const timeString = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    chart.data.labels.shift();
    chart.data.labels.push(timeString);

    chart.update();
}