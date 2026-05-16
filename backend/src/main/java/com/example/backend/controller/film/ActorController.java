package com.example.backend.controller.film;


import com.example.backend.dto.projection.ActorProjection;
import com.example.backend.service.film.ActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    @GetMapping
    public Page<ActorProjection> getAllActors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return actorService.getAllActors(PageRequest.of(page, size));
    }

    @GetMapping("/basic")
    public List<ActorProjection> getAllActorsBasic() {
        return actorService.getAllActorsBasic();
    }

    @GetMapping("/search")
    public Page<ActorProjection> searchActor(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return actorService.searchActor(name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ActorProjection getActorById(@PathVariable Integer id) {
        return actorService.getActorById(id);
    }
}
