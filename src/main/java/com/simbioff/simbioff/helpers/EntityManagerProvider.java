package com.simbioff.simbioff.helpers;

import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EntityManagerProvider {
	INSTANCE;

	private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = getEntityManagerFactory();

	private static EntityManagerFactory getEntityManagerFactory() {

		String activeProfile = System.getenv("ENVIRONMENT");
		
		if (activeProfile.equals("dev")) {
			String hibernateConnectionUrl = Optional.ofNullable(System.getenv("DB_HOST_DEV"))
					.orElseThrow(() -> new RuntimeException("The DB_HOST variable is not defined"));

			var properties = Map.of("hibernate.connection.url", hibernateConnectionUrl, "hibernate.connection.username",
					System.getenv("DB_USER"), "hibernate.connection.password", System.getenv("DB_PASSWORD"));
			return Persistence.createEntityManagerFactory("simbioff", properties);
		} else if (activeProfile.equals("prod")) {
			String hibernateConnectionUrl = Optional.ofNullable(System.getenv("PGHOST"))
					.orElseThrow(() -> new RuntimeException("The PGHOST variable is not defined"));

			var properties = Map.of("hibernate.connection.url", hibernateConnectionUrl, "hibernate.connection.username",
					System.getenv("PGUSER"), "hibernate.connection.password", System.getenv("PGPASSWORD"));
			return Persistence.createEntityManagerFactory("simbioff", properties);
		} else {
			throw new RuntimeException("The active profile is not defined");
		}
	}

	public EntityManager getEntityManager() {
		return ENTITY_MANAGER_FACTORY.createEntityManager();
	}
}
