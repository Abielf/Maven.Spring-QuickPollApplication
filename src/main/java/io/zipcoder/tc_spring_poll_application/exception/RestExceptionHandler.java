package io.zipcoder.tc_spring_poll_application.exception;


import io.zipcoder.tc_spring_poll_application.dtos.error.ErrorDetail;
import io.zipcoder.tc_spring_poll_application.dtos.error.ValidationError;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

    @Inject
    MessageSource messageSource;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException rnfe, HttpServletRequest request){
        ErrorDetail deetsies=new ErrorDetail();
        deetsies.setTimeStamp(new Date().getTime());
        deetsies.setTitle("Resource Not Found");
        deetsies.setStatus(HttpStatus.NOT_FOUND.value());
        deetsies.setDetail(rnfe.getMessage());
        deetsies.setDeveloperMessage(rnfe.getClass().getName());

        return new ResponseEntity<>(deetsies,null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?>
    handleValidationError(  MethodArgumentNotValidException manve,
                            HttpServletRequest request){
        ErrorDetail deetsies=new ErrorDetail();
        deetsies.setTimeStamp(new Date().getTime());
        deetsies.setTitle("Validation Failed, come on already");
        deetsies.setStatus(HttpStatus.BAD_REQUEST.value());
        deetsies.setDetail("Input Validation Failed, get with it");
        deetsies.setDeveloperMessage(manve.getClass().getName());
        List<FieldError> fieldErrors =  manve.getBindingResult().getFieldErrors();
        for(FieldError fe : fieldErrors) {

            List<ValidationError> validationErrorList = deetsies.getErrors().get(fe.getField());
            if(validationErrorList == null) {
                validationErrorList = new ArrayList<ValidationError>();
                deetsies.getErrors().put(fe.getField(), validationErrorList);
            }
            ValidationError validationError = new ValidationError();
            validationError.setCode(fe.getCode());
            validationError.setMessage(messageSource.getMessage(fe,null));
            validationErrorList.add(validationError);
        }
        return new ResponseEntity<>(deetsies, null, HttpStatus.BAD_REQUEST);
    }

}