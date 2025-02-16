package edu.eci.arsw.blueprints.model;

public class Response<T>{
    public long code;
    public T description;

    public Response(long code, T description) {
        this.code = code;
        this.description = description;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public T getDescription() {
        return description;
    }

    public void setDescription(T description) {
        this.description = description;
    }
}
