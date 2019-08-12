package com.tjh.riskfactor.entities;

import lombok.Setter;
import lombok.Getter;
import lombok.experimental.Accessors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Date;

/**
 * Returns when error occurs on api access
 * Not a database entity
 */
@Getter @Setter
@Accessors(chain = true)
public class ApiError {

    private Date timestamp;
    private HttpStatus status;
    private String message;
    private String uri;

    public ApiError() {
        this.timestamp = new Date();
    }

    public ApiError setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public ApiError setUri(WebRequest request) {
        this.setUri(((ServletWebRequest)request).getRequest().getRequestURI());
        return this;
    }

    public ResponseEntity<Object> toResponseEntity() {
        return new ResponseEntity<>(this, this.getStatus());
    }

}
