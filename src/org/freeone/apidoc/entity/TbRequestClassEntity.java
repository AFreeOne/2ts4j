package org.freeone.apidoc.entity;

import java.util.Date;
import java.util.List;

public class TbRequestClassEntity {

    private Integer id;

    private String className;

    private String packagePath;

    private String description;

    private Date createTime;

    private Date updateTime;

    private String platform;

    private String fieldType;

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    List<TbRequestFieldEntity> requestFieldList;

    public List<TbRequestFieldEntity> getRequestFieldList() {
        return requestFieldList;
    }

    public void setRequestFieldList(List<TbRequestFieldEntity> requestFieldList) {
        this.requestFieldList = requestFieldList;
    }

    public Integer getId() {
        return id;
    }

    public TbRequestClassEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public TbRequestClassEntity setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public TbRequestClassEntity setPackagePath(String packagePath) {
        this.packagePath = packagePath;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TbRequestClassEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public TbRequestClassEntity setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public TbRequestClassEntity setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getPlatform() {
        return platform;
    }

    public TbRequestClassEntity setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    @Override
    public String toString() {
        return "TbRequestClassEntity{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", packagePath='" + packagePath + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", platform='" + platform + '\'' +
                ", requestFieldList=" + requestFieldList +
                '}';
    }
}