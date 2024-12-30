package org.txn.control.histgen.dictionaries;

import lombok.experimental.UtilityClass;
import org.txn.control.histgen.model.Bank;

import java.util.List;

@UtilityClass
public class BankDictionary {

    public static final List<Bank> BANKS = List.of(
            Bank.builder()
                    .name("Сбербанк")
                    .country("Россия")
                    .build(),
            Bank.builder()
                    .name("Т-Банк")
                    .country("Россия")
                    .build(),
            Bank.builder()
                    .name("Альфабанк")
                    .country("Россия")
                    .build(),
            Bank.builder()
                    .name("ВТБ")
                    .country("Россия")
                    .build(),
            Bank.builder()
                    .name("ПСБ")
                    .country("Россия")
                    .build()
    );
}
