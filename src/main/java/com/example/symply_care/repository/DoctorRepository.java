package com.example.symply_care.repository;

import com.example.symply_care.entity.Doctor;
import com.example.symply_care.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>{

}
