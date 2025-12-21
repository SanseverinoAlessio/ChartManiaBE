package com.chartmania.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(name = "expire_date",nullable = false,updatable = false)
    private Instant expiryDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;


    public RefreshToken(){}

    public RefreshToken(String token, Instant expiryDate, User user ){
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }


    public Instant getExpiryDate(){
        return this.expiryDate;
    }

    public User getUser(){
        return this.user;
    }

}
