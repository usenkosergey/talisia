package com.example.talisia;

import com.example.talisia.service.MainService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TalisiaApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TalisiaApplication.class, args);
		MainService bean = context.getBean(MainService.class);
		bean.main();

	}

}
