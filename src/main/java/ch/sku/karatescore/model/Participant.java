package ch.sku.karatescore.model;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.commons.ScoreType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Data;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;


@Data
public class Participant {
    private ParticipantType participantType;





    public Participant(ParticipantType participantType) {
        this.participantType = participantType;

    }



}
