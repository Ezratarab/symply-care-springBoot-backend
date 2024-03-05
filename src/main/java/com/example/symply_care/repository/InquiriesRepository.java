package com.example.symply_care.repository;

import com.example.symply_care.entity.Inquiries;
import com.example.symply_care.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InquiriesRepository extends JpaRepository<Inquiries,Long> {
    Optional<Inquiries> findById(Long id);
}
