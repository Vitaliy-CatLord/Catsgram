package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public Optional<User> findUserById(long userId) {
        Optional<User> user= Optional.ofNullable(users.get(userId));
        if (user.isPresent()) {
            return user;
        } else {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    public User create(User newUser) {
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (isEmailEmployed(newUser)) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        newUser.setId(getNextId());
        newUser.setRegistrationDate(LocalDate.now());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                throw new ConditionsNotMetException("Имейл должен быть указан");
            }
            if (isEmailEmployed(newUser)) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }

            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getPassword() != null) {
                oldUser.setPassword(newUser.getPassword());
            }
            if (newUser.getUsername() != null) {
                oldUser.setUsername(newUser.getUsername());
            }
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean isEmailEmployed(User newUser) {
        return users.values().stream()
                .anyMatch(user -> newUser.getEmail().equals(user.getEmail()));
    }
}
