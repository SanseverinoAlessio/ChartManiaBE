package com.chartmania.dto.muidatagrid;

public class FindByDTO {
    private String field;
    private String value;
    private String entityName;


    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setEntityName(String value){
        this.entityName = value;
    }

    public String getEntityName(){
        return this.entityName;
    }

}
