package com.example.demo;

import com.splunk.logging.SplunkCimLogEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.LogTags.*;

@EnableJpaRepositories("com.example.demo.model.persistence.repositories")
@EntityScan("com.example.demo.model.persistence")
@SpringBootApplication
public class SareetaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SareetaApplication.class, args);
    }

    @Bean
    List<SplunkCimLogEvent> appLogEvents() {
        List<SplunkCimLogEvent> events = new ArrayList<>();
        events.add(new SplunkCimLogEvent("create_user_success", CREATE_USER_SUCCESS.toString()));
        events.add(new SplunkCimLogEvent("create_user_failure", CREATE_USER_FAILURE.toString()));
        events.add(new SplunkCimLogEvent("order_request_success", ORDER_REQUEST_SUCCESS.toString()));
        events.add(new SplunkCimLogEvent("order_request_failure", ORDER_REQUEST_FAILURE.toString()));
        events.add(new SplunkCimLogEvent("app_exception", APP_EXCEPTION.toString()));
        return events;
    }

}
