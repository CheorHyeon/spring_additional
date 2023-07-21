package com.ll.spring_additional.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
	Optional<SiteUser> findByUsername(String Username);

	Optional<SiteUser> findByusername(String username);
}