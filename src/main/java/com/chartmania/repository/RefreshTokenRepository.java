package com.chartmania.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.chartmania.model.RefreshToken;

import jakarta.transaction.Transactional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long>{    
   
    /*@Query("DELETE FROM RefreshToken r where r.user.id = :userId")
    void deleteByUserId(long userId); */
    void deleteByToken(String token);

    RefreshToken findByToken(String token);


}
