package utec.reciscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ReciscoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReciscoreApplication.class, args);
    }

}
