document.addEventListener('DOMContentLoaded', function () {
    const nodeIdToChartId = {
        'opc.tcp://localhost:4801/freeopcua/server:ns=2;i=6': 'chart1',
        'opc.tcp://localhost:4802/freeopcua/server:ns=2;i=6': 'chart2',
        'opc.tcp://localhost:4811/freeopcua/server:ns=2;i=6': 'chart3',
        'opc.tcp://localhost:4812/freeopcua/server:ns=2;i=6': 'chart4',
    };

    const charts = {
        chart1: createChart('Temperature_Sensor_1', 'Температура в баке TF1'),
        chart2: createChart('Pressure_Sensor_1', 'Давление в баке TF1'),
        chart3: createChart('Temperature_Sensor_2', 'Температура в баке TF2'),
        chart4: createChart('Pressure_Sensor_2', 'Давление в баке TF2'),
    };

    const socket = new WebSocket('http://localhost:8080/ws');

    socket.onmessage = function (event) {
        const data = JSON.parse(event.data);
        const endpointdURL = data.endpointUrl;
        const nodeId = data.nodeId;
        const value = data.value;

        const uniqueIdentifier = `${endpointdURL}:${nodeId}`;
        
        const chartId = nodeIdToChartId[uniqueIdentifier];
        if (chartId in charts) {
            const chart = charts[chartId];
            const timestamp = Date.now();
            chart.data.datasets[0].data.push({
                x: timestamp,
                y: value
            });

            if (chart.data.datasets[0].data.length > 20) {
                chart.data.datasets[0].data.shift();
            }

            chart.update();
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

function createChart(canvasId, label) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) {
        console.error(`Canvas element with id ${canvasId} not found!`);
        return null;
    }

    const ctx = canvas.getContext('2d');
    return new Chart(ctx, {
        type: 'line',
        data: {
            datasets: [{
                label: label,
                data: [],
                borderColor: getRandomColor(),
                fill: false
            }]
        },
        options: {
            animation: false,
            scales: {
                x: {
                    type: 'time',
                    time: {
                        unit: 'second',
                        displayFormats: {
                            second: 'HH:mm:ss'
                        }
                    },
                    title: {
                        display: true,
                        text: 'Time'
                    }
                },
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Value'
                    }
                }
            }
        }
    });
}

function getRandomColor() {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}