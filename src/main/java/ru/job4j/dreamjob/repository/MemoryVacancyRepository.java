package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);
    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    public MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "description for Intern Java Developer", true, 1, 0));
        save(new Vacancy(0, "Junior Java Developer", "description for Junior Java Developer", true, 2, 0));
        save(new Vacancy(0, "Junior+ Java Developer", "description for Junior+ Java Developer", true, 3, 0));
        save(new Vacancy(0, "Middle Java Developer", "description for Middle Java Developer", true, 1, 0));
        save(new Vacancy(0, "Middle+ Java Developer", "description for Middle+ Java Developer", true, 2, 0));
        save(new Vacancy(0, "Senior Java Developer", "description for Senior Java Developer", true, 3, 0));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.getAndIncrement());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public void deleteById(int id) {
    vacancies.remove(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription(), vacancy.getVisible(),
                        vacancy.getCityId(), vacancy.getFileId()))
                != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}
