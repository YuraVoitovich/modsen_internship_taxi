package io.voitovich.yura.driverservice.exceptionhandler;

import io.voitovich.yura.driverservice.exception.NoSuchRecordException;
import io.voitovich.yura.driverservice.exception.NotUniquePhoneException;
import io.voitovich.yura.driverservice.exception.NotValidUUIDException;
import io.voitovich.yura.driverservice.exceptionhandler.model.ExceptionInfo;
import io.voitovich.yura.driverservice.exceptionhandler.model.ValidationExceptionInfo;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class DriverProfileExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotValidUUIDException.class)
    public ResponseEntity<ExceptionInfo> handleNotValidUUIDException(NotValidUUIDException exception) {
        log.info(String.format("Handled exception - %s", exception), exception);
        ExceptionInfo info = ExceptionInfo
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.badRequest().body(info);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionInfo> handleConstraintViolationException(ConstraintViolationException exception) {
        log.info(String.format("Handled exception - %s", exception), exception);
        ExceptionInfo info = ExceptionInfo
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(info);
    }

    @ExceptionHandler(NoSuchRecordException.class)
    public ResponseEntity<ExceptionInfo> handleNoSuchRecordException(NoSuchRecordException exception) {
        log.info(String.format("Handled exception - %s", exception), exception);
        ExceptionInfo info = ExceptionInfo
                .builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(info);
    }

    @ExceptionHandler(NotUniquePhoneException.class)
    public ResponseEntity<ExceptionInfo> handleNotUniquePhoneException(NotUniquePhoneException exception) {
        log.info(String.format("Handled exception - %s", exception), exception);
        ExceptionInfo info = ExceptionInfo
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(info);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info(String.format("Handled exception - %s", exception), exception);
        var infoBuilder = ValidationExceptionInfo
                .builder()
                .status(HttpStatus.BAD_REQUEST);

        exception.getBindingResult()
                .getAllErrors()
                .forEach(error -> infoBuilder
                        .error(((FieldError) error).getField(), error.getDefaultMessage()));
        ValidationExceptionInfo info = infoBuilder.build();
        return ResponseEntity.badRequest().body(info);
    }
}
