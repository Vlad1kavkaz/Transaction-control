package org.txn.control.histgen.dictionaries;

import org.junit.jupiter.api.Test;
import org.txn.control.histgen.model.Bank;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankDictionaryTest {

    @Test
    void bankDictionary_ShouldContainExpectedBanks() {
        // Act
        List<Bank> banks = BankDictionary.BANKS;

        // Assert
        assertThat(banks).isNotEmpty();
        assertThat(banks).hasSize(5);

        assertThat(banks).extracting(Bank::getName)
                .containsExactly("Сбербанк", "Т-Банк", "Альфабанк", "ВТБ", "ПСБ");

        assertThat(banks).extracting(Bank::getCountry)
                .containsOnly("Россия");
    }

    @Test
    void utilityClass_ShouldThrowException_WhenInstantiated() {
        assertThrows(IllegalAccessException.class, () -> {
            BankDictionary bankDictionary = BankDictionary.class.getDeclaredConstructor().newInstance();
        });
    }
}
