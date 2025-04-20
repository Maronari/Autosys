package mirea.edu.autosys.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthStatusController {
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("OK");
    }
}

