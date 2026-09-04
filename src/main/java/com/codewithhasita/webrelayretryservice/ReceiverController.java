package com.codewithhasita.webrelayretryservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class ReceiverController {
    private final ReceiverRepository receiverRepository;

    @Value("${app.encryption-key}")
    private String encryptionKey;

    public ReceiverController(ReceiverRepository receiverRepository){
        this.receiverRepository = receiverRepository;
    }

    @PostMapping("${receiver-api.path}/{receiverId}")
    public ResponseEntity<String> receive(@PathVariable("receiverId") Long receiverId,
                                          @RequestBody String payload,
                                          @RequestHeader("X-Webhook-Signature") String signature){
        Receiver receiver = receiverRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("No receiver found with id " + receiverId));

        String plainSecret = EncryptionUtil.decrypt(receiver.getSecretKey(), encryptionKey);
        String expectedSignature = HmacUtil.sign(payload, plainSecret);

        if(!expectedSignature.equals(signature)){
            return ResponseEntity.status(401).body("Invalid signature");
        }
        return ResponseEntity.ok("Receiver Received!\n" + payload + "\n------------\n");
    }
}

