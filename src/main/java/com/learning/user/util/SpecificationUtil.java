package com.learning.user.util;

import com.learning.user.dto.search.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SpecificationUtil {

    private static Map<String, Object> dateMap = new HashMap<>();

    static {
        dateMap.put("java.time.LocalDateTime", LocalDateTime.now());
        dateMap.put("java.time.LocalDate", LocalDate.now());
        dateMap.put("java.util.Date", new Date());
    }

    public static <T> Specification<T> prepareSearchQuery(SearchQueryRequest searchQueryRequest, Class<T> classType) {
        List<SearchQuery> searchQueries = searchQueryRequest.getSearchQueries();
        if (!CollectionUtils.isEmpty(searchQueries)) {
            SearchQuery parentSearchQuery = searchQueries.get(0);
            Specification specification = Specification.where(bySearchQuery(parentSearchQuery, classType));
            searchQueries.remove(parentSearchQuery);

            for (final SearchQuery searchQuery : searchQueries) {
                String parentOperator = searchQuery.getParentOperator().toUpperCase();
                QueryOperator queryOperator = QueryOperator.valueOf(parentOperator);
                if (!StringUtils.isEmpty(parentOperator)) {
                    switch (queryOperator) {
                        case OR:
                            specification = specification.or(bySearchQuery(searchQuery, classType));
                            break;
                        case AND:
                            specification = specification.and(bySearchQuery(searchQuery, classType));
                            break;
                        default:
                            throw new IllegalArgumentException(parentOperator + " is not valid predicate.");
                    }
                }
            }
            return specification;
        }
        return Specification.where(bySearchQuery(searchQueries.get(0), classType));
    }

    public static <T> Specification<T> bySearchQuery(SearchQuery searchQuery, Class<T> classType) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            List<SearchFilter> searchFilters = searchQuery.getSearchFilter();

            if (!CollectionUtils.isEmpty(searchFilters)) {
                searchFilters.stream().forEach(searchFilter -> {
                    addPredicates(predicates, searchFilter, criteriaBuilder, root);
                });
            }

            if (CollectionUtils.isEmpty(predicates)) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private static <T> void addJoinColumnProps(List<Predicate> predicates, JoinColumnProps joinColumnProp,
                                               CriteriaBuilder criterailBuilder, Root<T> root) {
        SearchFilter searchFilter = joinColumnProp.getSearchFilter();
        Join<Object, Object> joinParent = root.join(joinColumnProp.getJoinColumnName());

        String property = searchFilter.getProperty();
        Path expression = joinParent.get(property);

        addPredicate(predicates, searchFilter, criterailBuilder, expression);
    }

    private static <T> void addPredicates(List<Predicate> predicates, SearchFilter searchFilter,
                                          CriteriaBuilder criteriaBuilder, Root<T> root) {
        String property = searchFilter.getProperty();
        Path expression = root.get(property);
        addPredicate(predicates, searchFilter, criteriaBuilder, expression);
    }

    private static <T> void addPredicate(List<Predicate> predicates, SearchFilter searchFilter,
                                         CriteriaBuilder criteriaBuilder, Path expression) {
        boolean isDateProperty = isDateProperty(expression);
        QueryOperator queryOperator = QueryOperator.valueOf(searchFilter.getOperator().toUpperCase());
        switch (queryOperator) {
            case EQUALS:
                if (isDateProperty) {
                    convertToDateType(searchFilter, expression);
                }
                predicates.add(criteriaBuilder.equal(expression, searchFilter.getValue()));
                break;
            case IN:
                predicates.add(criteriaBuilder.in(expression).value(searchFilter.getValue()));
                break;
            case LIKE:
                predicates.add(criteriaBuilder.like(expression, "%" + searchFilter.getValue() + "%"));
                break;
            case GREATER_THAN:
                if (isDateProperty) convertToDateType(searchFilter, expression);
                predicates.add(criteriaBuilder.greaterThan(expression, (Comparable) searchFilter.getValue()));
                break;
            case LESS_THAN:
                if (isDateProperty) convertToDateType(searchFilter, expression);
                predicates.add(criteriaBuilder.lessThan(expression, (Comparable) searchFilter.getValue()));
                break;
            case GREATER_THAN_EQ:
                if (isDateProperty) convertToDateType(searchFilter, expression);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(expression, (Comparable) searchFilter.getValue()));
                break;
            case LESS_THAN_EQ:
                if (isDateProperty) convertToDateType(searchFilter, expression);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(expression, (Comparable) searchFilter.getValue()));
                break;
            case NOT_EQ:
                if (isDateProperty) convertToDateType(searchFilter, expression);
                predicates.add(criteriaBuilder.notEqual(expression, searchFilter.getValue()));
                break;
            case BETWEEN:
                if (isDateProperty) {
                    setForDateBetween(predicates, criteriaBuilder, searchFilter, expression);
                } else {
                    predicates.add(criteriaBuilder.isNotNull(expression));
                }
                break;
            case IS_NULL:
                predicates.add(criteriaBuilder.isNull(expression));
                break;
            case NOT_NULL:
                predicates.add(criteriaBuilder.isNotNull(expression));
                break;
            case NOT_IN:
                predicates.add(criteriaBuilder.in(expression).value(searchFilter.getValue()).not());
                break;
            default:
                throw new IllegalArgumentException("For property :: " + searchFilter.getProperty() + " :: " + searchFilter.getOperator() + " is not valid predicate.");
        }
    }

    private static boolean isDateProperty(Path expression) {
        String propertyType = expression.getJavaType().getName();
        return Objects.isNull(dateMap.get(propertyType)) ? false : true;
    }

    private static void convertToDateType(SearchFilter searchFilter, Path expression) {
        String propertyType = expression.getJavaType().getName();
        Object dataType = dateMap.get(propertyType);
        Object propertyValue = getDateTypeValue(searchFilter.getValue(), dataType);
        searchFilter.setValue(propertyValue);
    }

    private static Object getDateTypeValue(Object propertyValue, Object dateType) {
        Object dateTypeValue = null;
        if (dateType instanceof LocalDate) {
            LocalDate localDate = null;
            if (!(propertyValue instanceof LocalDate))
                localDate = LocalDate.parse(propertyValue.toString());
            else
                localDate = (LocalDate) propertyValue;
            dateTypeValue = localDate;
        } else if (dateType instanceof LocalDateTime) {
            LocalDateTime localDateTime = null;
            if (propertyValue.toString().length() > 10) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                if (!(propertyValue instanceof LocalDateTime))
                    localDateTime = LocalDateTime.parse(propertyValue.toString(), format);
                else
                    localDateTime = (LocalDateTime) propertyValue;
            } else {
                LocalDate localDate = null;
                if (!(propertyValue instanceof LocalDate))
                    localDate = LocalDate.parse(propertyValue.toString());
                else
                    localDate = (LocalDate) propertyValue;
                localDateTime = localDate.atStartOfDay();
            }
            dateTypeValue = localDateTime;
        } else if (dateType instanceof Date) {
            Date date = new Date(propertyValue.toString());
            dateTypeValue = date;
        }
        return dateTypeValue;
    }

    private static void setForDateBetween(List<Predicate> predicates, CriteriaBuilder criteriaBuilder, SearchFilter searchFilter, Path expression) {
        String[] dates = ((String) searchFilter.getValue()).split("\\,");
        if (dates.length != 2) {
            throw new IllegalArgumentException("For Property :: " + searchFilter.getProperty() + " :: " + searchFilter.getOperator() + " is not a valid predicate");
        }
        String strStartDate = dates[0];
        String strEndDate = dates[1];
        String propertyType = expression.getJavaType().getName();
        Object dateType = dateMap.get(propertyType);
        Comparable startDate = (Comparable) getDateTypeValue(strStartDate, dateType);
        Comparable endDate = (Comparable) getDateTypeValue(strEndDate, dateType);
        predicates.add(criteriaBuilder.between(expression, startDate, endDate));
    }

    public static PageRequest getPageRequest(SearchQueryRequest searchQueryRequest) {
        int pageNumber = searchQueryRequest.getOffset();
        int pageSize = searchQueryRequest.getSize();
        List<Sort.Order> orders = new ArrayList<>();

        List<String> ascProps = searchQueryRequest.getSortOrder().getAscendingOrder();

        if (ascProps != null && !ascProps.isEmpty()) {
            for (String prop : ascProps) {
                orders.add(Sort.Order.asc(prop));
            }
        }

        List<String> descProps = searchQueryRequest.getSortOrder().getDescendingOrder();

        if (descProps != null && !descProps.isEmpty()) {
            for (String prop : descProps) {
                orders.add(Sort.Order.desc(prop));
            }

        }

        Sort sort = Sort.by(orders);
        return PageRequest.of(pageNumber, pageSize, sort);

    }
}
