package com.codewithhasita.webrelayretryservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class ReceiverManagementController {
    private final ReceiverRepository receiverRepository;

    @Value("${app.public-base-url}")
    private String baseUrl;

    @Value("${app.encryption-key}")
    private String encryptionKey;

    public ReceiverManagementController(ReceiverRepository receiverRepository) {
        this.receiverRepository = receiverRepository;
    }

    @PostMapping("/receivers")
    public ResponseEntity<CreateReceiverResponse> create(@RequestBody CreateReceiverRequest request){
        Receiver r = new Receiver();
        r.setName(request.getName());
        r.setDestinationURL(request.getDestinationURL());
        String plainSecret = UUID.randomUUID().toString();
        r.setSecretKey(EncryptionUtil.encrypt(plainSecret, encryptionKey));
        r.setManagementToken(UUID.randomUUID().toString());

        receiverRepository.save(r);
        String webhookUrl = baseUrl + "/webhook/" + r.getReceiverId();
        String managementUrl = baseUrl + "/manage/" + r.getManagementToken();

        return ResponseEntity.ok(new CreateReceiverResponse(webhookUrl, managementUrl));
    }

    @GetMapping("/manage/{token}")
    public ResponseEntity<ReceiverDetailsResponse> view(@PathVariable String token){
        Receiver receiver = receiverRepository.findByManagementToken(token)
                .orElseThrow(() -> new ReceiverNotFoundException("No receiver found for this management link"));

        String webhookUrl = baseUrl + "/webhook/" + receiver.getReceiverId();
        String plainSecret = EncryptionUtil.decrypt(receiver.getSecretKey(), encryptionKey);

        return ResponseEntity.ok(new ReceiverDetailsResponse(receiver.getName(),
                receiver.getDestinationURL(), webhookUrl, plainSecret));

    }

    @ExceptionHandler(ReceiverNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ReceiverNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
