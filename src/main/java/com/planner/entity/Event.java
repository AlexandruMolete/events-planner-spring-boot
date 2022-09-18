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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
//import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "event")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "time")
	private LocalTime time;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE,
			CascadeType.DETACH,CascadeType.REFRESH})
	@JoinTable(name = "accounts_events", 
	joinColumns = @JoinColumn(name = "event_id"), 
	inverseJoinColumns = @JoinColumn(name = "account_id"))
	private Collection<Account> accounts;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "event_id")
	private Collection<Reminder> reminders;

	public Event() {
	}

	public Event(int id, String eventTitle, LocalDate date, LocalTime time, Collection<Account> accounts,
			Collection<Reminder> reminders) {
		this.id = id;
		this.name = eventTitle;
		this.date = date;
		this.time = time;
		this.accounts = accounts;
		this.reminders = reminders;
	}

	public Event(String eventTitle, LocalDate date, LocalTime time, Collection<Account> accounts) {
		this.name = eventTitle;
		this.date = date;
		this.time = time;
		this.accounts = accounts;
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

	public Collection<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Collection<Account> accounts) {
		this.accounts = accounts;
	}

	public Collection<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(Collection<Reminder> reminders) {
		this.reminders = reminders;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", name=" + name + ", date=" + date + ", time=" + time + ", accounts="
				+ accounts + ", reminders=" + reminders + "]";
	}


	
}
