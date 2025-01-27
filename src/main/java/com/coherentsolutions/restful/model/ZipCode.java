package com.coherentsolutions.restful.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "zip_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZipCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @OneToMany(mappedBy = "zipCode", cascade = CascadeType.ALL)
    private Set<User> users;

    // Additional constructor
    public ZipCode(String code) {
        this.code = code;
    }
}
