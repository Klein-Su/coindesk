package com.cub;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.cub")
@EnableJpaRepositories( basePackages = {"com.cub.repo"} )
public class CoindeskApplication {

	public static void main(String[] args) {
		Locale.setDefault(Locale.TAIWAN);
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"));
		
		SpringApplication.run(CoindeskApplication.class, args);
	}

}
