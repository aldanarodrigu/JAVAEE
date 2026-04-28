package com.appchat.dto;

import jakarta.validation.constraints.NotBlank;

public class InvitacionDTO {
    
    @NotBlank
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}
