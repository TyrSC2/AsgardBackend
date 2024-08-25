package net.tyrai.asgardbackend.matchrunner.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface BuildRepository extends JpaRepository<Build, Long> {
	public List<Build> findByBot(String bot);
	public List<Build> findByIdIn(Collection<Integer> ids);
	
}
