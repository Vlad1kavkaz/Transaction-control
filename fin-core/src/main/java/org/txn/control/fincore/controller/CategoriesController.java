package org.txn.control.fincore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.txn.control.fincore.api.CategoriesApiDelegate;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.services.crud.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoriesController implements CategoriesApiDelegate {

    private final CategoryService categoryService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Category>> categoriesGet() {
        log.info("Getting categories");
        return ResponseEntity.ok(categoryService.findAll());
    }
}
