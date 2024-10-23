package com.coherentsolutions.restful.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zip_code_id", nullable = true)
    private ZipCode zipCode;

    @Column(nullable = false)
    private String sex;

    @Column(nullable = false)
    private int age;
}
