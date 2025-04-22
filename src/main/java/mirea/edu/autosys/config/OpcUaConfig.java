package mirea.edu.autosys.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import mirea.edu.autosys.service.OpcUaService;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpcUaConfig {
    private String applicationName = "AutoSys OPC UA Client";
    private String applicationUri = "urn:mirea:opcua:autosys";
    private String productUri = "AutoSys";

    @Autowired
    private OpcUaService opcUaService;

    private static String[] endpointUrls = {
            "opc.tcp://localhost:4801/freeopcua/server",
            "opc.tcp://localhost:4802/freeopcua/server",
            "opc.tcp://localhost:4803/freeopcua/server",
            "opc.tcp://localhost:4804/freeopcua/server",
            "opc.tcp://localhost:4805/freeopcua/server",
            "opc.tcp://localhost:4806/freeopcua/server",
            "opc.tcp://localhost:4807/freeopcua/server",
            "opc.tcp://localhost:4808/freeopcua/server",
            "opc.tcp://localhost:4809/freeopcua/server",
            "opc.tcp://localhost:48010/freeopcua/server",
            "opc.tcp://localhost:48011/freeopcua/server",

            "opc.tcp://localhost:4811/freeopcua/server",
            "opc.tcp://localhost:4812/freeopcua/server",
            "opc.tcp://localhost:4813/freeopcua/server",
            "opc.tcp://localhost:4814/freeopcua/server",
            "opc.tcp://localhost:4815/freeopcua/server",
            "opc.tcp://localhost:4816/freeopcua/server",
            "opc.tcp://localhost:4817/freeopcua/server",
            "opc.tcp://localhost:4818/freeopcua/server",
            "opc.tcp://localhost:4819/freeopcua/server",
            "opc.tcp://localhost:48110/freeopcua/server",
            "opc.tcp://localhost:48111/freeopcua/server",
            // "opc.tcp://192.168.0.184:4840/"
    };

    public static Map<String, List<String>> subscribeNodes = new HashMap<>();
    {
        subscribeNodes.put(endpointUrls[0],
                Arrays.asList(
                        "ns=2;i=6"));
        // subscribeNodes.put(endpointUrls[1],
        //         Arrays.asList(
        //                 "ns=2;i=6"));
        // subscribeNodes.put(endpointUrls[11],
        //         Arrays.asList(
        //                 "ns=2;i=6"));
        // subscribeNodes.put(endpointUrls[12],
        //         Arrays.asList(
        //                 "ns=2;i=6"));
        // subscribeNodes.put(endpointUrls[3],
        // Arrays.asList(
        // "ns=1;s=InternalTemp"));
        // // "ns=1;s=FreeHeapSize",
        // // "ns=1;s=MinimumEverFreeHeapSize",
        // // "ns=1;s=HeapStats"));
    }
    
    @Bean
    public List<OpcUaClient> opcUaClients() throws Exception {
        List<OpcUaClient> clients = Arrays.asList(endpointUrls).stream()
                .map(url -> {
                    OpcUaClient client = createClient(url);
                    
                    // Получаем узлы для текущего URL
                    List<String> nodes = getNodesForEndpoint(url);
                    
                    // Подписка на узлы для каждого клиента
                    if (nodes != null && !nodes.isEmpty()) {
                        nodes.forEach(nodeId -> {
                            opcUaService.subscribeToNode(url, NodeId.parse(nodeId));
                        });
                    } else {
                        System.err.println("No nodes found for URL: " + url);
                    }
    
                    return client;
                })
                .collect(Collectors.toList());
        return clients;
    }

    public static List<String> getNodesForEndpoint(String url) {
        return subscribeNodes.get(url);
    }

    private OpcUaClient createClient(String url) {
        OpcUaClient client = null;
        try {
            client = OpcUaClient.create(
                    url,
                    endpoints -> endpoints.stream()
                            .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                            .findFirst(),
                    configBuilder -> {
                        configBuilder.setIdentityProvider(new AnonymousProvider())
                                .setApplicationName(LocalizedText.english(applicationName))
                                .setApplicationUri(applicationUri)
                                .setProductUri(productUri)
                                .setRequestTimeout(UInteger.valueOf(5000))
                                .setSessionTimeout(UInteger.valueOf(60000));
                        return configBuilder.build();
                    });
        } catch (UaException e) {
            e.printStackTrace();
        }

        CompletableFuture<UaClient> future = client.connect();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        OpcUaService.addtoClientsMap(url, client);
        return client;
    }
}
