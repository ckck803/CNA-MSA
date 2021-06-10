package theater.aop;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import theater.exception.ApprovalBadRequestException;
import theater.exception.ExceptionResponse;
import theater.exception.ReservationNotFoundException;

import java.util.Date;

@RestController
@ControllerAdvice
public class ReservationResponseEntityHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ReservationNotFoundException.class)
    public final ResponseEntity<Object> handlerNotFoundExceptionHandler(Exception ex, WebRequest webRequest){
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .details(webRequest.getDescription(false))
                .build();

        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApprovalBadRequestException.class)
    public final ResponseEntity<Object> handlerBadRequestExceptionHandler(Exception ex, WebRequest webRequest){
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .details(webRequest.getDescription(false))
                .build();

        return new ResponseEntity<Object>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
