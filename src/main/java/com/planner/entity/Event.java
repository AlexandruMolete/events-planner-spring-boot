package com.planner.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "event")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "title")
	private String eventTitle;
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "time")
	private LocalTime time;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE,
			CascadeType.DETACH,CascadeType.REFRESH})
	@JoinColumn(name = "account_id")
	private Account account;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "event_id")
	private Collection<Reminder> reminders;

	public Event() {
	}

	public Event(int id, String eventTitle, LocalDate date, LocalTime time, Account account,
			Collection<Reminder> reminders) {
		this.id = id;
		this.eventTitle = eventTitle;
		this.date = date;
		this.time = time;
		this.account = account;
		this.reminders = reminders;
	}

	public Event(String eventTitle, LocalDate date, LocalTime time, Account account) {
		this.eventTitle = eventTitle;
		this.date = date;
		this.time = time;
		this.account = account;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Collection<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(Collection<Reminder> reminders) {
		this.reminders = reminders;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", eventTitle=" + eventTitle + ", date=" + date + ", time=" + time + ", account="
				+ account + ", reminders=" + reminders + "]";
	}


	
}
