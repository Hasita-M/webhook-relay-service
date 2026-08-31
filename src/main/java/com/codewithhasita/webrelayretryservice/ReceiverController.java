package com.codewithhasita.webrelayretryservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class ReceiverController {
    private final ReceiverRepository receiverRepository;

    public ReceiverController(ReceiverRepository receiverRepository){
        this.receiverRepository = receiverRepository;
    }

    @PostMapping("${receiver-api.path}")
    public ResponseEntity<String> receive(@RequestBody String payload,
                                          @RequestHeader("X-Webhook-Signature") String signature){
        Receiver receiver = receiverRepository.findAll().stream().findFirst().orElseThrow(() -> new
                RuntimeException("No receiver configured"));

        String expectedSignature = HmacUtil.sign(payload, receiver.getSecretKey());

        if(!expectedSignature.equals(signature)){
            return ResponseEntity.status(401).body("Invalid signature");
        }
        return ResponseEntity.ok("Receiver Received!\n" + payload + "\n------------\n");
    }
}

