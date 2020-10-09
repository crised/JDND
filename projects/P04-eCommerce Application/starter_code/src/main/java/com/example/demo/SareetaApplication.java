package com.example.demo;

import com.splunk.logging.SplunkCimLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.Arrays;
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
    String[] appLogEvents() {
        String[] events =
                {new SplunkCimLogEvent("create_user_success",
                        String.valueOf(CREATE_USER_SUCCESS.ordinal())).toString(),
                        new SplunkCimLogEvent("create_user_failure",
                                String.valueOf(CREATE_USER_FAILURE.ordinal())).toString(),
                        new SplunkCimLogEvent("order_request_success",
                                String.valueOf(ORDER_REQUEST_SUCCESS.ordinal())).toString(),
                        new SplunkCimLogEvent("order_request_failure",
                                String.valueOf(ORDER_REQUEST_FAILURE.ordinal())).toString(),
                        new SplunkCimLogEvent("app_exception",
                                String.valueOf(APP_EXCEPTION.ordinal())).toString()};
        return events;
    }

    @Bean
    Logger SplunkLogger() {
        Logger logger = LoggerFactory.getLogger("splunk.logger");
        logger.info("Created logger!");
        return logger;
    }

}
