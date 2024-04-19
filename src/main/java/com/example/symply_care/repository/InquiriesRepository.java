package com.example.symply_care.repository;

import com.example.symply_care.entity.Inquiries;
import com.example.symply_care.entity.Patient;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InquiriesRepository extends JpaRepository<Inquiries,Long> {
    Optional<Inquiries> findById(Long id);
    List<Inquiries> findByCreatedAtBefore(LocalDateTime date);

}
