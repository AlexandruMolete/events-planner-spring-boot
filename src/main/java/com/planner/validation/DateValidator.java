package com.planner.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<ValidDate, String>{

	
	@Override
	public boolean isValid(final String date, final ConstraintValidatorContext context) {
		
		boolean isDateFormatCorrect = Stream.of(date).allMatch(dateInput -> {
			try {
                LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
                return true;
            } catch (Exception exp) {
                return false;
            }
		});
		
		return isDateFormatCorrect;
	}

}
