package com.br.susreceita.evidence.infrastructure.service;

import com.br.susreceita.evidence.domain.gateway.OcrEngineGateway;
import com.br.susreceita.evidence.domain.model.RecognitionPrescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.Base64;

@Service
@Slf4j
public class IAOcrEngineService implements OcrEngineGateway {

    private final ChatClient chatClient;

    public IAOcrEngineService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public RecognitionPrescription recognize(String image64) {
        byte[] imageBytes = Base64.getDecoder().decode(image64);
        var resource = new ByteArrayResource(imageBytes);
        try{
            return chatClient.prompt()
                    .user(u -> u.text("Extraia as informações deste documento de identidade.")
                            .media(MimeTypeUtils.IMAGE_JPEG, resource))
                    .call()
                    .entity(RecognitionPrescription.class);
        } catch(Exception e){
            log.error("Error recognizing prescription: {}", e.getMessage());
            return new RecognitionPrescription("", null, null, "");
        }
    }
}
