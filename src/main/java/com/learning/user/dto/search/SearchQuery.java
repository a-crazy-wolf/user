package com.learning.user.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchQuery {

    private String parentOperator = "OR";
    private List<SearchFilter> searchFilter;
    private List<JoinColumnProps> joinColumnProps;
    private String childOperator;

}
