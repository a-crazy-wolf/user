package com.learning.user.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchFilter {

    private String property;
    private String operator;
    private Object value;

}
