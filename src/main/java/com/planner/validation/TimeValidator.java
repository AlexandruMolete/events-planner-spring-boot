package com.planner.validation;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimeValidator implements ConstraintValidator<ValidTime, String>{
	
	
	@Override
	public boolean isValid(final String time, final ConstraintValidatorContext context) {
		
		boolean isTimeFormatCorrect = Stream.of(time).anyMatch(timeInput -> {
			try {
                LocalTime.parse(timeInput, DateTimeFormatter.ISO_LOCAL_TIME);
                return true;
            } catch (Exception exp) {
                return false;
            }
		});
		
		return isTimeFormatCorrect;
	}

}
