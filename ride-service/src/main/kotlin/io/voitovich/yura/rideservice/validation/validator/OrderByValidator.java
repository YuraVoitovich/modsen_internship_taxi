package io.voitovich.yura.rideservice.validation.validator;

import io.voitovich.yura.rideservice.validation.annotations.OrderBy;
import io.voitovich.yura.rideservice.validation.annotations.SortExclude;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.Arrays;

public class OrderByValidator implements ConstraintValidator<OrderBy, String> {


    private Class<?> aClass;

    @Override
    public boolean isValid(String orderBy, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(aClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(SortExclude.class))
                .map(Field::getName)
                .anyMatch(fieldName -> fieldName.equals(orderBy));
    }

    @Override
    public void initialize(OrderBy constraintAnnotation) {
        aClass = constraintAnnotation.value();
    }
}
