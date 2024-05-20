package ch.sku.karatescore.services;

import ch.sku.karatescore.commons.ParticipantType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class SenshuService {
    private final ObservableMap<ParticipantType, BooleanProperty> senshuProperties = FXCollections.observableHashMap();

    public SenshuService() {
        for (ParticipantType type : ParticipantType.values()) {
            senshuProperties.put(type, new SimpleBooleanProperty(false));
        }
    }

    public BooleanProperty getSenshuProperty(ParticipantType type) {
        return senshuProperties.get(type);
    }
}
