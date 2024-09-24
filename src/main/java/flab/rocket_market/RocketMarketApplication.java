package flab.rocket_market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class RocketMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(RocketMarketApplication.class, args);
	}

}
