package org.freeone.apidoc.entity;

import java.util.ArrayList;
import java.util.List;

public class TbResponseFieldEntity {
    private Integer id;

    private String fieldName;

    private String fieldType;

    private Integer pid;

    private String description;

    private List<TbResponseFieldEntity> nodeFieldList = new ArrayList<>();

    public List<TbResponseFieldEntity> getNodeFieldList() {
        return nodeFieldList;
    }

    public void setNodeFieldList(List<TbResponseFieldEntity> nodeFieldList) {
        this.nodeFieldList = nodeFieldList;
    }

    public Integer getId() {
        return id;
    }

    public TbResponseFieldEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public TbResponseFieldEntity setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public String getFieldType() {
        return fieldType;
    }

    public TbResponseFieldEntity setFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public Integer getPid() {
        return pid;
    }

    public TbResponseFieldEntity setPid(Integer pid) {
        this.pid = pid;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TbResponseFieldEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "TbResponseFieldEntity{" +
                "id=" + id +
                ", fieldName='" + fieldName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", pid=" + pid +
                ", description='" + description + '\'' +
                ", nodeFieldList=" + nodeFieldList +
                '}';
    }
}
