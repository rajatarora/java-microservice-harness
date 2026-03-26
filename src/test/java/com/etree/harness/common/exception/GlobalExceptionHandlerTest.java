package com.etree.harness.common.exception;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404AndBody() {
        ResponseEntity<Object> resp = handler.handleNotFound(new EntityNotFoundException("missing"), mock(WebRequest.class));
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();
        assertEquals(404, body.get("status"));
        assertEquals("Not Found", body.get("error"));
        assertEquals("missing", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleValidation_returnsBadRequestAndFieldMessages() throws Exception {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(new Object(), "obj");
        br.addError(new FieldError("obj", "name", "must not be blank"));

        MethodParameter mp = new MethodParameter(DummyController.class.getMethod("dummy", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mp, br);

        ResponseEntity<Object> resp = handler.handleValidation(ex, mock(WebRequest.class));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();
        assertEquals("Validation Failed", body.get("error"));
        @SuppressWarnings("unchecked")
        List<String> msgs = (List<String>) body.get("message");
        assertTrue(msgs.stream().anyMatch(m -> m.contains("name: must not be blank")));
    }

    @Test
    void handleConstraintViolation_returnsBadRequestWithViolations() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> cv = mock(ConstraintViolation.class);
        Path p = mock(Path.class);
        when(p.toString()).thenReturn("dto.age");
        when(cv.getPropertyPath()).thenReturn(p);
        when(cv.getMessage()).thenReturn("must be >= 0");

        Set<ConstraintViolation<?>> set = Collections.singleton((ConstraintViolation<?>) cv);
        ConstraintViolationException ex = new ConstraintViolationException(set);

        ResponseEntity<Object> resp = handler.handleConstraintViolation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();
        @SuppressWarnings("unchecked")
        List<String> msgs = (List<String>) body.get("message");
        assertTrue(msgs.stream().anyMatch(m -> m.contains("dto.age: must be >= 0")));
    }

    @Test
    void handleAll_returns500() {
        ResponseEntity<Object> resp = handler.handleAll(new RuntimeException("boom"), mock(WebRequest.class));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("boom", body.get("message"));
    }

    static class DummyController {
        public void dummy(String input) {}
    }
}
