package com.br.susreceita.prescription;

import com.br.susreceita.prescription.infrastructure.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class PrescriptionApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrescriptionApplication.class, args);
	}

}

