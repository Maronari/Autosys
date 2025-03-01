package mirea.edu.autosys.config;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;

@Configuration
public class OpcUaClientConfig {

    @Bean
    public OpcUaClient opcUaClient() throws Exception {
        String endpointUrl = "opc.tcp://milo.digitalpetri.com:62541/milo";
        OpcUaClient client = OpcUaClient.create(
            endpointUrl,
            endpoints -> endpoints.stream()
                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                .findFirst(),
            configBuilder -> {
                configBuilder.setIdentityProvider(new AnonymousProvider());
                return configBuilder.build();
            }
        );

        CompletableFuture<UaClient> future = client.connect();
        future.get(); // Ожидание подключения
        return client;
    }
}