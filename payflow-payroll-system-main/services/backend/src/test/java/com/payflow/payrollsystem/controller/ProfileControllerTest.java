package com.payflow.payrollsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.payrollsystem.dto.ProfileDto;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private void authAs(String email) {
        SecurityContext ctx = Mockito.mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(email, "n/a"));
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void me_returns_profile() throws Exception {
        User u = new User(); u.setId(1L); u.setEmail("u@c.com"); u.setDisplayName("User One");
        when(userRepository.findByEmail("u@c.com")).thenReturn(Optional.of(u));
        authAs("u@c.com");
        mvc.perform(get("/api/profile/me")).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("u@c.com"));
    }

    @Test
    void update_saves_profile() throws Exception {
        User u = new User(); u.setId(2L); u.setEmail("u2@c.com");
        when(userRepository.findByEmail("u2@c.com")).thenReturn(Optional.of(u));
        authAs("u2@c.com");

        ProfileDto dto = new ProfileDto(); dto.setDisplayName("NewName"); dto.setPhone("123");
        mvc.perform(put("/api/profile").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.displayName").value("NewName"));
    }
}
