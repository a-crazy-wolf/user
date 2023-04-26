package com.learning.user.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoinColumnProps {
    private String joinColumnName;
    private SearchFilter searchFilter;
}
