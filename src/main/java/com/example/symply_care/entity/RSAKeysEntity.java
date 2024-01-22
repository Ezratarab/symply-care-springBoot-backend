package com.example.symply_care.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RSAKeysEntity {

    @Id
    private Integer kid; // Key ID

    @Lob // Large Object - for storing large data
    @Column(name = "`private_key`", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] private_key; // The private key, stored in binary format

    @Lob // Large Object - for storing large data
    @Column(name = "`public_key`", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] public_key; // The private key, stored in binary format

    public RSAKeysEntity(Integer kid, byte[] privateKey, byte[] publicKey) {
        this.kid = kid;
        this.private_key = privateKey;
        this.public_key = publicKey;
    }


}
