package com.learning.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements Serializable {

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private String emailId;

    @JsonProperty
    private String password;

    @JsonProperty
    private Integer userType;

    @JsonProperty
    private boolean isSuperAdmin;
}
