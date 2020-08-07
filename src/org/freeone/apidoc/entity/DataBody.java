package org.freeone.apidoc.entity;

public class DataBody {
    TbRequestClassEntity requestClass;

    TbResponseClassEntity responseClass;

    public TbRequestClassEntity getRequestClass() {
        return requestClass;
    }

    public void setRequestClass(TbRequestClassEntity requestClass) {
        this.requestClass = requestClass;
    }

    public TbResponseClassEntity getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(TbResponseClassEntity responseClass) {
        this.responseClass = responseClass;
    }
}
