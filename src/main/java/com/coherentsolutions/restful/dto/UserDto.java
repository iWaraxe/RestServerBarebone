package com.coherentsolutions.restful.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String name;
    private String email;
    private String sex;
    private Integer age;
    private String zipCode;
}
