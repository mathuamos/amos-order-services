package com.jagaad.jagaadorderservices.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseModel {
    private String status;
    private String message;
    private Object data;

    public ResponseModel(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseModel(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

}
