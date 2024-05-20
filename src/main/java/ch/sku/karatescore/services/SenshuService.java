package ch.sku.karatescore.services;

import ch.sku.karatescore.commons.ParticipantType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SenshuService {
    private final BooleanProperty akaSenshu = new SimpleBooleanProperty(false);
    private final BooleanProperty aoSenshu = new SimpleBooleanProperty(false);

    public BooleanProperty getSenshuProperty(ParticipantType participantType) {
        if (participantType == ParticipantType.AKA) {
            return akaSenshu;
        } else {
            return aoSenshu;
        }
    }

    public void toggleSenshu(ParticipantType participantType) {
        if (participantType == ParticipantType.AKA) {
            akaSenshu.set(!akaSenshu.get());
        } else {
            aoSenshu.set(!aoSenshu.get());
        }
    }
}
