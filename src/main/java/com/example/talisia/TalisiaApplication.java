package com.example.talisia;

import com.example.talisia.service.InitService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TalisiaApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TalisiaApplication.class, args);
//		MainService bean = context.getBean(MainService.class);
//		bean.main();

		InitService gtpInitService = context.getBean(InitService.class);
		gtpInitService.temp();

	}

}
