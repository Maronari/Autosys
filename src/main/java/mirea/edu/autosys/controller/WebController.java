package mirea.edu.autosys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class WebController {

    @GetMapping("/")
    public String index() {
        // for (String endpointUrl: OpcUaConfig.subscribeNodes.keySet())
        // {
        //     String url = opcUaBaseUrl + "/subscribe?endpointUrl=" + endpointUrl;
        //     restTemplate.postForEntity(url, null, String.class);
        // }
        return "index"; // Возвращает index.html из папки static
    }
}
