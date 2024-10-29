// Server-side UploadUserDto
package com.coherentsolutions.restful.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadUserDto {
    private String name;
    private String email;
    private String sex;
    private ZipCodeDto zipCode;
    private int age;
}