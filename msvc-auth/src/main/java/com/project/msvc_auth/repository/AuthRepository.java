package com.project.msvc_auth.repository;

import com.project.msvc_auth.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByUsername(String username);

}