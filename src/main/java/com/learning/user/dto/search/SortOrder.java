package com.learning.user.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SortOrder {

    private List<String> ascendingOrder;
    private List<String> descendingOrder;

}
