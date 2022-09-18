package com.planner.auxiliary;

public class EventNotification{
	
	private String eventName;
	
	private String remainingTime;

	public EventNotification(String eventName, String remainingTime) {
		this.eventName = eventName;
		this.remainingTime = remainingTime;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(String remainingTime) {
		this.remainingTime = remainingTime;
	}

	@Override
	public String toString() {
		return "Notification [eventName=" + eventName + ", remainingTime=" + remainingTime + "]";
	}
	
	
}
