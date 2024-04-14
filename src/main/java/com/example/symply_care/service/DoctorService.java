package com.example.symply_care.service;

import com.example.symply_care.controller.RabbitMQController;
import com.example.symply_care.dto.DoctorDTO;
import com.example.symply_care.dto.DoctorShortDTO;
import com.example.symply_care.dto.PatientDTO;
import com.example.symply_care.entity.*;
import com.example.symply_care.repository.*;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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
    private final RabbitMQController rabbitMQController;
    private final AppointmentsRepository appointmentsRepository;
    @Autowired
    @Lazy
    private PatientService patientService;
    public static final String FLASK_SERVER_URL = "http://localhost:8500";
    private final RestTemplate restTemplate = new RestTemplate();

    public DoctorService(DoctorRepository doctorRepository, PatientRepository patientRepository, UsersRepository usersRepository, RoleRepository roleRepository, InquiriesRepository inquiriesRepository, RabbitMQController rabbitMQController, AppointmentsRepository appointmentsRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.inquiriesRepository = inquiriesRepository;
        this.rabbitMQController = rabbitMQController;
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
    public DoctorShortDTO mapDoctorToDoctorShortDTO(Doctor doctor) {
        if (doctor != null) {
            DoctorShortDTO doctorShortDTO = new DoctorShortDTO();
            doctorShortDTO.setFirstName(doctor.getFirstName());
            doctorShortDTO.setLastName(doctor.getLastName());
            doctorShortDTO.setEmail(doctor.getEmail());
            doctorShortDTO.setHmo(doctor.getHmo());
            doctorShortDTO.setExperience(doctor.getExperience());
            doctorShortDTO.setHospital(doctor.getHospital());
            doctorShortDTO.setSpecialization(doctor.getSpecialization());
            doctorShortDTO.setImageData(doctor.getImageData());
            return doctorShortDTO;
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
    public List<DoctorShortDTO> getAllShortDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorShortDTO> doctorShortDTOS = new ArrayList<>();
        for (int i = 0; i < doctors.size(); i++) {
            doctorShortDTOS.add(mapDoctorToDoctorShortDTO(doctors.get(i)));
        }
        return doctorShortDTOS;
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
    public String deleteAppointment(Long id) throws Exception {
        Optional<Appointments> optionalAppointment = appointmentsRepository.findById(id);
        if (!optionalAppointment.isPresent()) {
            throw new Exception("Appointment not found with ID: " + id);
        }
        Patient patient = optionalAppointment.get().getPatient();
        List<Appointments> patientAppointments = patient.getAppointments();
        patientAppointments.remove(optionalAppointment.get());
        patient.setAppointments(patientAppointments);
        Doctor doctor = optionalAppointment.get().getDoctor();
        List<Appointments> doctorAppointments = doctor.getAppointments();
        doctorAppointments.remove(optionalAppointment.get());
        doctor.setAppointments(doctorAppointments);
        appointmentsRepository.delete(optionalAppointment.get());
        return "Appointment deleted successfully";
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

                List<Doctor> doctors = new ArrayList<>();
                doctors.add(doctor);
                inquiry.setDoctor(doctors);

                inquiry.setPatient(patient);
                inquiry.setSenderId(doctorID);
                inquiry.setSymptoms(symptoms);

                // Save the inquiry
                Inquiries savedInquiry = inquiriesRepository.save(inquiry);
                List<Inquiries> doctorsInquiries = doctor.getInquiries();
                doctorsInquiries.add(inquiry);
                doctor.setInquiries(doctorsInquiries);
                doctorRepository.save(doctor);
                List<Inquiries> patientInquiries = patient.getInquiries();
                patientInquiries.add(savedInquiry);
                patient.setInquiries(patientInquiries);
                patientRepository.save(patient);
                RabbitMQMessage rabbitMQMessage = new RabbitMQMessage();
                rabbitMQMessage.setDoctorEmail(doctor.getEmail());
                rabbitMQMessage.setSenderInquiryEmail(doctor.getEmail());
                rabbitMQMessage.setQuestion(inquiry.getSymptoms());
                if(inquiry.getPatient()!=null){
                    rabbitMQMessage.setPatientEmail(inquiry.getPatient().getEmail());
                }
                rabbitMQController.sendMessage(rabbitMQMessage);
                return patientInquiries;
            } else {
                throw new NoSuchElementException("Patient not found with id: " + patientID);
            }
        } else {
            throw new NoSuchElementException("Doctor not found with id: " + doctorID);
        }
    }

    @Transactional
    public List<Inquiries> addInquiryToDoctor(Long doctorID, Map<String, Object> inquiryData) {
        Map<String, Object> doctorData = (Map<String, Object>) inquiryData.get("doctor2");
        String doctor2Email = null;
        Object doctor2EmailObject = doctorData.get("email");
        if (doctor2EmailObject != null) {
            doctor2Email = (String) doctor2EmailObject;
        } else {
            // Handle the case where the doctor2 ID is null
            throw new IllegalArgumentException("Doctor2 Email cannot be null");
        }

        String symptoms = (String) inquiryData.get("symptoms");
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorID);
        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            Optional<Doctor> optionalDoctor2 = doctorRepository.findByEmail(doctor2Email);
            if (optionalDoctor2.isPresent()) {
                Doctor doctor2 = optionalDoctor2.get();
                Inquiries inquiry = new Inquiries();
                List<Doctor> doctors =  new ArrayList<>();
                doctors.add(doctor);
                inquiry.setDoctor(doctors);
                List<Doctor> doctors2 =  new ArrayList<>();
                doctors2.add(doctor2);
                inquiry.setDoctor2(doctors2);
                inquiry.setSymptoms(symptoms);
                inquiry.setSenderId(doctorID);
                inquiriesRepository.save(inquiry);
                List<Inquiries> doctorsInquiries = doctor.getInquiries();
                doctorsInquiries.add(inquiry);
                doctor.setInquiries(doctorsInquiries);
                doctorRepository.save(doctor);
                List<Inquiries> doctors2Inquiries = doctor2.getInquiries();
                doctors2Inquiries.add(inquiry);
                doctor2.setInquiries(doctors2Inquiries);
                doctorRepository.save(doctor2);
                RabbitMQMessage rabbitMQMessage = new RabbitMQMessage();
                rabbitMQMessage.setDoctorEmail(doctor.getEmail());
                rabbitMQMessage.setSenderInquiryEmail(doctor.getEmail());
                rabbitMQMessage.setQuestion(inquiry.getSymptoms());
                rabbitMQMessage.setDoctor2Email(doctor2.getEmail());
                rabbitMQController.sendMessage(rabbitMQMessage);
                return doctor.getInquiries();
            } else {
                throw new NoSuchElementException("Doctor2 not found with email: " + doctor2Email);
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
                    RabbitMQMessage rabbitMQMessage = new RabbitMQMessage();
                    rabbitMQMessage.setAppointmentDate(date);
                    rabbitMQMessage.setDoctorEmail(doctor.getEmail());
                    rabbitMQMessage.setPatientEmail(patient.getEmail());
                    rabbitMQController.sendMessage(rabbitMQMessage);
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
    public void answerInquiry(Long inquiryId, String answer) throws Exception {
        Optional<Inquiries> optionalInquiry = inquiriesRepository.findById(inquiryId);
        if (!optionalInquiry.isPresent()) {
            throw new Exception("Inquiry not found with ID: " + inquiryId);
        }
        Inquiries inquiry = optionalInquiry.get();
        inquiry.setAnswer(answer);
        inquiry.setHasAnswered(true);

        RabbitMQMessage rabbitMQMessage = new RabbitMQMessage();
        rabbitMQMessage.setQuestion(inquiry.getSymptoms());
        rabbitMQMessage.setDoctorAnswer(answer);
        Doctor doctor = inquiry.getDoctor().get(0);
        rabbitMQMessage.setDoctorEmail(doctor.getEmail());
        if(!inquiry.getDoctor2().isEmpty()){
            Doctor doctor2 = inquiry.getDoctor2().get(0);
            inquiry.setSenderId(doctor2.getId());
            rabbitMQMessage.setSenderInquiryEmail(doctor2.getEmail());
            rabbitMQMessage.setDoctor2Email(doctor2.getEmail());
        }
        else if(inquiry.getPatient() != null){
            Patient patient = inquiry.getPatient();
            rabbitMQMessage.setSenderInquiryEmail(doctor.getEmail());
            rabbitMQMessage.setPatientEmail(patient.getEmail());
        }
        inquiriesRepository.save(inquiry);
        rabbitMQController.sendMessage(rabbitMQMessage);
    }


    @Transactional
    public ResponseEntity<String> sendTextToFlaskServer(Long inquiryId) throws Exception {
        System.out.println("Sending text to Flask server");
        Optional<Inquiries> optionalInquiry = inquiriesRepository.findById((inquiryId));
        if (!optionalInquiry.isPresent()) {
            throw new Exception("Inquiry not found with ID: " + inquiryId);
        }
        Inquiries inquiry = optionalInquiry.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("symptoms", inquiry.getSymptoms());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(FLASK_SERVER_URL + "/predict", requestEntity, String.class);


        System.out.println(response.getBody());
        JSONObject responseJson = new JSONObject(response.getBody());
        int maxIndex = responseJson.getInt("max_index");
        String matchingRowJson = responseJson.optString("matching_row");
        int rowNumber = responseJson.getInt("row_number");
        String matchingDisease = responseJson.optString("mainDisease");

        Prediction prediction = new Prediction();
        prediction.setMaxIndex(maxIndex);
        prediction.setMatchingRow(matchingRowJson);
        prediction.setPredictionRowNumber(rowNumber);
        prediction.setMatchingDisease(matchingDisease);
        optionalInquiry.get().setPrediction(prediction);
        return response;
    }

}

