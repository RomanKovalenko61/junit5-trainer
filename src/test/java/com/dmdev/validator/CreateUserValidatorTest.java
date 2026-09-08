package com.dmdev.validator;

import com.dmdev.dto.CreateUserDto;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateUserValidatorTest {

    private final CreateUserValidator validator = CreateUserValidator.getInstance();

    @Test
    void shouldPassValidation() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();

        ValidationResult actual = validator.validate(dto);

        assertTrue(actual.isValid());
    }

    @Test
    void invalidBirthday() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01 12:32")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();

        ValidationResult actual = validator.validate(dto);

        Assertions.assertThat(actual.getErrors()).hasSize(1);
        Assertions.assertThat(actual.getErrors().get(0).getCode()).isEqualTo("invalid.birthday");
    }

    @Test
    void invalidGender() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01")
                .role(Role.USER.name())
                .gender("fake")
                .build();

        ValidationResult actual = validator.validate(dto);

        Assertions.assertThat(actual.getErrors()).hasSize(1);
        Assertions.assertThat(actual.getErrors().get(0).getCode()).isEqualTo("invalid.gender");
    }

    @Test
    void invalidRole() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01")
                .role("fake")
                .gender(Gender.MALE.name())
                .build();

        ValidationResult actual = validator.validate(dto);

        Assertions.assertThat(actual.getErrors()).hasSize(1);
        Assertions.assertThat(actual.getErrors().get(0).getCode()).isEqualTo("invalid.role");
    }

    @Test
    void invalidRoleGenderBirthday() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("01-01-2000")
                .role("fake_role")
                .gender("fake_gender")
                .build();

        ValidationResult actual = validator.validate(dto);

        Assertions.assertThat(actual.getErrors()).hasSize(3);
        List<String> errorCodes = actual.getErrors().stream()
                .map(Error::getCode)
                .toList();
        Assertions.assertThat(errorCodes)
                .hasSize(3)
                .contains("invalid.birthday", "invalid.role", "invalid.gender");
    }
}