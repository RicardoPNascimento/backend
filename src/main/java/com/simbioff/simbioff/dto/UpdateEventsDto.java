package com.simbioff.simbioff.dto;

import com.simbioff.simbioff.enums.DayoffType;
import com.simbioff.simbioff.enums.StatusDayOff;

import javax.validation.constraints.FutureOrPresent;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

public class UpdateEventsDto implements Serializable {

    private static final long serialVersionUID = -7919208251865985316L;

    private StatusDayOff status;

    private DayoffType typeDay;

    @FutureOrPresent(message = "date must be in the future or present")
    private LocalDate date;

    private Time startTime;

    public boolean hasAtestado;

    public boolean isHasAtestado() {
        return hasAtestado;
    }

    public void setHasAtestado(boolean hasAtestado) {
        this.hasAtestado = hasAtestado;
    }

    private String justification;

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public StatusDayOff getStatus() {
        return status;
    }

    public void setStatus(StatusDayOff status) {
        this.status = status;
    }

    public DayoffType getTypeDay() {
        return typeDay;
    }

    public void setTypeDay(DayoffType typeDay) {
        this.typeDay = typeDay;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public UpdateEventsDto(StatusDayOff status, DayoffType typeDay, LocalDate date, Time startTime, boolean hasAtestado, String justification) {
        this.status = status;
        this.typeDay = typeDay;
        this.date = date;
        this.startTime = startTime;
        this.hasAtestado = hasAtestado;
        this.justification = justification;
    }

    public UpdateEventsDto() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateEventsDto that = (UpdateEventsDto) o;
        return status == that.status && typeDay == that.typeDay && Objects.equals(date, that.date) && Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(status, typeDay, date, startTime);
        result = 31 * result;
        return result;
    }
}
