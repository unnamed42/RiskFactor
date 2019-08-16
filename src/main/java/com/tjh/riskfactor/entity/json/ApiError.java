package com.tjh.riskfactor.entity.json;

import lombok.Setter;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@Getter @Setter
public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date timestamp;

    private Integer status;

    private String error;

    @Setter
    private String message;

    private String uri;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String body = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stacktrace = null;

}
