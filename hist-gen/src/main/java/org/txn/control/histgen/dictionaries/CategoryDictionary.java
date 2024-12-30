package org.txn.control.histgen.dictionaries;

import lombok.experimental.UtilityClass;
import org.txn.control.histgen.model.Category;

import java.util.List;

@UtilityClass
public class CategoryDictionary {

    public static final List<Category> CATEGORIES = List.of(
            Category.builder()
                    .name("Еда")
                    .build(),
            Category.builder()
                    .name("Транспорт")
                    .build(),
            Category.builder()
                    .name("Подписки")
                    .build(),
            Category.builder()
                    .name("Развлечения")
                    .build()
    );
}
