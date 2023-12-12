package com.example.symply_care.service;

import com.example.symply_care.dto.DoctorDTO;
import com.example.symply_care.dto.PatientDTO;
import com.example.symply_care.entity.Appointments;
import com.example.symply_care.entity.Doctor;
import com.example.symply_care.entity.Inquiries;
import com.example.symply_care.entity.Patient;
import com.example.symply_care.repository.DoctorRepository;
import com.example.symply_care.repository.PatientRepository;
import com.example.symply_care.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Valid
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    @Autowired
    @Lazy
    private PatientService patientService;

    public DoctorService(DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional

    public DoctorDTO mapDoctorToDoctorDTO(Doctor doctor) {
        if (doctor != null) {
            DoctorDTO doctorDTO = new DoctorDTO();
            doctorDTO.setId(doctor.getId());
            doctorDTO.setFirstName(doctor.getFirstName());
            doctorDTO.setLastName(doctor.getLastName());
            doctorDTO.setEmail(doctor.getEmail());
            doctorDTO.setCity(doctor.getCity());
            doctorDTO.setCountry(doctor.getCountry());
            doctorDTO.setStreet(doctor.getStreet());
            doctorDTO.setHMO(doctor.getHMO());
            doctorDTO.setExperience(doctor.getExperience());
            doctorDTO.setHospital(doctor.getHospital());
            doctorDTO.setSpecialization(doctor.getSpecialization());
            doctorDTO.setBirthDay(doctor.getBirthDay());
            doctorDTO.setImageData(doctor.getImageData());
            doctorDTO.setInquiriesList(doctor.getInquiries());
            doctorDTO.setPatients(doctor.getPatients());
            doctorDTO.setAppointments(doctor.getAppointments());
            return doctorDTO;
        }
        return null;
    }

    @Transactional

    public Doctor mapDoctorDTOToDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorDTO.getId());
        doctor.setPassword(doctorDTO.getPassword());
        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setEmail(doctorDTO.getEmail());
        doctor.setCity(doctorDTO.getCity());
        doctor.setCountry(doctorDTO.getCountry());
        doctor.setStreet(doctorDTO.getStreet());
        doctor.setHMO(doctorDTO.getHMO());
        doctor.setExperience(doctorDTO.getExperience());
        doctor.setHospital(doctorDTO.getHospital());
        doctor.setSpecialization(doctorDTO.getSpecialization());
        doctor.setBirthDay(doctorDTO.getBirthDay());
        doctor.setImageData(doctorDTO.getImageData());
        doctor.setInquiries(doctorDTO.getInquiriesList());
        doctor.setPatients(doctorDTO.getPatients());
        doctor.setAppointments(doctorDTO.getAppointments());
        return doctor;
    }

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = mapDoctorDTOToDoctor(doctorDTO);
        doctorRepository.save(doctor);
        return doctorDTO;
    }

    @Transactional

    public List<DoctorDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorDTO> doctorDTOS = new ArrayList<>();
        for (int i = 0; i < doctors.size(); i++) {
            doctorDTOS.add(mapDoctorToDoctorDTO(doctors.get(i)));
        }
        return doctorDTOS;
    }

    @Transactional

    public DoctorDTO getDoctorByID(Long id) {
        DoctorDTO doctorDTO = mapDoctorToDoctorDTO(doctorRepository.findById(id).orElse(null));
        if (doctorDTO != null)
            return doctorDTO;
        throw new IllegalArgumentException("Doctor not found");
    }

    @Transactional

    public DoctorDTO updateDoctor(Long id, DoctorDTO doctorDTO) {
        if (doctorDTO == null || doctorDTO.getEmail() == null) {
            throw new IllegalArgumentException("Invalid Doctor data.");
        }
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        doctor.setEmail(doctorDTO.getEmail());
        doctor = doctorRepository.save(doctor);
        return mapDoctorToDoctorDTO(doctor);
    }
    @Transactional

    public void deleteDoctor(Long id) throws Exception {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(id);
        if (!optionalDoctor.isPresent()) {
            throw new Exception("Doctor not found with ID: " + id);
        }
        Doctor doctor = optionalDoctor.get();
        doctorRepository.delete(doctor);
    }
    @Transactional
    public List<PatientDTO> getPatientsOfDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found with id: " + id));

        List<Patient> patients = doctor.getPatients();

        return patients.stream()
                .map(patientService::mapPatientToPatientDTO)
                .collect(Collectors.toList());
    }
    @Transactional

    public List<Inquiries> getInquiriesOfDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found with id: " + id));

        return doctor.getInquiries();
    }
    @Transactional

    public List<Appointments> getAppointmentsOfDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found with id: " + id));

        return doctor.getAppointments();
    }

    @Transactional
    public List<PatientDTO> addPatientToDoctor(Long doctorID, Long PatientID) {
        Doctor doctor = doctorRepository.findById(doctorID)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found with id: " + doctorID));
        Patient patient = patientRepository.findById(PatientID)
                .orElseThrow(() -> new NoSuchElementException("Patient not found with id: " + PatientID));

        List<Patient> patients = doctor.getPatients();
        patients.add(patient);
        doctor.setPatients(patients);
        return patients.stream()
                .map(patientService::mapPatientToPatientDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Appointments> addAppointmentToDoctor(Long doctorID, Appointments appointment) {
        Doctor doctor = doctorRepository.findById(doctorID)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found with id: " + doctorID));
        Date now = new Date();
        if (appointment.getDate().after(now)) {
            List<Appointments> appointments = doctor.getAppointments();
            appointments.add(appointment);
            doctor.setAppointments(appointments);
            return appointments;
        } else {
            throw new NoSuchElementException("The date has already passed");
        }
    }

}

