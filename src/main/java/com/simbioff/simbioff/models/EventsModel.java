package com.simbioff.simbioff.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simbioff.simbioff.enums.DayoffType;
import com.simbioff.simbioff.enums.StatusDayOff;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventId;

    @Column(name = "id_user")
    private UUID idUser;

    @Column(name = "type_day")
    @Enumerated(EnumType.STRING)
    private DayoffType typeDay;

    @Column(name = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Column(name = "start_time")
    private java.sql.Time startTime;

    @Column(name = "end_time")
    private java.sql.Time endTime;

    @Column(name = "document")
    private Byte[] document;

    @Column(name = "created_at")
    @CreationTimestamp
    private java.sql.Timestamp createdAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusDayOff status;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Column(name = "card_title")
    private String cardTitle;

    @Column(name = "reason")
    private String reason;
    
    @Column(name = "has_atestado")
    private Boolean hasAtestado;
    
    private int mainEventAtestado;

    public EventsModel(EventsModel eventsModel) {
        this.eventId = eventsModel.getEventId();
        this.idUser = eventsModel.getIdUser();
        this.typeDay = eventsModel.getTypeDay();
        this.date = eventsModel.getDate();
        this.startTime = eventsModel.getStartTime();
        this.endTime = eventsModel.getEndTime();
        this.document = eventsModel.getDocument();
        this.createdAt = eventsModel.getCreatedAt();
        this.status = eventsModel.getStatus();
        this.justification = eventsModel.getJustification();
        this.cardTitle = eventsModel.getCardTitle();
        this.reason = eventsModel.getReason();
        this.hasAtestado = eventsModel.getHasAtestado();
        this.mainEventAtestado = eventsModel.getMainEventAtestado();
    }

}
