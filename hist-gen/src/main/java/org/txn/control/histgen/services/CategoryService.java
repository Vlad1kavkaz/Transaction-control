package org.txn.control.histgen.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.txn.control.histgen.dictionaries.CategoryDictionary;
import org.txn.control.histgen.model.Category;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final Queue<Category> categories = new LinkedList<>();
    private final List<Category> sendCategories;
    private int count = 0;

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>(categories);
        sendCategories.addAll(categoryList);
        categories.clear();
        return categoryList;
    }

    @Scheduled(fixedRate = 420000)
    private void scheduled() {
        if (CategoryDictionary.CATEGORIES.size() - 1 >= count) {
            categories.add(CategoryDictionary.CATEGORIES.get(count));
            count++;
        }
    }
}
