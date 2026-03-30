package com.app.servicefinder.controller;

import com.app.servicefinder.model.serviceProvider;
import com.app.servicefinder.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceProviderController {

    private final ServiceProviderRepository repo;

    @GetMapping
    public List<serviceProvider> getAll() { return repo.findAll(); }

    @PostMapping
    public serviceProvider create(@RequestBody serviceProvider s) { return repo.save(s); }
}