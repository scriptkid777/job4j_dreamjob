package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.repository.CityService;
import ru.job4j.dreamjob.service.CandidateService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CandidateControllerTest {
    private CandidateService candidateService;
    private CityService cityService;
    private CandidateController controller;
    private MultipartFile testFile;

    @BeforeEach
    void init() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        controller = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    void whenRequestCandidateListPageThenGetListWithCandidates() {
        var candidate1 = new Candidate(1, "test1", "desc1", now(), 1, 1);
        var candidate2 = new Candidate(2, "test2", "desc2", now(), 2, 2);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var model = new ConcurrentModel();
        var view = controller.getAll(model);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    void whenRequestCandidatesCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Саткт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = controller.getCreationPage(model);
        var actualCities = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCities).isEqualTo(expectedCities);
    }

    @Test
    void whenPostCreateCandidateWithFileThenSuccessAndRedirectToCandidatesPage() throws Exception {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);

        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate);

        var model = new ConcurrentModel();
        var view = controller.create(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    void whenPostCreateCandidateWithFileThenNotSuccessAndGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Some Message");
        when(candidateService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = controller.create(new Candidate(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    void whenRequestByIdThenGetPageOneForEdit() {
        var candidate1 = new Candidate(1, "test1", "desc1", now(), 1, 1);
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Саткт-Петербург");
        var expectedCities = List.of(city1, city2);

        when(cityService.findAll()).thenReturn(expectedCities);
        when(candidateService.findById(candidate1.getId())).thenReturn(Optional.of(candidate1));
        var model = new ConcurrentModel();
        var view = controller.getById(model, candidate1.getId());
        var actualCandidate = model.getAttribute("candidate");
        var actualCities = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/one");
        assertThat(actualCandidate).isEqualTo(candidate1);
        assertThat(actualCities).isEqualTo(expectedCities);
    }

    @Test
    void whenRequestByIdNotSuccessThenGetErrorPageWithMessage() {
        when(candidateService.findById(any(Integer.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = controller.getById(model, 1);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Резюме с указанным id не найдено");
    }

    @Test
    void whenPostUpdateThenSuccessAndRedirectToCandidatesPage() throws Exception {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);

        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);
        var model = new ConcurrentModel();
        var view = controller.update(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    void whenPostUpdateThenNotSuccessAndGetErrorPageWithMessage() {
        when(candidateService.update(any(Candidate.class), any(FileDto.class))).thenReturn(false);

        var model = new ConcurrentModel();
        var view = controller.update(new Candidate(), testFile, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Кандидат с указанным идентификатором не найден");
    }

    @Test
    void whenPostUpdateThenNotSuccessAndGetSomeException() {
        var expectedException = new RuntimeException("Some Exception");

        when(candidateService.update(any(Candidate.class), any(FileDto.class))).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = controller.update(new Candidate(), testFile, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    void whenRequestDeleteCandidatesSuccessThenRedirectToCandidatesPage() {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 1);

        when(candidateService.findById(candidate.getId())).thenReturn(Optional.of(candidate));

        var model = new ConcurrentModel();
        var view = controller.delete(model, candidate.getId());

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    void whenRequestDeleteCandidatesNotSuccessThenGetErrorWithPage() {
        when(candidateService.findById(any(Integer.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = controller.delete(model, 1);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Резюме с указанным id не найдено");
    }
}
