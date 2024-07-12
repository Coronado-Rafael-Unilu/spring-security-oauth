package com.baeldung.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import com.baeldung.auth.config.KeycloakServerProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties({ KeycloakServerProperties.class })
public class AuthorizationServerApp {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationServerApp.class);

	public static void main(String[] args) throws Exception {
		populateTestDatabase();
		SpringApplication.run(AuthorizationServerApp.class, args);
	}

	public static void populateTestDatabase() throws Exception {
		DataSource ds =  DataSourceBuilder.create()
				.driverClassName("org.h2.Driver")
				.url("jdbc:h2:./target/customuser")
				.username("SA")
				.password("")
				.build();

		ResourceDatabasePopulator populator = new ResourceDatabasePopulator(false, false, "UTF-8",
				new ClassPathResource("custom-database.sql"));
		populator.execute(ds);

	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> onApplicationReadyEventListener(ServerProperties serverProperties,
			KeycloakServerProperties keycloakServerProperties) {

		return (evt) -> {

			Integer port = serverProperties.getPort();
			String keycloakContextPath = keycloakServerProperties.getContextPath();

			LOG.info("Embedded Keycloak started: http://localhost:{}{} to use keycloak", port, keycloakContextPath);
		};
	}

}
