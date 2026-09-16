package org.roadmap.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ResourceStorageIntegrationTest {
    @Container
    static GenericContainer<?> minio =
            new GenericContainer<>("minio/minio:latest")
                    .withEnv("MINIO_ROOT_USER", "test-user")
                    .withEnv("MINIO_ROOT_PASSWORD", "test-password")
                    .withCommand(
                            "server",
                            "/data",
                            "--console-address",
                            ":9001"
                    )
                    .withExposedPorts(9000, 9001)
                    .waitingFor(
                            Wait.forHttp("/minio/health/ready")
                                    .forPort(9000)
                                    .forStatusCode(200)
                    );

    @DynamicPropertySource
    static void configureMinio(DynamicPropertyRegistry registry) {
        registry.add(
                "minio.url",
                () -> "http://" + minio.getHost()
                        + ":" + minio.getMappedPort(9000)
        );

        registry.add("minio.access-key", () -> "test-user");
        registry.add("minio.secret-key", () -> "test-password");
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser("Kamil")
    void shouldUploadFileWhenRequestIsValid() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "object",
                "test.txt",
                "text/plain",
                new byte[100*1024]);

        mockMvc.perform(
                multipart("/resource")
                        .file(multipartFile)
                        .param("path", "documents/"))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser("Kamil")
    void shouldUploadFileToNestedDirectoryWhenPathIsValid() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "object",
                "test1.txt",
                "text/plain",
                new byte[100*1024]);

        mockMvc.perform(
                        multipart("/resource")
                                .file(multipartFile)
                                .param("path", "documents/doc1/"))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser("Kamil")
    void shouldUploadNestedResourcesWhenMultipartContainsDirectories() throws Exception {
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "object",
                "test1.txt",
                "text/plain",
                new byte[100*1024]);

        MockMultipartFile multipartFile2 = new MockMultipartFile(
                "object",
                "test2.txt",
                "text/plain",
                new byte[120*1024]);

        mockMvc.perform(
                        multipart("/resource")
                                .file(multipartFile1)
                                .file(multipartFile2)
                                .param("path", "documents/doc3/"))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser("Kamil")
    void shouldReturnConflictWhenFileAlreadyExists() throws Exception{
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "object",
                "test1.txt",
                "text/plain",
                new byte[100*1024]);

        MockMultipartFile multipartFile2 = new MockMultipartFile(
                "object",
                "test1.txt",
                "text/plain",
                new byte[100*1024]);


        mockMvc.perform(
                        multipart("/resource")
                                .file(multipartFile1)
                                .param("path", "documents/doc1/"))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        multipart("/resource")
                                .file(multipartFile2)
                                .param("path", "documents/doc1/"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser("Kamil")
    void shouldRenameFileWhenTargetPathIsAvailable() throws Exception {
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "object",
                "test1.txt",
                "text/plain",
                new byte[100*1024]);



        mockMvc.perform(
                        multipart("/resource")
                                .file(multipartFile1)
                                .param("path", "documents/doc1/"));



        mockMvc.perform(
                        multipart("/resource/move")
                                .param("from", "documents/doc1/test1.txt")
                                .param("to", "documents/doc1/test2.txt"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("Kamil")
    void shouldMoveFileWhenTargetPathIsAvailable() throws Exception {
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "object",
                "test1.txt",
                "text/plain",
                new byte[100 * 1024]);

        mockMvc.perform(
                multipart("/resource")
                        .file(multipartFile1)
                        .param("path", "documents/doc1/"));

        mockMvc.perform(
                post("/directory")
                        .param("path", "documents/doc2/"));

        mockMvc.perform(
                        multipart("/resource/move")
                                .param("from", "documents/doc1/test1.txt")
                                .param("to", "documents/doc2/test1.txt"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser("Kamil")
    void shouldMoveDirectoryWhenTargetPathIsAvailable() throws Exception {
        MockMultipartFile firstFile = new MockMultipartFile(
                "object",
                "text1.txt",
                "text/plain",
                new byte[1024]);

        MockMultipartFile secondFile = new MockMultipartFile(
                "object",
                "text2.txt",
                "text/plain",
                new byte[1024]);

        mockMvc.perform(
                multipart("/resource")
                        .file(firstFile)
                        .param("path", "documents/doc1/"));

        mockMvc.perform(
                multipart("/resource")
                        .file(secondFile)
                        .param("path", "documents/doc1/"));

        mockMvc.perform(
                post("/directory")
                        .param("path", "documents/doc2/"));

        mockMvc.perform(
                        post("/resource/move")
                                .param("from", "documents/doc1/")
                                .param("to", "documents/doc2/doc1/"))
                .andExpect(status().is2xxSuccessful());
    }
}
