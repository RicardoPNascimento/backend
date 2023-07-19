package com.simbioff.simbioff.dto;

import com.simbioff.simbioff.enums.DayoffType;
import com.simbioff.simbioff.models.EventsModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Time;
import java.util.UUID;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class EventsDto implements Serializable {

	private static final long serialVersionUID = 5526944616460827387L;
	private int eventId;
	private UUID idUser;
	@NotNull(message = "Tipo do dayoff é inválido ou em branco")
	private DayoffType typeDay;
	@NotNull(message = "Data é obrigatório")
	private String date;
	private Time startTime;
	private String justification;
	private String cardTitle;
	private String reason;
	private String status;
	private Boolean hasAtestado;

	public EventsDto(EventsModel entity) {
		this.eventId = entity.getEventId();
		this.idUser = entity.getIdUser();
		this.typeDay = entity.getTypeDay();
		this.date = entity.getDate().toString();
		this.startTime = entity.getStartTime();
		this.justification = entity.getJustification();
		this.reason = entity.getReason();
		this.status = entity.getStatus().toString();
		this.hasAtestado = entity.getHasAtestado();
	}

}
