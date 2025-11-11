package com.coldwindx.loki.controller;

import com.coldwindx.loki.store.WsSenderStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private WsSenderStore sender;

    @PostMapping(value = "/send")
    public String send(@RequestParam String id, @RequestParam String data) {
        Optional.ofNullable(sender.get(id)).ifPresent(s -> s.send(data));
        return "Success";
    }

    @PostMapping(value = "complete")
    public String complete(@RequestParam String id) {
        Optional.ofNullable(sender.get(id)).ifPresent(WsSenderStore.Sender::complete);
        return "Success";
    }
}
