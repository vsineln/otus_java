package ru.otus.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.otus.homework.config.DbClientsProperties;
import ru.otus.homework.config.FeClientsProperties;

@SpringBootApplication
@EnableConfigurationProperties({DbClientsProperties.class, FeClientsProperties.class})
public class MsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsApplication.class, args);
    }
}
