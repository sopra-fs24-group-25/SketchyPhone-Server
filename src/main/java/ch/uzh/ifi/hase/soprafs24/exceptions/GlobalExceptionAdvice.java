package ch.uzh.ifi.hase.soprafs24.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

  private final Logger log = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

  @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
  protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
    // Construct a detailed error message or use the exception's message
    String detailedErrorMessage = ex.getMessage(); // You could also customize this message based on the exception type
    
    // Optionally, include more details in the response body
    Map<String, Object> bodyOfResponse = new HashMap<>();
    bodyOfResponse.put("timestamp", LocalDateTime.now());
    bodyOfResponse.put("status", HttpStatus.CONFLICT.value());
    bodyOfResponse.put("error", HttpStatus.CONFLICT.getReasonPhrase());
    bodyOfResponse.put("message", detailedErrorMessage);
    bodyOfResponse.put("path", ((ServletWebRequest)request).getRequest().getRequestURI()); // Path where the error occurred

    // Return a structured and informative error response
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
  }


  @ExceptionHandler(TransactionSystemException.class)
  public ResponseStatusException handleTransactionSystemException(Exception ex, HttpServletRequest request) {
    log.error("Request: {} raised {}", request.getRequestURL(), ex);
    return new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
  }

  // Keep this one disable for all testing purposes -> it shows more detail with
  // this one disabled
  @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
  public ResponseStatusException handleException(Exception ex) {
    log.error("Default Exception Handler -> caught:", ex);
    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
  }
}