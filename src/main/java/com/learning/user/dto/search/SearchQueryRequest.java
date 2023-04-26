package com.learning.user.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.LinkedList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchQueryRequest {

    private LinkedList<SearchQuery> searchQueries;
    private int offset = 0;
    private int size = 10;
    private SortOrder sortOrder;

}
