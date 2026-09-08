package com.dmdev.dao;

import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserDaoIT extends IntegrationTestBase {

    private final UserDao userDao = UserDao.getInstance();

    @Test
    void findAll() {
        User ivan = getUser("Ivan", "ivan@gmail.com", LocalDate.of(1990, 1, 10), Gender.MALE);
        User petr = getUser("Petr", "petr@gmail.com", LocalDate.of(1995, 10, 19), Gender.MALE);
        User sveta = getUser("Sveta", "sveta@gmail.com", LocalDate.of(2001, 12, 23), Gender.FEMALE);
        userDao.save(ivan);
        userDao.save(petr);
        userDao.save(sveta);

        List<User> actual = userDao.findAll();

        assertThat(actual).hasSize(3);
        List<Integer> userIds = actual.stream()
                .map(User::getId)
                .toList();
        assertThat(userIds).contains(ivan.getId(), petr.getId(), sveta.getId());
    }

    @Test
    void findById() {
        User ivan = getUser("Ivan", "ivan@gmail.com", LocalDate.of(1990, 1, 10), Gender.MALE);
        userDao.save(ivan);

        Optional<User> actual = userDao.findById(ivan.getId());

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(ivan);
    }

    @Test
    void save() {
        User vlad = getUser("Vlad", "vlad@gmail.com", LocalDate.of(1984, 3, 14), Gender.MALE);

        User actual = userDao.save(vlad);

        assertNotNull(actual.getId());
    }

    @Test
    void saveWithExistingEmail() {
        User vlad = getUser("Vlad", "vlad@gmail.com", LocalDate.of(1984, 3, 14), Gender.MALE);
        User duplicate = getUser("Dummy", "vlad@gmail.com", LocalDate.of(1990, 6, 20), Gender.MALE);

        userDao.save(vlad);
        assertThrows(SQLException.class, () -> userDao.save(duplicate));
    }

    @Test
    void findByEmailAndPassword() {
        User sveta = getUser("Sveta", "sveta@gmail.com", LocalDate.of(2001, 12, 23), Gender.FEMALE);
        userDao.save(sveta);

        Optional<User> actual = userDao.findByEmailAndPassword(sveta.getEmail(), sveta.getPassword());

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(sveta);
    }

    @Test
    void shouldNotFindByEmailAndPasswordIfUserDoesNotExist() {
        User sveta = getUser("Sveta", "sveta@gmail.com", LocalDate.of(2001, 12, 23), Gender.FEMALE);
        userDao.save(sveta);

        Optional<User> actual = userDao.findByEmailAndPassword("dummy", "123");

        assertThat(actual).isEmpty();
    }

    @Test
    void deleteExistingEntity() {
        User petr = getUser("Petr", "petr@gmail.com", LocalDate.of(1995, 10, 19), Gender.MALE);
        userDao.save(petr);

        boolean actual = userDao.delete(petr.getId());

        assertTrue(actual);
    }

    @Test
    void deleteNotExistingEntity() {
        User petr = getUser("Petr", "petr@gmail.com", LocalDate.of(1995, 10, 19), Gender.MALE);
        userDao.save(petr);

        boolean actual = userDao.delete(100500);

        assertFalse(actual);
    }

    @Test
    void update() {
        User sveta = getUser("Sveta", "sveta@gmail.com", LocalDate.of(2001, 12, 23), Gender.FEMALE);
        userDao.save(sveta);
        sveta.setEmail("sveta@mail.ru");
        sveta.setBirthday(LocalDate.of(2002, 3, 8));

        userDao.update(sveta);

        User updated = userDao.findById(sveta.getId()).get();
        assertThat(updated).isEqualTo(sveta);
    }

    private static User getUser(String name, String email, LocalDate ld, Gender gender) {
        return User.builder()
                .name(name)
                .email(email)
                .birthday(ld)
                .password("111")
                .role(Role.USER)
                .gender(gender)
                .build();
    }
}