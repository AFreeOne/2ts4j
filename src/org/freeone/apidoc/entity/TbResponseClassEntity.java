package org.freeone.apidoc.entity;

import java.util.Date;

public class TbResponseClassEntity {
    private Integer id;

    private String className;

    private String packagePath;

    private String description;

    private Date createTime;

    private Date updateTime;

    private Integer requestClassId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getRequestClassId() {
        return requestClassId;
    }

    public void setRequestClassId(Integer requestClassId) {
        this.requestClassId = requestClassId;
    }
}
