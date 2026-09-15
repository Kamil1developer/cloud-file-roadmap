package org.roadmap.integration;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.roadmap.entity.User;
import org.roadmap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthIntegrationTest {

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password1";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7-alpine")
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add(
                "spring.data.redis.port",
                () -> redis.getMappedPort(6379)
        );
    }

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserWhenRequestIsValid() throws Exception {
        mockMvc.perform(
                        post("/auth/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "testuser",
                                          "password": "password1"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(USERNAME));

        User savedUser = userRepository.findByUsername(USERNAME)
                .orElseThrow();

        assertAll(
                () -> assertNotEquals(PASSWORD, savedUser.passwordHash),
                () -> assertTrue(
                        passwordEncoder.matches(
                                PASSWORD,
                                savedUser.passwordHash
                        )
                )
        );
    }

    @Test
    void shouldReturnConflictWhenUsernameAlreadyExists() throws Exception {
        createUser(USERNAME, PASSWORD);

        mockMvc.perform(
                        post("/auth/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "testuser",
                                          "password": "password1"
                                        }
                                        """)
                )
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenRegistrationDataIsInvalid() throws Exception {
        mockMvc.perform(
                        post("/auth/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "ab",
                                          "password": "password1"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldSignInAndCreateSessionWhenCredentialsAreValid() throws Exception {
        createUser(USERNAME, PASSWORD);

        MvcResult signInResult = mockMvc.perform(
                        post("/auth/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "testuser",
                                          "password": "password1"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(cookie().exists("SESSION"))
                .andReturn();

        Cookie sessionCookie = signInResult
                .getResponse()
                .getCookie("SESSION");

        assertNotNull(sessionCookie);

        mockMvc.perform(
                        get("/user/me")
                                .cookie(sessionCookie)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME));
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordIsInvalid() throws Exception {
        createUser(USERNAME, PASSWORD);

        mockMvc.perform(
                        post("/auth/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "testuser",
                                          "password": "wrongPassword"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(
                        post("/auth/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "unknown",
                                          "password": "password1"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenSignInDataIsInvalid() throws Exception {
        mockMvc.perform(
                        post("/auth/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "ab",
                                          "password": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorizedWhenCurrentUserRequestedWithoutSession()
            throws Exception {

        mockMvc.perform(get("/user/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldSignOutAndInvalidateSession() throws Exception {
        createUser(USERNAME, PASSWORD);
        Cookie sessionCookie = signIn(USERNAME, PASSWORD);

        mockMvc.perform(
                        post("/auth/sign-out")
                                .cookie(sessionCookie)
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        mockMvc.perform(
                        get("/user/me")
                                .cookie(sessionCookie)
                )
                .andExpect(status().isUnauthorized());
    }

    private void createUser(String username, String password) {
        userRepository.save(
                new User(
                        username,
                        passwordEncoder.encode(password)
                )
        );
    }

    private Cookie signIn(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/auth/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "%s",
                                          "password": "%s"
                                        }
                                        """.formatted(username, password))
                )
                .andExpect(status().isOk())
                .andExpect(cookie().exists("SESSION"))
                .andReturn();

        Cookie sessionCookie = result.getResponse().getCookie("SESSION");
        assertNotNull(sessionCookie);

        return sessionCookie;
    }
}
