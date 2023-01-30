package zoidnet.dev.chat.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import zoidnet.dev.chat.configuration.SecurityConfiguration;
import zoidnet.dev.chat.controller.dto.UserDto;
import zoidnet.dev.chat.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<UserDto> userDtoCaptor;


    @Test
    @WithMockUser(username = "Jacqueline", roles = "ADMIN")
    void shouldGetPrincipalIfUserLoggedIn() throws Exception {
        MvcResult response = mockMvc.perform(get("/users/principal")
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseAsString = response.getResponse().getContentAsString();

        assertThat(responseAsString, is("{\"username\":\"Jacqueline\",\"authorities\":[{\"authority\":\"ROLE_ADMIN\"}]}"));
    }

    @Test
    void shouldNotGetPrincipalIfUserNotLoggedIn() throws Exception {
        MvcResult response = mockMvc.perform(get("/users/principal")
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseAsString = response.getResponse().getContentAsString();

        assertThat(responseAsString, is(emptyString()));
    }

    @Test
    void shouldCreateUser() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        ObjectMapper mapper = new ObjectMapper();
        String userAsJson = mapper.writeValueAsString(userDto);

        MockHttpServletRequestBuilder request = post("/users")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAsJson);

        mockMvc.perform(request).andExpect(status().isCreated());

        verify(userService, times(1)).registerUser(userDtoCaptor.capture());

        UserDto capturedDto = userDtoCaptor.getValue();
        assertThat(capturedDto.getUsername(), is(username));
        assertThat(capturedDto.getPassword(), is(password));
    }

    @Test
    void shouldThrowErrorIfUsernameAlreadyExists() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        ObjectMapper mapper = new ObjectMapper();
        String userAsJson = mapper.writeValueAsString(userDto);

        doThrow(new DuplicateKeyException("Username already exists")).when(userService).registerUser(userDto);

        MockHttpServletRequestBuilder request = post("/users")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAsJson);

        mockMvc.perform(request).andExpect(status().isConflict());
    }
}
