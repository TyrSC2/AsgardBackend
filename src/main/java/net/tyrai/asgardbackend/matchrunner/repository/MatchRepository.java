package net.tyrai.asgardbackend.matchrunner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface MatchRepository extends JpaRepository<Match, Long> {
	public List<Match> findAllByStatusOrderByCreatedDateAsc(String status);
	public List<Match> findAllByOrderByCreatedDateDesc();
}
