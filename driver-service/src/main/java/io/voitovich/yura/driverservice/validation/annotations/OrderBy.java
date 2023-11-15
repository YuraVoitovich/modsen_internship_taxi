package io.voitovich.yura.driverservice.validation.annotations;


import io.voitovich.yura.driverservice.validation.validator.OrderByValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrderByValidator.class)
@Documented
public @interface OrderBy {

    Class<?> value();

    String message() default "OrderBy field is invalid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
