package com.example.symply_care.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.util.Map;


@Data
public class RabbitMQMessage {
    private Long doctorId;
    private Long patientId;
    private Long doctor2Id;
    private String doctorAnswer;
    private String appointmentDate;
}
