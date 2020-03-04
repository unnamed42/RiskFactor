package com.tjh.riskfactor.entity

import javax.persistence.*

/**
 * 复杂动作的“已执行”flag，仅在[com.tjh.riskfactor.component.InitialDataLoader]中使用
 */
@Entity @Table(name = "save_guard")
class SaveGuard(@Id val id: Int = 0)
