package net.tyrai.asgardbackend.matchrunner.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface BotVersionRepository extends JpaRepository<BotVersion, Long> {
	public List<BotVersion> findByBot(String bot);
	public List<BotVersion> findByIdIn(Collection<Integer> ids);
}
