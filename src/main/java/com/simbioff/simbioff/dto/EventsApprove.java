package com.simbioff.simbioff.dto;

import com.simbioff.simbioff.enums.StatusDayOff;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class EventsApprove {

    @NotNull(message = "Id do usuário é obrigatório")
    private UUID idUser;
    @NotNull(message = "Status do dayoff é obrigatório : APPROVED , REJECTED , PENDING")
    private StatusDayOff statusDayOff;
    private String reason;

    @Min(value = 1, message = "Id do dayoff é obrigatório")
    private int eventId;

    public UUID getIdUser() {
        return idUser;
    }

    public void setIdUser(UUID idUser) {
        this.idUser = idUser;
    }

    public StatusDayOff getStatusDayOff() {
        return statusDayOff;
    }

    public void setStatusDayOff(StatusDayOff statusDayOff) {
        this.statusDayOff = statusDayOff;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
