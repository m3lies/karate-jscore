package ch.sku.karatescore.model;

import ch.sku.karatescore.commons.ParticipantType;
import lombok.Data;


@Data
public class Participant {
    private ParticipantType participantType;

    public Participant(ParticipantType participantType) {
        this.participantType = participantType;

    }

}
