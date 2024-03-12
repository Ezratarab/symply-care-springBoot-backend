package com.example.symply_care.service;

import com.example.symply_care.dto.DoctorDTO;
import com.example.symply_care.dto.PatientDTO;
import com.example.symply_care.entity.*;
import com.example.symply_care.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Valid
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final InquiriesRepository inquiriesRepository;
    private final AppointmentsRepository appointmentsRepository;
    @Autowired
    @Lazy
    private PatientService patientService;

    public DoctorService(DoctorRepository doctorRepository, PatientRepository patientRepository, UsersRepository usersRepository, RoleRepository roleRepository, InquiriesRepository inquiriesRepository, AppointmentsRepository appointmentsRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.usersRepository=usersRepository;
        this.roleRepository=roleRepository;
        this.inquiriesRepository = inquiriesRepository;
        this.appointmentsRepository = appointmentsRepository;
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
            doctorDTO.setHmo(doctor.getHmo());
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
        doctor.setHmo(doctorDTO.getHmo());
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
    public DoctorDTO getDoctorByEmail(String email) {
        DoctorDTO doctorDTO = mapDoctorToDoctorDTO(doctorRepository.findByEmail(email).orElse(null));
        if (doctorDTO != null)
            return doctorDTO;
        throw new IllegalArgumentException("Doctor not found");
    }
    @Transactional
    public Doctor updateDoctorDetails(Doctor doctor, DoctorDTO doctorDTO) {
        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setCity(doctorDTO.getCity());
        doctor.setCountry(doctorDTO.getCountry());
        doctor.setStreet(doctorDTO.getStreet());
        doctor.setBirthDay(doctorDTO.getBirthDay());
        return doctor;
    }
    @Transactional
    public DoctorDTO updateDoctor(Long id, DoctorDTO doctorDTO) {
        if (doctorDTO == null || doctorDTO.getEmail() == null) {
            throw new IllegalArgumentException("Invalid Doctor data.");
        }
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        doctor = updateDoctorDetails(doctor,doctorDTO);
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
        Optional<Users> user = usersRepository.findByEmail(doctor.getEmail());
        usersRepository.delete(user.get());
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
        List<Doctor> patientDoctors = patient.getDoctors();
        patientDoctors.add(doctor);
        patient.setDoctors(patientDoctors);
        return patients.stream()
                .map(patientService::mapPatientToPatientDTO)
                .collect(Collectors.toList());
    }
    public Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.parse(dateString);
    }

    @Transactional
    public List<Inquiries> addInquiryToPatient(Long doctorID, Map<String, Object> inquiryData) {
        Map<String, Object> patientData = (Map<String, Object>) inquiryData.get("patient");
        Long patientID = ((Number) patientData.get("id")).longValue();

        String symptoms = (String) inquiryData.get("symptoms");

        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorID);
        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            Optional<Patient> optionalPatient = patientRepository.findById(patientID);
            if (optionalPatient.isPresent()) {
                Patient patient = optionalPatient.get();
                Inquiries inquiry = new Inquiries();
                inquiry.setDoctor(doctor);
                inquiry.setPatient(patient);
                inquiry.setSymptoms(symptoms);
                List<Inquiries> inquiries = doctor.getInquiries();
                List<Inquiries> patientInquiries = patient.getInquiries();
                patientInquiries.add(inquiry);
                patient.setInquiries(patientInquiries);
                inquiries.add(inquiry);
                doctor.setInquiries(inquiries);
                inquiriesRepository.save(inquiry);
                return inquiries;
            } else {
                throw new NoSuchElementException("Patient not found with id: " + optionalPatient.get().getId());
            }
        } else {
            throw new NoSuchElementException("Doctor not found with id: " + doctorID);
        }
    }

    @Transactional
    public List<Inquiries> addInquiryToDoctor(Long doctorID, Map<String, Object> inquiryData) {
        Map<String, Object> doctorData = (Map<String, Object>) inquiryData.get("doctor2");
        Long doctor2ID = ((Number) doctorData.get("id")).longValue();

        String symptoms = (String) inquiryData.get("symptoms");

        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorID);
        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            Optional<Doctor> optionalDoctor2 = doctorRepository.findById(doctor2ID);
            if (optionalDoctor2.isPresent()) {
                Doctor doctor2 = optionalDoctor2.get();
                Inquiries inquiry = new Inquiries();
                inquiry.setDoctor(doctor);
                inquiry.setDoctor2(doctor2);
                inquiry.setSymptoms(symptoms);
                List<Inquiries> inquiries = doctor.getInquiries();
                List<Inquiries> doctor2Inquiries = doctor2.getInquiries();
                doctor2Inquiries.add(inquiry);
                doctor2.setInquiries(doctor2Inquiries);
                inquiries.add(inquiry);
                doctor.setInquiries(inquiries);
                inquiriesRepository.save(inquiry);
                return inquiries;
            } else {
                throw new NoSuchElementException("Doctor2 not found with id: " + optionalDoctor2.get().getId());
            }
        } else {
            throw new NoSuchElementException("Doctor not found with id: " + doctorID);
        }
    }

    @Transactional
    public List<Appointments> addAppointmentToDoctor(Long doctorID, Map<String, Object> appointmentData) throws ParseException {
        Map<String, Object> patientData = (Map<String, Object>) appointmentData.get("patient");
        Long patientID = ((Number) patientData.get("id")).longValue();

        String date = (String) appointmentData.get("date");

        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorID);
        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            Optional<Patient> optionalPatient = patientRepository.findById(patientID);
            if (optionalPatient.isPresent()) {
                Patient patient = optionalPatient.get();
                Date now = new Date();
                Date date2 = convertStringToDate(date);
                List<Appointments> doctorAppointments = doctor.getAppointments();
                List<Appointments> patientAppointments = patient.getAppointments();
                for (Appointments patientAppointment : patientAppointments) {
                    if (convertStringToDate(patientAppointment.getDate()).equals(date2)) {
                        throw new NoSuchElementException("The patient already has an appointment on this date");
                    }
                }
                for (Appointments doctorAppointment : doctorAppointments) {
                    if (convertStringToDate(doctorAppointment.getDate()).equals(date2)) {
                        throw new NoSuchElementException("You already have an appointment on this date");
                    }
                }
                if (date2.after(now)) {
                    Appointments appointment = new Appointments();
                    appointment.setPatient(patient);
                    appointment.setDoctor(doctor);
                    appointment.setDate(date);
                    List<Appointments> appointments = patient.getAppointments();
                    appointments.add(appointment);
                    patient.setAppointments(appointments);
                    List<Appointments> appointments2 = doctor.getAppointments();
                    appointments2.add(appointment);
                    doctor.setAppointments(appointments2);
                    appointmentsRepository.save(appointment);
                    return appointments;
                } else {
                    throw new NoSuchElementException("The date has already passed");
                }
            } else {
                throw new NoSuchElementException("Patient not found with id: " + optionalPatient.get().getId());
            }
        } else {
            throw new NoSuchElementException("Doctor not found with id: " + doctorID);
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

    @Transactional
    public Users addRoleToDoctor(Long id, String roleName){
        Role role = roleRepository.findByRole(roleName);
        Optional<Doctor> doctor = doctorRepository.findById(id);
        Optional<Users> user = usersRepository.findByEmail(doctor.get().getEmail());
        List<Role> roles = user.get().getRoles();
        roles.add(role);
        user.get().setRoles(roles);
        Users userNew = user.get();
        if(user.isPresent()){
            usersRepository.save(userNew);}
        return userNew;
    }
    public void uploadImage(Long id,MultipartFile file) throws Exception {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        try {
            if (!file.isEmpty()) {
            doctor.get().setImageData(file.getBytes());
            doctorRepository.save(doctor.get());
            System.out.println("File uploaded successfully");
            }

        } catch (IOException ex) {
            throw new Exception("Could not store file " + file, ex);
        }
    }


}

