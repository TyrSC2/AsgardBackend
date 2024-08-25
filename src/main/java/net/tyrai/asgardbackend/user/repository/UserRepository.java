package net.tyrai.asgardbackend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> 
{
	boolean existsUserByEmail(String email);
	void deleteById(Long id);
	User findByEmail(String email);
}
