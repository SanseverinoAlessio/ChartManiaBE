package com.chartmania.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.chartmania.model.User;


public interface UserRepository extends JpaRepository<User,Long>{
   User findByEmail(String email);
   User findByUsername(String username);
}
