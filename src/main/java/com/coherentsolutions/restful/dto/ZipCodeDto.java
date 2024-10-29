// Server-side ZipCodeDto
package com.coherentsolutions.restful.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZipCodeDto {
    private String code;
}