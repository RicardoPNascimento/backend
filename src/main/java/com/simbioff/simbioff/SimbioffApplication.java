package com.simbioff.simbioff;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;


@SpringBootApplication
//@EnableScheduling
public class SimbioffApplication {


    public static void main(String[] args) {
        SpringApplication.run(SimbioffApplication.class, args);
    }

    @Component
    public static class Runner implements ApplicationRunner {

        @Value("${spring.profiles.active}")
        private String activeProfile;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            if (activeProfile.equals("dev")) {
                System.out.println("Setting up datasource for DEV environment ");
                System.out.println("Documentation :  http://localhost:8080/swagger-ui/index.html#");
            } else if (activeProfile.equals("prod")) {
                System.out.println("Setting up datasource for PRODUCTION environment.");
                System.out.println("Documentation :  http://uu/swagger-ui/index.html#");
            }


        }
    }

}

