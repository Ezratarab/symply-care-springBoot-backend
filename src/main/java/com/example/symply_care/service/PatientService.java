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
public class PatientService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    @Autowired
    @Lazy
    private DoctorService doctorService;


    public PatientService(PatientRepository patientRepository, DoctorRepository doctorRepository, UsersRepository usersRepository, RoleRepository roleRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.usersRepository = usersRepository;
        this.roleRepository=roleRepository;
    }

    @Transactional
    public PatientDTO mapPatientToPatientDTO(Patient patient) {
        if (patient != null) {
            PatientDTO patientDTO = new PatientDTO();
            patientDTO.setId(patient.getId());
            patientDTO.setFirstName(patient.getFirstName());
            patientDTO.setLastName(patient.getLastName());
            patientDTO.setEmail(patient.getEmail());
            patientDTO.setCity(patient.getCity());
            patientDTO.setCountry(patient.getCountry());
            patientDTO.setStreet(patient.getStreet());
            System.out.println(patient.getFirstName());
            patientDTO.setBirthDay(patient.getBirthDay());
            patientDTO.setImageData(patient.getImageData());
            patientDTO.setDoctors(patient.getDoctors());
            patientDTO.setInquiriesList(patient.getInquiries());
            patientDTO.setAppointments(patient.getAppointments());
            return patientDTO;
        }
        return null;
    }

    @Transactional
    public Patient mapPatientDTOToPatient(PatientDTO patientDTO) {
        Patient patient = new Patient();
        patient.setId(patientDTO.getId());
        patient.setFirstName(patientDTO.getFirstName());
        patient.setLastName(patientDTO.getLastName());
        patient.setEmail(patientDTO.getEmail());
        patient.setCity(patientDTO.getCity());
        patient.setCountry(patientDTO.getCountry());
        patient.setStreet(patientDTO.getStreet());
        patient.setBirthDay(patientDTO.getBirthDay());
        patient.setImageData(patientDTO.getImageData());
        patient.setPassword(patientDTO.getPassword());
        patient.setInquiries(patientDTO.getInquiriesList());
        patient.setDoctors(patientDTO.getDoctors());
        patient.setAppointments(patientDTO.getAppointments());
        return patient;
    }

    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO) {
        Patient patient = mapPatientDTOToPatient(patientDTO);
        patientRepository.save(patient);
        Users user = new Users();
        user.setId(patient.getId());
        user.setEmail(patient.getEmail());
        user.setPassword(patient.getPassword());
        List<Role> userRoles = user.getRoles();
        userRoles.add(roleRepository.findByRole("PATIENT"));
        user.setRoles(userRoles);
        usersRepository.save(user);
        return patientDTO;
    }
    @Transactional

    public List<PatientDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<PatientDTO> patientDTOS = new ArrayList<>();
        for (int i = 0; i < patients.size(); i++) {
            patientDTOS.add(mapPatientToPatientDTO(patients.get(i)));
        }
        return patientDTOS;
    }
    @Transactional

    public PatientDTO getPatientByID(Long id) {
        PatientDTO patientDTO = mapPatientToPatientDTO(patientRepository.findById(id).orElse(null));
        if (patientDTO != null)
            return patientDTO;
        throw new IllegalArgumentException("Patient not found");
    }
    @Transactional

    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        if (patientDTO == null || patientDTO.getEmail() == null) {
            throw new IllegalArgumentException("Invalid patient data.");
        }
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        patient.setEmail(patientDTO.getEmail());
        patient = patientRepository.save(patient);
        return mapPatientToPatientDTO(patient);
    }
    @Transactional
    public void deletePatient(Long id) throws Exception {
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (!optionalPatient.isPresent()) {
            throw new Exception("Patient not found with ID: " + id);
        }
        Patient patient = optionalPatient.get();
        deletePatientFromDoctors(id);
        patientRepository.delete(patient);
        Optional<Users> user = usersRepository.findById(id);
        usersRepository.delete(user.get());
    }
    @Transactional

    public List<DoctorDTO> getDoctorsOfPatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patient not found with id: " + id));

        List<Doctor> doctors = patient.getDoctors();

        return doctors.stream()
                .map(doctorService::mapDoctorToDoctorDTO)
                .collect(Collectors.toList());
    }
    @Transactional

    public List<Inquiries> getInquiriesOfPatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patient not found with id: " + id));

        return patient.getInquiries();
    }
    @Transactional

    public List<Appointments> getAppointmentsOfPatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patient not found with id: " + id));

        return patient.getAppointments();
    }
    @Transactional

    public Appointments createAppointment(Long id, Appointments appointment) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patient not found with id: " + id));

        List<Appointments> appointments = patient.getAppointments();
        appointments.add(appointment);
        patient.setAppointments(appointments);
        return appointment;
    }

    @Transactional
    public List<DoctorDTO> addDoctorToPatient(Long patientID, Long doctorID) {
        Patient patient = patientRepository.findById(patientID)
                .orElseThrow(() -> new NoSuchElementException("Patient not found with id: " + patientID));
        Doctor doctor = doctorRepository.findById(doctorID)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found with id: " + doctorID));
        List<Doctor> doctors = patient.getDoctors();
        doctors.add(doctor);
        patient.setDoctors(doctors);
       List<Patient> patients = doctor.getPatients();
       patients.add(patient);
       doctor.setPatients(patients);
        return doctors.stream().
                map(doctorService::mapDoctorToDoctorDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Appointments> addAppointmentToPatient(Long patientID, Appointments appointment) {
        Patient patient = patientRepository.findById(patientID)
                .orElseThrow(() -> new NoSuchElementException("Patient not found with id: " + patientID));
        Date now = new Date();
        if (appointment.getDate().after(now)) {
            List<Appointments> appointments = patient.getAppointments();
            appointments.add(appointment);
            patient.setAppointments(appointments);
            return appointments;
        } else {
            throw new NoSuchElementException("The date has already passed");
        }
    }

    @Transactional
    public List<Inquiries> addInquiryToPatient(Long patientID, Inquiries inquiry){
        Patient patient = patientRepository.findById(patientID)
                .orElseThrow(() -> new NoSuchElementException("Patient not found with id: " + patientID));
        List<Inquiries> inquiries = patient.getInquiries();
        inquiries.add(inquiry);
        patient.setInquiries(inquiries);
        return inquiries;
    }

    @Transactional
    public void deletePatientFromDoctors(Long patientId) {
        List<Doctor> doctors = doctorRepository.findAll();
        for (Doctor doctor : doctors) {
            doctorService.deletePatientFromDoctor(doctor.getId(), patientId);
        }
    }
    @Transactional
    public void deleteDoctorFromPatients(Long doctorId, Long patientId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);

        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            List<Doctor> doctors = patient.getDoctors();

            Iterator<Doctor> iterator = doctors.iterator();
            while (iterator.hasNext()) {
                Doctor doctor = iterator.next();
                if (doctor.getId().equals(doctorId)) {
                    iterator.remove();
                    break;
                }
            }
            patient.setDoctors(doctors);
        }
    }
}
