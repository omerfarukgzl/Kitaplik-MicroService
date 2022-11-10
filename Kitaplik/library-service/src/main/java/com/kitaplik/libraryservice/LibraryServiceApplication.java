package com.kitaplik.libraryservice;

import com.kitaplik.libraryservice.client.RetreiveMessageErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class LibraryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryServiceApplication.class, args);
	}
	/*
	Ya bu çalışacak

	@Bean
	public ErrorDecoder errorDecoder() {
		return new RetreiveMessageErrorDecoder();
	}*/

/*
	Ya da bu çalışacak */
	/*@Bean
	Logger.Level feignLoggerLevel() {//Full olsun bazen logların çok şişmesini istemeyip full değilde bazı logları yazırabilriz
		return Logger.Level.FULL;

	}*/

}
