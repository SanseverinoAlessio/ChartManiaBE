package com.chartmania.util;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import com.chartmania.dto.muidatagrid.FilterDTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class GenericSpecification {
    public static <T> Specification<T> columnContains(FilterDTO filter) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if(filter == null){
                   return criteriaBuilder.conjunction();
                }

                if (filter.getValue() == null) {
                    return criteriaBuilder.conjunction();
                }

                String pattern = "%" + filter.getValue() + "%";
               return criteriaBuilder.like(
                    root.get(filter.getField()).as(String.class), 
                    pattern
                );
            }
        };
    }

}
