package org.txn.control.histgen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.txn.control.histgen.model.Bank;
import org.txn.control.histgen.model.Category;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DictionaryConfig {

    @Bean
    public List<Bank> banks() {
        return new ArrayList<>();
    }

    @Bean
    public List<Category> categories() {
        return new ArrayList<>();
    }
}
