package org.sky_pro.team_work.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RecommendationServiceExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        String message = ex.getMessage();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        log.error(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        String message;
        Map<String, Object> errorResponse = new HashMap<>();
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(UUID.class)) {
            message = "Полученный id не является UUID";
        } else {
            message = "Ошибка: " + ex.getMessage();
        }
        errorResponse.put("message", message);
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        log.error(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        FieldError firstError = ex.getBindingResult().getFieldErrors().getFirst();
        String message = firstError.getDefaultMessage() + ": " + firstError.getField();
        errorResponse.put("message", message);
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        log.error("Ошибка валидации: {}", message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        String message;
        Map<String, Object> errorResponse = new HashMap<>();
        if (ex.getCause() instanceof InvalidFormatException ife) {
            String fieldName = ife.getPath().getFirst().getFieldName();
            message = "Некорректное значение в поле '" + fieldName + "': " + ife.getValue();
            errorResponse.put("message", message);
        } else if (ex.getCause() instanceof JsonParseException jpe) {
            message = "Ошибка парсинга JSON: " + jpe.getOriginalMessage();
            errorResponse.put("message", message);
        } else if (ex.getCause() instanceof JsonMappingException jme) {
            String fieldName = jme.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));
            message = "Ошибка маппинга JSON: " + jme.getOriginalMessage() + " в поле '" + fieldName + "'";
            errorResponse.put("message", message);
        } else {
            message = "Ошибка чтения сообщения: " + ex.getMessage();
            errorResponse.put("message", message);
        }
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        log.error(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        String message = "Ошибка целостности данных: " + ex.getMessage();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        log.error(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRuleNotFoundException(
            RuleNotFoundException ex) {
        String message = ex.getMessage();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        log.error(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
