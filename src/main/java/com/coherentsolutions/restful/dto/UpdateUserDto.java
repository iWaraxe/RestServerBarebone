package com.coherentsolutions.restful.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDto {
    private UserDto userNewValues;
    private UserDto userToChange;
}
