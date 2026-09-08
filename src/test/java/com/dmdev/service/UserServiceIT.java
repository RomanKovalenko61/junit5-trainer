package com.dmdev.service;

import com.dmdev.dao.UserDao;
import com.dmdev.dto.CreateUserDto;
import com.dmdev.dto.UserDto;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateUserMapper;
import com.dmdev.mapper.UserMapper;
import com.dmdev.validator.CreateUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserServiceIT extends IntegrationTestBase {

    private UserService userService;
    private UserDao userDao;

    @BeforeEach
    void init() {
        userDao = UserDao.getInstance();
        userService = new UserService(
                CreateUserValidator.getInstance(),
                userDao,
                CreateUserMapper.getInstance(),
                UserMapper.getInstance()
        );
    }

    @Test
    void login() {
        User user = userDao.save(getUser());

        Optional<UserDto> actual = userService.login(user.getEmail(), user.getPassword());

        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void create() {
        CreateUserDto createUserDto = getCreateUserDto();

        UserDto actual = userService.create(createUserDto);

        assertNotNull(actual.getId());
    }

    private static CreateUserDto getCreateUserDto() {
        return CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();
    }

    private static User getUser() {
        return User.builder()
                .id(99)
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday(LocalDate.of(2000, 1, 1))
                .role(Role.USER)
                .gender(Gender.MALE)
                .build();
    }
}