package com.myproject.toutiaonews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class ToutiaonewsApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ToutiaonewsApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ToutiaonewsApplication.class, args);
	}

}
