package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.CustomValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user1 = new User(1, "", "", "", LocalDate.of(2222, 10, 10), null);
        user2 = new User(0, "mail@mail.com", "Nagibator228", "Robert",
                LocalDate.of(1989, 9, 13), null);
    }

    @Test
    void whenGetUsers_thenGetListOfUsers() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("Nagibator228"));
    }

    @Test
    void givenUser_whenAddUserMethod_thenFillMap() throws Exception {
        when(userService.addUser(any(User.class))).thenReturn(user2);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk());
    }

    @Test
    void givenUser_whenUpdateUserMethod_thenFillMap() throws Exception {
        User updatedUser = new User(1, "mmm@mmm.mm", "Rocky", "Stanislav",
                LocalDate.of(1995, 5, 7), null);

        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Stanislav"));
    }

    @Test
    void givenWrongUser_whenPostRequest_thenThrowException() throws Exception {
        when(userService.addUser(any(User.class))).thenThrow(new CustomValidationException("Validation error"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isBadRequest());
    }
}
