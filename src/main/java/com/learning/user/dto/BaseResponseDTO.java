package com.learning.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponseDTO<T> {
    private int status;
    private String message;
    private T data;
}
