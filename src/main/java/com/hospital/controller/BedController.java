package com.hospital.controller;

import com.hospital.model.Bed;
import com.hospital.model.Enums.BedStatus;
import com.hospital.repository.BedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beds")
@RequiredArgsConstructor
public class BedController extends CrudController<Bed> {
    private final BedRepository repo;
    @Override protected JpaRepository<Bed, Long> repo() { return repo; }

    /** Admin can switch a free bed between AVAILABLE and MAINTENANCE. */
    @PatchMapping("/{id}/status")
    public Bed setStatus(@PathVariable Long id, @RequestParam BedStatus status) {
        Bed b = one(id);
        if (b.getStatus() == BedStatus.OCCUPIED || status == BedStatus.OCCUPIED)
            throw new IllegalStateException("Occupied status is managed by allocations only");
        b.setStatus(status);
        return repo.save(b);
    }
}
