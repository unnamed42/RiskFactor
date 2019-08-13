package com.tjh.riskfactor.entity;

import lombok.Data;

import javax.persistence.*;

import java.util.Date;

@Data
@Entity
@Table(name = "basic_info")
public class BasicInfo {

    // 中心编号（预设）
    @Id @GeneratedValue
    private Integer id;

    // 研究编号（4位）
    private Integer surveyId;

    // 出生日期（年月日）
    @Temporal(TemporalType.DATE)
    private Date birth;

    // 性别
    private String gender;

    // 民族
    private String ethnic;

    // 身高（m）
    private Integer height;

    // 体重（kg）
    private Integer weight;

    // 职业
    private String job;

    // 籍贯-省
    private String birthProvince;

    // 籍贯-市
    private String birthCity;

    // 地址-省
    private String addrProvince;

    // 地址-市
    private String addrCity;

    // 地址-县
    private String addrCounty;
}

