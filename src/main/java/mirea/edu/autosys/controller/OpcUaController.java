package mirea.edu.autosys.controller;

import mirea.edu.autosys.config.OpcUaConfig;
import mirea.edu.autosys.service.OpcUaService;
import mirea.edu.autosys.utils.NodeInfo;

import java.util.Collections;
import java.util.List;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/opcua")
public class OpcUaController {

    private final OpcUaService opcUaService;

    public OpcUaController(OpcUaService opcUaService) {
        this.opcUaService = opcUaService;
    }

    @PostMapping("/connect")
    public ResponseEntity<String> connect(@RequestParam String endpointUrl) {
        try {
            opcUaService.createClient(endpointUrl);
            return ResponseEntity.ok("Connected to " + endpointUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to connect: " + e.getMessage());
        }
    }

    @GetMapping("/clients")
    public ResponseEntity<List<String>> listClients() {
        return ResponseEntity.ok(opcUaService.listClients());
    }

    @GetMapping("/check-connection")
    public ResponseEntity<Boolean> checkConnection(@RequestParam String endpointUrl) {
        boolean isConnected = opcUaService.listClients().contains(endpointUrl);
        return ResponseEntity.ok(isConnected);
    }

    @DeleteMapping("/disconnect")
    public ResponseEntity<String> disconnect(@RequestParam String endpointUrl) {
        try {
            opcUaService.disconnectClient(endpointUrl);
            return ResponseEntity.ok("Disconnected from " + endpointUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to disconnect: " + e.getMessage());
        }
    }

    @GetMapping("/read")
    public ResponseEntity<?> readValue(@RequestParam String endpointUrl, @RequestParam String nodeId) {
        try {
            Object value = opcUaService.readValue(endpointUrl, nodeId);
            return ResponseEntity.ok(value);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading value: " + e.getMessage());
        }
    }

    @PostMapping("/write")
    public ResponseEntity<String> writeValue(@RequestParam String endpointUrl, @RequestParam String nodeId,
            @RequestParam String value) {
        try {
            opcUaService.writeValue(endpointUrl, nodeId, value);
            return ResponseEntity.ok("Value written successfully to " + nodeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error writing value: " + e.getMessage());
        }
    }

    @GetMapping("/random-int32")
    public ResponseEntity<?> getRandomInt32(@RequestParam String endpointUrl) {
        try {
            int value = (Integer) opcUaService.readValue(endpointUrl, "Dynamic/RandomInt32");
            return ResponseEntity.ok(value);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading RandomInt32: " + e.getMessage());
        }
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeToNodes(@RequestParam String endpointUrl) {
        List<String> nodes = OpcUaConfig.getNodesForEndpoint(endpointUrl);
        for (String node: nodes)
        {
            try {
                opcUaService.subscribeToNode(endpointUrl, NodeId.parse(node));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error subscribing: " + e.getMessage());
            }
        }
        return ResponseEntity.ok("Subscribed successfully to " + nodes);
    }

    @GetMapping("/nodes")
    public ResponseEntity<List<NodeInfo>> getNodes(@RequestParam String endpointUrl) {
        try {
            List<NodeInfo> nodes = opcUaService.getNodes(endpointUrl);
            return new ResponseEntity<>(nodes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
