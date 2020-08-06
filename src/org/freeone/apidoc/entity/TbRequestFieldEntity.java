package org.freeone.apidoc.entity;

public class TbRequestFieldEntity {
    private Integer id;

    private String fieldName;

    private Boolean required;

    private Integer requestClassId;

    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Integer getRequestClassId() {
        return requestClassId;
    }

    public void setRequestClassId(Integer requestClassId) {
        this.requestClassId = requestClassId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
