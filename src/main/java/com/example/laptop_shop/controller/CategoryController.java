package com.example.laptop_shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.laptop_shop.service.categoryservice.CategoryServiceUserInterface;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryServiceUserInterface cateService;
    @GetMapping
    public ResponseEntity<?> getAllCategory()
    {
        return ResponseEntity.ok().body(cateService.getAllCategory());
    }
}
