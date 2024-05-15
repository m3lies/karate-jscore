package ch.sku.karatescore.services;

import ch.sku.karatescore.commons.ParticipantType;
import javafx.beans.property.BooleanProperty;

public class SenshuService {
    private final boolean isSenshu;

    public SenshuService(ParticipantType participantType, boolean isSenshu) {

        this.isSenshu = isSenshu;
    }

    public BooleanProperty getSenshuProperty(){
        return  null;
    }
}
