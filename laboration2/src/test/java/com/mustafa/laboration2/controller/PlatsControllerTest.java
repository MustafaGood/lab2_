package com.mustafa.laboration2.controller;

import com.mustafa.laboration2.entity.Plats;
import com.mustafa.laboration2.repository.PlatsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(PlatsController.class)
class PlatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlatsRepository platsRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllShouldReturnOK() throws Exception {
        Mockito.when(platsRepository.findByStatusAndIsDeletedFalse(Plats.Status.PUBLIK)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/platser").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
