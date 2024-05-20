package ch.sku.karatescore.commons;

public enum PenaltyType {
    CHUI1("C1"), CHUI2("C2"), CHUI3("C2"), HANSOKU_CHUI("HC"), HANSOKU("H");

    private final String name;

    PenaltyType(String name) {
        this.name = name;
    }
}
