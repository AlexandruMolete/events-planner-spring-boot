package com.planner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class TestScript {
	
	public static void main(String[] args) {
		String time = "09:30";
		String date = "2022-09-11t";
		boolean isTimeFormatCorrect = Stream.of(time).anyMatch(t -> {
			try {
                LocalTime.parse(t, DateTimeFormatter.ISO_LOCAL_TIME);
                return true;
            } catch (Exception exp) {
            	exp.printStackTrace();
                return false;
            }
		});
		System.out.println(isTimeFormatCorrect);
		boolean isDateFormatCorrect = Stream.of(date).anyMatch(d -> {
			try {
                LocalDate.parse(d, DateTimeFormatter.ISO_LOCAL_DATE);
                return true;
            } catch (Exception exp) {
            	exp.printStackTrace();
                return false;
            }
		});
		System.out.println(isDateFormatCorrect);
	}

}