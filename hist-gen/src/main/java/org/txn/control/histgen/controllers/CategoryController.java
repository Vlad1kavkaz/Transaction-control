package org.txn.control.histgen.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.txn.control.histgen.model.Category;
import org.txn.control.histgen.services.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    public List<Category> getAllCategories() {
        log.info("Get all categories");
        return categoryService.getAllCategories();
    }
}
