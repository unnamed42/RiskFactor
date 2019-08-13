package com.tjh.riskfactor.entity;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "personal_history")
public class PersonalHistory {
    private Integer historyId;
    private Integer userId;
    private Integer patientId;
    private String smoking;
    private String smoking2;
    private String drinking;
    private String drinking2;
}
