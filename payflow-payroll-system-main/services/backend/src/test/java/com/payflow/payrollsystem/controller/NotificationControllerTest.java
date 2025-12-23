package com.payflow.payrollsystem.controller;

import com.payflow.payrollsystem.model.Notification;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.UserRepository;
import com.payflow.payrollsystem.service.NotificationService;
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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserRepository userRepository;

    private void authAs(String email) {
        SecurityContext ctx = Mockito.mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(email, "n/a"));
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void list_returns_notifications_for_user() throws Exception {
        User u = new User(); u.setId(5L); u.setEmail("notified@c.com");
        Notification n = new Notification(); n.setId(1L); n.setMessage("You were promoted"); n.setUser(u);
        when(userRepository.findByEmail("notified@c.com")).thenReturn(java.util.Optional.of(u));
        when(notificationService.listForUser(5L)).thenReturn(List.of(n));
        authAs("notified@c.com");
        mvc.perform(get("/api/notifications").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].message").value("You were promoted"));
    }
}
