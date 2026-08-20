package com.codewithhasita.webrelayretryservice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class ReceiverController {
    @PostMapping("${receiver-api.path}")
    public String receive(@RequestBody String payload){
        return "Receiver Received!\n" + payload + "\n------------\n";
    }
}

