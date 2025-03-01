package mirea.edu.autosys.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedDataItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedSubscription;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OpcUaService {

    @Autowired
    private OpcUaClient opcUaClient;
    @Autowired
    private WebSocketService webSocketService;

    public String readValue(String nodeId) {
        // Логика чтения значения из OPC UA
        return "Value from OPC UA";
    }

    public void writeValue(String nodeId, String value) {
        // Логика записи значения в OPC UA
    }

    // Метод для REST API
    public int readRandomInt32() {
        try {
            NodeId nodeId = new NodeId(2, "Dynamic/RandomInt32");
            DataValue dataValue = opcUaClient.readValue(0, TimestampsToReturn.Both, nodeId).get();
            Variant variant = dataValue.getValue();
            if (variant.getValue() instanceof Integer) {
                return (Integer) variant.getValue();
            } else {
                throw new RuntimeException("Value is not an integer");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to read value from OPC UA server", e);
        }
    }

    public void subscribeToNode(NodeId nodeId) {
        try {
            ManagedSubscription subscription = ManagedSubscription.create(opcUaClient);
            subscription.setPublishingInterval(1000);
    
            CompletableFuture<ManagedDataItem> itemFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return subscription.createDataItem(nodeId);
                } catch (UaException e) {
                    e.printStackTrace();
                }
                return null;
            });

            itemFuture.thenAccept(item -> item.addDataValueListener(dataValue -> {
                Variant variant = dataValue.getValue();
                Object value = variant.getValue();
                webSocketService.sendMessage(nodeId, value.toString());
            }));
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}