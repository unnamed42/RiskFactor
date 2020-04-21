package com.tjh.riskfactor.test.bean

import com.tjh.riskfactor.service.AccountService
import org.hibernate.service.spi.InjectService
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AccountServiceTest {

    @InjectMocks
    private lateinit var service: AccountService



}
