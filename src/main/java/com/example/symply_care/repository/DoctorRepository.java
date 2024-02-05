package com.example.symply_care.repository;

import com.example.symply_care.entity.Doctor;
import com.example.symply_care.entity.User;
import com.example.symply_care.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>{
    Optional<Doctor> findByEmail(String email);

}
