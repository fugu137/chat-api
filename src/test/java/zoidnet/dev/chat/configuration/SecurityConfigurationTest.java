package zoidnet.dev.chat.configuration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(value = SecurityConfiguration.class)
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldAllowRequestsFromCorsAllowedOrigins() throws Exception {
        mockMvc.perform(options("/users/login")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://allowed-url.com"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowRequestsFromNonCorsAllowedOrigins() throws Exception {
        mockMvc.perform(options("/users/login")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://non-allowed-url.com"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Invalid CORS request"));
    }
}
