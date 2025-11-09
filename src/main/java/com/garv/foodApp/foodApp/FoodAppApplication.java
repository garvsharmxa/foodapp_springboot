package com.garv.foodApp.foodApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@ComponentScan(excludeFilters = @ComponentScan.Filter(
		type = FilterType.REGEX,
		pattern = "com\\.garv\\.foodApp\\.foodApp\\.mappers\\..*"
))
public class FoodAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(FoodAppApplication.class, args);
	}
}

