package com.example.symply_care.repository;

import com.example.symply_care.entity.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentsRepository extends JpaRepository<Appointments,Long> {
    Optional<Appointments> findById(Long id);
}