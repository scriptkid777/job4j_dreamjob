package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.repository.CandidateRepository;
import ru.job4j.dreamjob.repository.MemoryCandidateRepository;

@Controller
@RequestMapping("/candidates")
public class CandidateController {
        private final CandidateRepository candidateRepository = MemoryCandidateRepository.getInstance();

        @GetMapping
        public String getAll(Model model) {
            model.addAttribute("candidates", candidateRepository.findAll());
            return "candidates/list";
        }

    @GetMapping("/create")
    public String getCreationPage() {
        return "candidates/create";
    }
}
