package com.tjh.riskfactor.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Patient {

    @Id @GeneratedValue
    private Integer id;

    // 姓名
    private String name;

    // 电话号码
    private String telephone;

    // 医保号
    private String medicareId;

}
