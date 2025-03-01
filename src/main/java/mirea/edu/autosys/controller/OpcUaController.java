package mirea.edu.autosys.controller;

import mirea.edu.autosys.service.OpcUaService;

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

    @GetMapping("/read")
    public String readValue(@RequestParam String nodeId) {
        return opcUaService.readValue(nodeId);
    }

    @PostMapping("/write")
    public void writeValue(@RequestParam String nodeId, @RequestParam String value) {
        opcUaService.writeValue(nodeId, value);
    }

    @GetMapping("/random-int32")
    public ResponseEntity<Integer> getRandomInt32() {
        try {
            int value = opcUaService.readRandomInt32();
            return ResponseEntity.ok(value);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
