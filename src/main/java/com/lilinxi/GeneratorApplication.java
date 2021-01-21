package com.lilinxi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * code-generator
 *
 * @author lilinxi lilinxi015@163.com
 */
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class GeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeneratorApplication.class, args);
    }
}
