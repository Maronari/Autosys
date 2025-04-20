package mirea.edu.autosys.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CsrfController {
    @GetMapping("/csrf")
    public Map<String, String> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return Collections.singletonMap("token", csrf.getToken());
    }
}
