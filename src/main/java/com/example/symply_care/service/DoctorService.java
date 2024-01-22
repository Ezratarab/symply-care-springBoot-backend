package com.example.symply_care.service;

import com.example.symply_care.dto.DoctorDTO;
import com.example.symply_care.dto.PatientDTO;
import com.example.symply_care.entity.*;
import com.example.symply_care.repository.DoctorRepository;
import com.example.symply_care.repository.PatientRepository;
import com.example.symply_care.repository.RoleRepository;
import com.example.symply_care.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Valid
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    @Autowired
    @Lazy
    private PatientService patientService;

    public DoctorService(DoctorRepository doctorRepository, PatientRepository patientRepository, UsersRepository usersRepository, RoleRepository roleRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.usersRepository=usersRepository;
        this.roleRepository=roleRepository;
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
            doctorDTO.setStreet(doctor.getStreet());
            doctorDTO.setCountry(doctor.getCountry());
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
        doctor.setInquiries(doctorDTO.getInquiriesList());
        doctor.setPatients(doctorDTO.getPatients());
        doctor.setAppointments(doctorDTO.getAppointments());
        return doctor;
    }

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = mapDoctorDTOToDoctor(doctorDTO);
        doctorRepository.save(doctor);
        Users user = new Users();
        user.setId(doctor.getId());
        user.setEmail(doctor.getEmail());
        user.setPassword(doctor.getPassword());
        List<Role> userRoles = user.getRoles();
        userRoles.add(roleRepository.findByRole("DOCTOR"));
        user.setRoles(userRoles);
        usersRepository.save(user);
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
        deleteDoctorFromPatients(id);
        doctorRepository.delete(doctor);
        Optional<Users> user = usersRepository.findById(doctor.getId());
        usersRepository.delete(user.get());
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

    @Transactional
    public void deletePatientFromDoctor(Long doctorId, Long patientId) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);

        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            List<Patient> patients = doctor.getPatients();

            Iterator<Patient> iterator = patients.iterator();
            while (iterator.hasNext()) {
                Patient patient = iterator.next();
                if (patient.getId().equals(patientId)) {
                    iterator.remove();
                    break;
                }
            }
            doctor.setPatients(patients);
        }
    }
    @Transactional
    public void deleteDoctorFromPatients(Long id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        List<Patient> patients = doctor.get().getPatients();

        for (Patient patient : patients) {
            patientService.deleteDoctorFromPatients(doctor.get().getId(),patient.getId());
        }

    }



}

