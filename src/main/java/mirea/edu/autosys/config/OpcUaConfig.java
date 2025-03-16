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
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpcUaConfig {
    private String applicationName = "AutoSys OPC UA Client";
    private String applicationUri = "urn:mirea:opcua:autosys";
    private String productUri = "AutoSys";

    private static String[] endpointUrls = {
            "opc.tcp://milo.digitalpetri.com:62541/milo",
            "opc.tcp://localhost:4841/freeopcua/server",
            "opc.tcp://localhost:4842/freeopcua/server",
            // "opc.tcp://192.168.0.184:4840/"
    };

    public static Map<String, List<String>> subscribeNodes = new HashMap<>();
    {
        subscribeNodes.put(endpointUrls[0],
                Arrays.asList(
                        "ns=2;s=Dynamic/RandomDouble"));
        subscribeNodes.put(endpointUrls[1],
                Arrays.asList(
                        "ns=2;i=6"));
        subscribeNodes.put(endpointUrls[2],
                Arrays.asList(
                        "ns=2;i=6"));
        // subscribeNodes.put(endpointUrls[3],
        // Arrays.asList(
        // "ns=1;s=InternalTemp"));
        // // "ns=1;s=FreeHeapSize",
        // // "ns=1;s=MinimumEverFreeHeapSize",
        // // "ns=1;s=HeapStats"));
    }

    @Bean
    public List<OpcUaClient> opcUaClients() throws Exception {
        return Arrays.asList(endpointUrls).stream()
                .map(url -> createClient(url))
                .collect(Collectors.toList());
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
