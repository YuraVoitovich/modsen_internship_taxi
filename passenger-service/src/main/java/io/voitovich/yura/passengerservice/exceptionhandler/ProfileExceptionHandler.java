package io.voitovich.yura.passengerservice.exceptionhandler;

import io.voitovich.yura.passengerservice.exception.NoSuchRecordException;
import io.voitovich.yura.passengerservice.exception.NotUniquePhoneException;
import io.voitovich.yura.passengerservice.exception.NotValidUUIDException;
import io.voitovich.yura.passengerservice.exceptionhandler.model.ExceptionInfo;
import io.voitovich.yura.passengerservice.exceptionhandler.model.ValidationExceptionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProfileExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotValidUUIDException.class)
    @ResponseBody
    public ResponseEntity<ExceptionInfo> handleNotValidUUIDException(NotValidUUIDException exception) {
        log.info(String.format("Handled exception - %s", exception), exception);
        ExceptionInfo info = new ExceptionInfo(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(info, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchRecordException.class)
    public ResponseEntity<ExceptionInfo> handleNoSuchRecordException(NoSuchRecordException exception) {
        log.info(String.format("Handled exception - %s", exception), exception);
        ExceptionInfo info = new ExceptionInfo(exception.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(info, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotUniquePhoneException.class)
    public ResponseEntity<ExceptionInfo> handleNotUniquePhoneException(NotUniquePhoneException exception) {
        log.info(String.format("Handled exception - %s", exception), exception);
        ExceptionInfo info = new ExceptionInfo(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(info, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info(String.format("Handled exception - %s", exception), exception);
        ValidationExceptionInfo info = new ValidationExceptionInfo(HttpStatus.BAD_REQUEST);
        exception.getBindingResult().getAllErrors().forEach(error -> info
                .addError(((FieldError) error).getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(info, HttpStatus.BAD_REQUEST);
    }
}
