package com.planner.form;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.planner.validation.ValidDate;
import com.planner.validation.ValidTime;

public class PlannerEvent {

	private int id;

	@NotNull(message = "is required")
	@Size(min = 1, message = "is required")
	private String name;

	@ValidDate
	@NotNull(message = "is required")
	@Size(min = 1, message = "is required")
	private String date;

	@ValidTime
	@NotNull(message = "is required")
	@Size(min = 1, message = "is required")
	private String time;

	public PlannerEvent() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
}
