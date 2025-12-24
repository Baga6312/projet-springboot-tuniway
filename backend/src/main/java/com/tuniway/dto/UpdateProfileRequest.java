package com.tuniway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String username;
    private String email;
    private String profilePicture; // Base64 encoded image with data URI prefix: "data:image/png;base64,..."
}