package ru.yandex.practicum.catsgram.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"email"})
public class User {

    Long id;
    String username;
    String email;
    String password;
    LocalDate registrationDate;


}
