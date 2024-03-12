package com.example.symply_care.repository;


import com.example.symply_care.entity.RSAKeysEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RSAKeysRepository extends JpaRepository<RSAKeysEntity, Integer> {

    RSAKeysEntity findTopByOrderByKidDesc();

}
