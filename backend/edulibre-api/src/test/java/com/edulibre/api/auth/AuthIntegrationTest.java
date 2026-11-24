package com.edulibre.api.auth;

import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registroSeteaCookieYPermitePerfil() throws Exception {
        String email = "nuevo-" + System.nanoTime() + "@edulibre.com";
        String body = """
                {"nombre":"Nuevo Estudiante","email":"%s","password":"secreto123"}
                """.formatted(email);

        MvcResult registro = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"))
                .andExpect(cookie().exists("edulibre_token"))
                .andExpect(cookie().httpOnly("edulibre_token", true))
                .andExpect(cookie().path("edulibre_token", "/"))
                .andExpect(header().string("Set-Cookie", Matchers.containsString("SameSite=Lax")))
                .andReturn();

        Cookie cookie = registro.getResponse().getCookie("edulibre_token");

        mockMvc.perform(get("/api/auth/me").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"));
    }

    @Test
    void loginAceptaCookieYLimpiaEnLogout() throws Exception {
        String email = "login-" + System.nanoTime() + "@edulibre.com";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Login User","email":"%s","password":"secreto123"}
                                """.formatted(email)))
                .andExpect(status().isCreated());

        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"secreto123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("edulibre_token"))
                .andExpect(cookie().httpOnly("edulibre_token", true))
                .andReturn();

        Cookie cookie = login.getResponse().getCookie("edulibre_token");

        mockMvc.perform(post("/api/auth/logout").cookie(cookie))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("edulibre_token", 0));
    }

    @Test
    void rutaProtegidaSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/inscripciones/mis"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void perfilSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void catalogoEsPublico() throws Exception {
        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk());
    }
}
