package com.chartmania.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.chartmania.dto.muidatagrid.MuiDataGridRequestDTO;
import com.chartmania.dto.muidatagrid.SortDTO;
import com.chartmania.util.GenericSpecification;

import org.springframework.data.domain.Sort;

@Service
public class MuiDataGridService {
    public <T, ID extends Serializable> Page<T> getData(
            JpaRepository<T, ID> repository,
            MuiDataGridRequestDTO request) {
        List<SortDTO> sortModel = request.getSortModel();

        JpaSpecificationExecutor<T> executor = (JpaSpecificationExecutor<T>) repository;
        Specification<T> specification = GenericSpecification.columnContains(request.getFilter());

        // Sorting here
        Sort sort = this.getSort(sortModel);
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize(), sort);

        
        return executor.findAll(specification,pageable);
    }

    private Sort getSort(List<SortDTO> sortModel) {
        if (sortModel == null || sortModel.isEmpty())
            return Sort.unsorted();

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < sortModel.size(); i++) {
            SortDTO sortElement = sortModel.get(i);

            Order newOrder;
            if ("asc".equalsIgnoreCase(sortElement.getSort()))
                newOrder = Sort.Order.asc(sortElement.getField());
            else
                newOrder = Sort.Order.desc(sortElement.getField());

            orders.add(newOrder);
        }

        return Sort.by(orders);
    }



}
