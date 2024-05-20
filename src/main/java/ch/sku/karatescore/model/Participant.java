package ch.sku.karatescore.model;

import ch.sku.karatescore.commons.ParticipantType;


public class Participant {
    private ParticipantType participantType;

    public Participant(ParticipantType participantType) {
        this.participantType = participantType;

    }

    public ParticipantType getParticipantType() {
        return participantType;
    }
}
