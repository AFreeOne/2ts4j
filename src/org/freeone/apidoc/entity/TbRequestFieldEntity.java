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

    public TbRequestFieldEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public TbRequestFieldEntity setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public Boolean getRequired() {
        return required;
    }

    public TbRequestFieldEntity setRequired(Boolean required) {
        this.required = required;
        return this;
    }

    public Integer getRequestClassId() {
        return requestClassId;
    }

    public TbRequestFieldEntity setRequestClassId(Integer requestClassId) {
        this.requestClassId = requestClassId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TbRequestFieldEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "TbRequestFieldEntity{" +
                "id=" + id +
                ", fieldName='" + fieldName + '\'' +
                ", required=" + required +
                ", requestClassId=" + requestClassId +
                ", description='" + description + '\'' +
                '}';
    }
}
