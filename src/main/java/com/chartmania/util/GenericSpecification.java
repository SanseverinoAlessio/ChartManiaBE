package com.chartmania.util;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.mapping.Join;
import org.springframework.data.jpa.domain.Specification;
import com.chartmania.dto.muidatagrid.FilterDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.chartmania.dto.muidatagrid.FindByDTO;
import com.chartmania.model.Chart;

public class GenericSpecification {
    public static <T> Specification<T> columnContains(FilterDTO filter, FindByDTO findBy) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (filter != null && filter.getValue() != null) {
                    String pattern = "%" + filter.getValue() + "%";
                    predicates.add(
                            criteriaBuilder.like(
                                    root.get(filter.getField()).as(String.class),
                                    pattern));
                }

                if (findBy != null && findBy.getValue() != null) {
                    Predicate findByPredicate;
                    if (findBy.getEntityName() != null)
                        findByPredicate = criteriaBuilder.equal(root.get(findBy.getEntityName()).get(findBy.getField()),
                                findBy.getValue());
                    else
                        findByPredicate = criteriaBuilder.equal(root.get(findBy.getField()), findBy.getValue());

                    predicates.add(
                            findByPredicate);

                }

                if (predicates.size() <= 0) {
                    return criteriaBuilder.conjunction();
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

}
