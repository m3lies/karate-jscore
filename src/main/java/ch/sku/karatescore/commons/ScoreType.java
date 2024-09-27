package ch.sku.karatescore.commons;

import lombok.Getter;

@Getter
public enum ScoreType {
    YUKO("1 Yuko "),
    WAZARI("2 Waza-ari"),
    IPPON("3 Ippon");

    private final String stringValue;

    // Constructor to associate string with each enum constant
    ScoreType(String stringValue) {
        this.stringValue = stringValue;
    }


}
