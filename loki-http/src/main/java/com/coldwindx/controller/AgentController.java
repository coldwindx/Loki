package com.coldwindx.controller;

import com.coldwindx.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class AgentController {
    @Autowired
    private AgentService service;

    @PostMapping("call")
    public String call(@RequestParam String input) {
        return service.call(input);
    }
}
