package mirea.edu.autosys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import mirea.edu.autosys.config.OpcUaConfig;


@Controller
@RequestMapping("/")
public class WebController {
    @Autowired
    private RestTemplate restTemplate;

    private final String opcUaBaseUrl = "http://localhost:8080/api/opcua";
    

    @GetMapping("/")
    public String index() {
        for (String endpointUrl: OpcUaConfig.subscribeNodes.keySet())
        {
            String url = opcUaBaseUrl + "/subscribe?endpointUrl=" + endpointUrl;
            restTemplate.postForEntity(url, null, String.class);
        }
        return "index"; // Возвращает index.html из папки static
    }
}
