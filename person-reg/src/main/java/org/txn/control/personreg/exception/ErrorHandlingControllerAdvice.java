package org.txn.control.personreg.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PersonNotFoundException.class)
    public String onPersonNotFoundException(PersonNotFoundException ex) {
        log.error(ex.getMessage());
        return ex.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RoleNotFoundException.class)
    public String onRoleNotFoundException(RoleNotFoundException ex) {
        log.error(ex.getMessage());
        return ex.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public String onRuntimeException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String onException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ex.getMessage();
    }
}
