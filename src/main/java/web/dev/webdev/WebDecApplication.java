package web.dev.webdev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WebDecApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebDecApplication.class, args);
	}

}
