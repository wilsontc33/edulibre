package com.edulibre.api.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class

    AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registroLoginYPerfil() throws Exception {
        String email = "nuevo-" + System.nanoTime() + "@edulibre.com";
        String body = """
                {"nombre":"Nuevo Estudiante","email":"%s","password":"secreto123"}
                """.formatted(email);

        String registro = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(registro).get("token").asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"));
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
