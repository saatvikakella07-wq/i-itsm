package com.itsm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the I-ITSM (Intelligent IT Service Management System) application.
 */
@SpringBootApplication
@EnableScheduling
public class ItsmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItsmApplication.class, args);
    }
}
