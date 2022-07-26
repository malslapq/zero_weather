package com.zero_weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class ZeroWeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeroWeatherApplication.class, args);
    }

}
