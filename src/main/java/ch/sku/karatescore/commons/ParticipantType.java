package ch.sku.karatescore.commons;

import lombok.Getter;

@Getter
public enum ParticipantType {
    AKA("Aka") , AO("Ao");

    private final String type;
    ParticipantType(String type) {
        this.type = type;
    }
}
