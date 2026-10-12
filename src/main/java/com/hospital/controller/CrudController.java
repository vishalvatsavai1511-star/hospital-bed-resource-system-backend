package com.hospital.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Reusable GET / POST / PUT / DELETE for simple entities. */
public abstract class CrudController<T> {
    protected abstract JpaRepository<T, Long> repo();

    protected String[] ignoredOnUpdate() { return new String[]{"id"}; }

    @GetMapping
    public List<T> all() { return repo().findAll(Sort.by(Sort.Direction.DESC, "id")); }

    @GetMapping("/{id}")
    public T one(@PathVariable Long id) {
        return repo().findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public T create(@RequestBody T body) { return repo().save(body); }

    @PutMapping("/{id}")
    public T update(@PathVariable Long id, @RequestBody T body) {
        T existing = one(id);
        BeanUtils.copyProperties(body, existing, ignoredOnUpdate());
        return repo().save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        one(id);
        repo().deleteById(id);
    }
}
