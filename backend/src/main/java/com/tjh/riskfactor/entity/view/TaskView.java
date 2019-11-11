package com.tjh.riskfactor.entity.view;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public interface TaskView {

    Integer getId();

    String getName();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date getMtime();

    String getCenter();
}
