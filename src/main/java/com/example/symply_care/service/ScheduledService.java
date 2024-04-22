package com.example.symply_care.service;

import com.example.symply_care.entity.Appointments;
import com.example.symply_care.entity.Doctor;
import com.example.symply_care.entity.Inquiries;
import com.example.symply_care.entity.Patient;
import com.example.symply_care.repository.AppointmentsRepository;
import com.example.symply_care.repository.DoctorRepository;
import com.example.symply_care.repository.InquiriesRepository;
import com.example.symply_care.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableScheduling
public class ScheduledService {
    AppointmentsRepository appointmentsRepository;
    PatientRepository patientRepository;
    DoctorRepository doctorRepository;
    InquiriesRepository inquiriesRepository;

    public ScheduledService(AppointmentsRepository appointmentsRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, InquiriesRepository inquiriesRepository) {
        this.appointmentsRepository = appointmentsRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.inquiriesRepository = inquiriesRepository;
    }

    @Transactional
    @Scheduled(cron = "0 44 15 * * ?")
    public void deleteOldAppointments() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<Appointments> allAppointments = appointmentsRepository.findAll();
        List<Appointments> oldAppointments = new ArrayList<>();

        for (Appointments appointment : allAppointments) {
            LocalDateTime appointmentDateTime = LocalDateTime.parse(appointment.getDate(), formatter);
            if (appointmentDateTime.isBefore(currentDateTime)) {
                oldAppointments.add(appointment);
            }
        }

        for (Appointments appointment : oldAppointments) {
            Patient patient = appointment.getPatient();
            List<Appointments> patientAppointments = patient.getAppointments();
            patientAppointments.remove(appointment);
            patient.setAppointments(patientAppointments);
            patientRepository.save(patient);

            Doctor doctor = appointment.getDoctor();
            List<Appointments> doctorAppointments = doctor.getAppointments();
            doctorAppointments.remove(appointment);
            doctor.setAppointments(doctorAppointments);
            doctorRepository.save(doctor);

            appointmentsRepository.delete(appointment);
        }
    }
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldInquiries() {
        LocalDateTime threeWeeksAgo = LocalDateTime.now().minus(3, ChronoUnit.WEEKS);

        List<Inquiries> oldInquiries = inquiriesRepository.findByCreatedAtBefore(threeWeeksAgo);

        for (Inquiries inquiry : oldInquiries) {
            if (!inquiry.getHasAnswered()) {
                if (inquiry.getPatient() != null) {
                    Patient patient = inquiry.getPatient();
                    List<Inquiries> patientInquiries = patient.getInquiries();
                    patientInquiries.remove(inquiry);
                    patient.setInquiries(patientInquiries);
                    patientRepository.save(patient);
                } else if (inquiry.getDoctor2() != null) {
                    List<Doctor> doctor2List = inquiry.getDoctor2();
                    Doctor doctor2 = doctor2List.get(0);
                    List<Inquiries> doctor2Inquiries = doctor2.getInquiries();
                    doctor2Inquiries.remove(inquiry);
                    doctor2.setInquiries(doctor2Inquiries);
                    doctorRepository.save(doctor2);
                }
                List<Doctor> doctorList = inquiry.getDoctor();
                Doctor doctor = doctorList.get(0);
                List<Inquiries> doctorInquiries = doctor.getInquiries();
                doctorInquiries.remove(inquiry);
                doctor.setInquiries(doctorInquiries);
                doctorRepository.save(doctor);
                inquiriesRepository.delete(inquiry);
            }
        }
    }



}
