package com.example.symply_care.controller;

import com.example.symply_care.dto.DoctorDTO;
import com.example.symply_care.dto.PatientDTO;
import com.example.symply_care.entity.*;
import com.example.symply_care.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor

public class PatientController {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private PatientService patientService;


    @GetMapping("/patients")
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }
    @GetMapping("/allInquiries")
    public ResponseEntity<List<Inquiries>> getAllInquiries(){
        return ResponseEntity.ok(patientService.getAllInquiries());
    }

    @PostMapping("/addPatient")
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) throws Exception {
        return ResponseEntity.ok(patientService.createPatient(patientDTO));
    }

    @GetMapping("/patient/I{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(patientService.getPatientByID(id));
    }
    @GetMapping("/patient/E{email}")
    public ResponseEntity<PatientDTO> getPatientByEmail(@PathVariable String email) throws Exception {
        return ResponseEntity.ok(patientService.getPatientByEmail(email));
    }

    @PutMapping("/updatePatient/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id, @RequestBody @Valid PatientDTO patientDTO) throws Exception {
        return ResponseEntity.ok(patientService.updatePatient(id, patientDTO));
    }

    @DeleteMapping("/deletePatient/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatientFromDoctors(id);
        try {
            patientService.deletePatient(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/patient/{id}/doctors")
    public ResponseEntity<List<DoctorDTO>> getDoctorsOfPatient(@PathVariable Long id){
        return ResponseEntity.ok(patientService.getDoctorsOfPatient(id));
    }

    @GetMapping("/patient/{id}/inquiries")
    public ResponseEntity<List<Inquiries>> getInquiriesOfPatient(@PathVariable Long id){
        return ResponseEntity.ok(patientService.getInquiriesOfPatient(id));
    }

    @GetMapping("/patient/{id}/appointments")
    public ResponseEntity<List<Appointments>> getAppointmentsOfPatient(@PathVariable Long id){
        return ResponseEntity.ok(patientService.getAppointmentsOfPatient(id));
    }

    @PostMapping("/patient/{patientID}/addDoctor")
    public ResponseEntity<List<DoctorDTO>> addDoctorToPatient(@PathVariable Long patientID,@RequestBody @Valid DoctorDTO doctor){
        return ResponseEntity.ok(patientService.addDoctorToPatient(patientID,doctor.getId()));
    }
    @PostMapping("/patient/{patientID}/addAppointment")
    public ResponseEntity<List<Appointments>> addAppointmentToPatient(@PathVariable Long patientID,@RequestBody Map<String, Object> appointmentData) throws ParseException {
        return ResponseEntity.ok(patientService.addAppointmentToPatient(patientID,appointmentData));
    }

    @PostMapping("/patient/{patientID}/addInquiry")
    public ResponseEntity<List<Inquiries>> addInquiryToPatient(
            @PathVariable Long patientID,
            @RequestBody Map<String, Object> inquiryData) {
        List<Inquiries> inquiries = patientService.addInquiryToPatient(patientID, inquiryData);
        return ResponseEntity.ok(inquiries);
    }

    @PostMapping("/patient/{patientID}/addRole")
    public ResponseEntity<Users> addRoleToPatient(@PathVariable Long patientID, @RequestBody @jakarta.validation.Valid String role){
        return ResponseEntity.ok(patientService.addRoleToPatient(patientID,role));
    }
    @PostMapping("/patient/{patientID}/addImage")
    public String uploadImage(@PathVariable Long patientID, @RequestParam("image") MultipartFile file) {
        System.out.println(file);
        try {
            patientService.uploadImage(patientID,file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/patient/{patientID}";
    }

}

