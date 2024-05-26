package ch.sku.karatescore.commons;

import lombok.Getter;

@Getter
public enum PenaltyType {
    CHUI1("C1"), CHUI2("C2"), CHUI3("C3"), HANSOKU_CHUI("HC"), HANSOKU("H");

    private final String name;

    PenaltyType(String name) {
        this.name = name;
    }

}
