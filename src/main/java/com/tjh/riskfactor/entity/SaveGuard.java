package com.tjh.riskfactor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data @Entity
@Table(name = "save_guard")
@Accessors(chain = true)
public class SaveGuard {

    @Id
    private Integer id;

}
