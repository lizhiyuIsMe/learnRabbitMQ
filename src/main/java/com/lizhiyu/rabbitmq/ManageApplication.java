package com.lizhiyu.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManageApplication {
    public static Logger logger = LoggerFactory.getLogger(ManageApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ManageApplication.class, args);
    }

}

