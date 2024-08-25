package net.tyrai.asgardbackend.matchrunner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface BotRepository extends JpaRepository<Bot, Long> {
	public Bot findByName(String name);
}
