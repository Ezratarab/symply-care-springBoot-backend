package com.example.symply_care.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.util.Map;


@Data
public class RabbitMQMessage {
    private String doctorEmail;
    private String patientEmail;
    private String doctor2Email;
    private String senderInquiryEmail;
    private String question;
    private String doctorAnswer;
    private String appointmentDate;
    //for contect us
    private String Email;
    private String Message;
    private String AdminEmail = "ezra5385@gmail.com";
}
