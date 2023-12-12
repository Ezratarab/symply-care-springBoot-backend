package com.example.symply_care.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Embeddable
public class Prediction {

    private String disease;
    private double percentage;
}
