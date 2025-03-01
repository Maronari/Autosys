package mirea.edu.autosys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/subscribe")
public class WebController {
    @GetMapping("/")
    public String index() {
        return "index"; // Возвращает index.html из папки static
    }
}
