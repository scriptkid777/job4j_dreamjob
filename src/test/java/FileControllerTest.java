import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.controller.FileController;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileControllerTest {
    private FileService fileService;
    private FileController controller;
    private MultipartFile testFile;

    @BeforeEach
    void init() {
        fileService = mock(FileService.class);
        controller = new FileController(fileService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    void whenRequestFileGetByIdThenGetStatusOK() throws Exception {
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        when(fileService.getFileById(any(Integer.class))).thenReturn(Optional.of(fileDto));
        var expected = ResponseEntity.ok(fileDto.getContent());
        assertThat(controller.getById(1)).isEqualTo(expected);
    }

    @Test
    void whenRequestFileGetByIdThenGetNotFound() throws Exception {
        when(fileService.getFileById(any(Integer.class))).thenReturn(Optional.empty());
        var expected = ResponseEntity.notFound().build();
        assertThat(controller.getById(1)).isEqualTo(expected);
    }
}
