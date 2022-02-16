package alex.marinov;

import alex.marinov.error.ApiErrorResponse;
import alex.marinov.error.PlaceNotFoundException;
import alex.marinov.error.ServiceConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { PlaceNotFoundException.class })
    protected ResponseEntity<Object> handleConflict(PlaceNotFoundException ex, WebRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                "not-found", "No place found with ID " + ex.getId());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { ServiceConnectionException.class })
    protected ResponseEntity<Object> handleConflict(ServiceConnectionException ex, WebRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                "no-connection",  ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
