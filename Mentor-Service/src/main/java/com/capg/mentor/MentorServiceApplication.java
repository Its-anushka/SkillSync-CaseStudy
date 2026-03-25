package com.capg.mentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class MentorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentorServiceApplication.class, args);
        System.out.println("Mentor");
    }

}
