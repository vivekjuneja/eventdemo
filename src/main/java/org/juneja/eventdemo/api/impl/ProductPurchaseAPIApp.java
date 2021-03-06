package org.juneja.eventdemo.api.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProductPurchaseAPIApp {

	
	@Value("${isSubscriptionConfirmed}")
	private  boolean isSubscriptionConfirmed;
	
	@Value("${uriForDataAPI}")
	private  String uriForDataAPI;

	
	public static void main(String[] args) {
		
		SpringApplication.run(ProductPurchaseAPIApp.class, args);
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
		return factory;
	}

}
