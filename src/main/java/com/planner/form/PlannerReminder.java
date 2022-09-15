package com.planner.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.planner.validation.ValidTime;

public class PlannerReminder {

	private int id;
	
	@ValidTime
	@NotNull(message = "is required")
	@Size(min = 1, message = "is required")
	private String time;
	
	private int eventId;

	public PlannerReminder() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	
}
