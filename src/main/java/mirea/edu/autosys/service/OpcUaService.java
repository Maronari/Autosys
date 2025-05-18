package mirea.edu.autosys.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedDataItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedSubscription;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import mirea.edu.autosys.utils.NodeInfo;

@Service
public class OpcUaService {

    private final static Map<String, OpcUaClient> clients = new HashMap<>();
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    @Lazy
    private PasterizationService pasterizationService;

    private String applicationName = "AutoSys OPC UA Client";
    private String applicationUri = "urn:mirea:opcua:autosys";
    private String productUri = "AutoSys";

    public OpcUaClient createClient(String endpointUrl) throws Exception {
        OpcUaClient client = OpcUaClient.create(
                endpointUrl,
                endpoints -> endpoints.stream()
                        .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                        .findFirst(),
                configBuilder -> {
                    configBuilder.setIdentityProvider(new AnonymousProvider())
                            .setApplicationName(LocalizedText.english(applicationName)) // Название приложения
                            .setApplicationUri(applicationUri) // URI приложения
                            .setProductUri(productUri)
                            .setRequestTimeout(UInteger.valueOf(5000)) // Таймаут запросов
                            .setSessionTimeout(UInteger.valueOf(60000)); // Таймаут сессии
                    return configBuilder.build();
                });

        CompletableFuture<UaClient> future = client.connect();
        future.get(); // Ожидание подключения

        clients.put(endpointUrl, client);
        return client;
    }

    public static void addtoClientsMap(String endpointUrl, OpcUaClient client) {
        clients.put(endpointUrl, client);
    }

    public OpcUaClient getClient(String endpointUrl) {
        return clients.get(endpointUrl);
    }

    public void disconnectClient(String endpointUrl) throws Exception {
        OpcUaClient client = clients.remove(endpointUrl);
        if (client != null) {
            client.disconnect().get();
        }
    }

    public List<String> listClients() {
        return new ArrayList<>(clients.keySet());
    }

    public Object readValue(String endpointUrl, String nodeId) {
        try {
            OpcUaClient client = this.getClient(endpointUrl);
            if (client == null) {
                throw new RuntimeException("OPC UA Client for " + endpointUrl + " not found.");
            }

            NodeId node = NodeId.parse(nodeId);
            DataValue dataValue = client.readValue(0, TimestampsToReturn.Both, node).get();
            Variant variant = dataValue.getValue();

            return variant.getValue();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to read value from OPC UA server at " + endpointUrl, e);
        }
    }

    public void writeValue(String endpointUrl, String nodeId, String value) {
        try {
            OpcUaClient client = this.getClient(endpointUrl);
            if (client == null) {
                throw new RuntimeException("OPC UA Client for " + endpointUrl + " not found.");
            }

            NodeId node = NodeId.parse(nodeId);
            Variant v = new Variant(value);
            DataValue dv = new DataValue(v, StatusCode.GOOD);

            client.writeValue(node, dv);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write value to OPC UA server at " + endpointUrl, e);
        }
    }

    public void subscribeToNode(String endpointUrl, NodeId nodeId) {
        try {
            OpcUaClient client = getClient(endpointUrl);
            if (client == null) {
                throw new RuntimeException("OPC UA Client for " + endpointUrl + " not found.");
            }

            ManagedSubscription subscription = ManagedSubscription.create(client, 1000.0);
            subscription.setDefaultSamplingInterval(100.0);

            CompletableFuture<ManagedDataItem> itemFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return subscription.createDataItem(nodeId);
                } catch (UaException e) {
                    e.printStackTrace();
                }
                return null;
            });

            itemFuture.thenAccept(item -> {
                try {
                    String displayName = client.getAddressSpace().getNode(nodeId).getDisplayName().getText();
                    item.addDataValueListener(dataValue -> {
                        Variant variant = dataValue.getValue();
                        Object value = (Object) variant.getValue();

                        if (value instanceof Number) {
                            value = ((Number) value).doubleValue();
                        } else {
                            throw new RuntimeException("Unsupported value type: " + value.getClass().getName());
                        }

                        Instant timestamp = dataValue.getSourceTime().getJavaInstant();
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());

                        webSocketService.sendMessage(endpointUrl, nodeId, value.toString());

                        databaseService.saveSensorValue(endpointUrl, (Double) value, localDateTime);

                        pasterizationService.processNodeValue(endpointUrl, nodeId, value);
                    });
                } catch (UaException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<NodeInfo> getNodes(String url) {
        try {
            UaClient client = this.getClient(url);
            AddressSpace addressSpace = client.getAddressSpace();
            UaNode rootFolder = addressSpace.getNode(Identifiers.RootFolder);

            List<NodeInfo> allNodes = new ArrayList<>();
            browseNodesRecursively(rootFolder, client, allNodes);

            return allNodes;
        } catch (Exception e) {
            throw new RuntimeException("Error getting nodes", e);
        }
    }

    private void browseNodesRecursively(UaNode currentNode, UaClient client, List<NodeInfo> allNodes)
            throws UaException {
        List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(currentNode);

        for (UaNode node : nodes) {
            if (node.getNodeId().getNamespaceIndex().intValue() > 0) {
                allNodes.add(new NodeInfo(node.getNodeId().toParseableString(), node.getBrowseName().getName()));
            }
            browseNodesRecursively(node, client, allNodes);
        }
    }

}