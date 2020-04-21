package com.tjh.riskfactor.test.bean

import com.tjh.riskfactor.repository.Group
import com.tjh.riskfactor.repository.GroupRepository
import com.tjh.riskfactor.repository.User
import com.tjh.riskfactor.repository.UserRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountRepoTest {

    @Autowired private lateinit var users: UserRepository
    @Autowired private lateinit var groups: GroupRepository

    private val encoder = BCryptPasswordEncoder()

    private lateinit var userList: List<User>

    @BeforeAll
    fun setup() {
        val root = groups.save(Group("root"))
        val users_ = groups.save(Group("users"))
        val nobody = groups.save(Group("nobody"))

        val userData = listOf(
            User(username = "admin", password = encoder.encode("admin"), isAdmin = true,
                email = "a@b", group = root),
            User(username = "admin2", password = encoder.encode("admin2"),
                email = "a@b", group = root),
            User(username = "user", password = encoder.encode("user"), isAdmin = true,
                email = "c@d", group = users_),
            User(username = "user2", password = encoder.encode("user2"),
                email = "e@f", group = users_),
            User(username = "nuser", password = encoder.encode("nuser"), isAdmin = true,
                email = "n1@n", group = nobody),
            User(username = "nuser2", password = encoder.encode("nuser2"),
                email = "n1@n", group = nobody)
        )
        userList = users.saveAll(userData)
    }

    @Test
    fun testGetAllId() {
        val userIds = users.findAllIds().sorted()
        val existing = userList.asSequence().map { it.id }.sorted().toList()
        assertEquals(userIds, existing)
    }

}
