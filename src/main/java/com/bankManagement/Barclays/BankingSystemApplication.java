package com.bankManagement.Barclays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class BankingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingSystemApplication.class, args);
	}

	@Bean
	public Docket swaggerConfiguration() {
		return new Docket(DocumentationType.SWAGGER_2).select().paths(PathSelectors.ant("/bank/*"))
				.apis(RequestHandlerSelectors.basePackage("com.bankManagement.Barclays")).build();
	}

}
