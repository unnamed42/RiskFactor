package com.tjh.riskfactor.entities;

import lombok.Data;

@Data
public class PersonalHistory {
    private Integer historyId;
    private Integer userId;
    private Integer patientId;
    private String smoking;
    private String smoking2;
    private String drinking;
    private String drinking2;
}
