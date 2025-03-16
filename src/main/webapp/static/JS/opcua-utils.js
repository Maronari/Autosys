// opcua-utils.js

async function connectClient(endpointUrl) {
    let response = await fetch(`/api/opcua/connect?endpointUrl=${encodeURIComponent(endpointUrl)}`, { method: "POST" });
    // alert(await response.text());
    //loadClients();
}

async function disconnectClient(endpointUrl) {
    let response = await fetch(`/api/opcua/disconnect?endpointUrl=${encodeURIComponent(endpointUrl)}`, { method: "DELETE" });
    // alert(await response.text());
    //loadClients();
}

function updateButtonStyle(url) {
    const button = document.querySelector(`button[onclick="disconnectClient('${url}')"]`);
    button.style.color = 'red';
    button.innerHTML += '<br><button onclick="updateButtonStyle(\'${url}\')">Подключить</button>';
}

async function loadClients() {
    let list = document.getElementById("clientList");
    list.innerHTML = "";

    fetch('/api/opcua/clients')
        .then(response => response.json())
        .then(data => {
            const uniqueConnectedClients = [...new Set([...data])];

            uniqueConnectedClients.forEach(url => {
                let item = document.createElement("li");
                item.innerHTML = `${url} 
                                <button onclick="openNodesPage('${url}')">Перейти к узлам</button>
                                <button onclick="disconnectClient('${url}')">Отключить</button>`;
                list.appendChild(item);
            });
        });
}

function openNodesPage(url) {
    window.location.href = '/node_statuses.html?endpointUrl=' + encodeURIComponent(url);
}

async function checkConnectionStatus(url) {
    try {
        const response = await fetch(`/api/opcua/check-connection?endpointUrl=${encodeURIComponent(url)}`, { method: "GET" });
        if (response.ok) {
            const isConnected = await response.json();
            return isConnected;
        } else {
            return false;
        }
    } catch (error) {
        console.error(`Error checking connection status for ${url}:`, error);
        return false;
    }
}

const urlParams = new URLSearchParams(window.location.search);
const endpointUrl = urlParams.get('endpointUrl');
function LoadNodes() {

    fetch(`/api/opcua/nodes?endpointUrl=${endpointUrl}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(nodes => {
            console.log('Узлы:', nodes);
            const nodeList = document.getElementById('nodeList');
            nodeList.innerHTML = '';
            nodes.forEach(node => {
                const li = document.createElement('li');
                li.textContent = `${node.nodeId}: ${node.nodeName}`;
                nodeList.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Не удалось загрузить информацию о узлах. Попробуйте еще раз.');
        });
}

function subscribeToNode(endpointUrl, nodeId) {
    fetch(`/api/opcua/subscribe?endpointUrl=${endpointUrl}&nodeId=${nodeId}`)
        .then(response => response.text())
        .then(result => {
            console.log(`Subscribe ${nodeId}: ${result}`);
        })
        .catch(error => {
            console.error(`Error subscribing ${nodeId}:`, error);
        });
}