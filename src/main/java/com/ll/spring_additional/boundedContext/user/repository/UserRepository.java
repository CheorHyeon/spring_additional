package com.ll.spring_additional.boundedContext.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.spring_additional.boundedContext.user.entity.SiteUser;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
	Optional<SiteUser> findByUsername(String Username);

	Optional<SiteUser> findByusername(String username);
}