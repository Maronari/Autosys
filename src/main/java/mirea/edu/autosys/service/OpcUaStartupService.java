package mirea.edu.autosys.service;

import jakarta.annotation.PostConstruct;

import java.util.List;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.springframework.stereotype.Service;

@Service
public class OpcUaStartupService {

    private final OpcUaService subscriptionService;

    public OpcUaStartupService(OpcUaService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostConstruct
    public void init() {
        List<NodeId> nodes = List.of(
            new NodeId(2, "Dynamic/RandomDouble"),
            new NodeId(2, "Dynamic/RandomFloat")
        );
        nodes.forEach(subscriptionService::subscribeToNode);
    }
}

