package com.tjh.riskfactor.entity.view;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public interface AnswerView {
    Integer getId();
    String getCreator();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date getMtime();
}
