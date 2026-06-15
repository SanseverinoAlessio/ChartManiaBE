package com.chartmania.dto.muidatagrid;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;;

public class MuiDataGridRequestDTO {
    @NotNull
    private int page;

    @NotNull
    private int pageSize;

    private List<SortDTO> sortModel = new ArrayList<>();

    private FilterDTO filter;

    private FindByDTO findBy;

    public MuiDataGridRequestDTO() {}

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setSortModel(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        sortModel = objectMapper.readValue(json, new TypeReference<List<SortDTO>>(){});
    }

    public void setFilter(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        filter = objectMapper.readValue(json, FilterDTO.class);
    }
    public FilterDTO getFilter(){
        return filter;
    }

    public void setFindBy(String field, String value){
        findBy = new FindByDTO();
        findBy.setField(field);
        findBy.setValue(value);
    }

      public void setFindBy(String field, String value,String entityName){
        findBy = new FindByDTO();
        findBy.setField(field);
        findBy.setValue(value);
        findBy.setEntityName(entityName);
    }


    public FindByDTO getFindBy(){
        return findBy;
    }

    public List<SortDTO> getSortModel(){
        return this.sortModel;
    }
}