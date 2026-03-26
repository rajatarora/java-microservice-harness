package com.etree.harness.common.exception;

import java.util.Collections;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GlobalExceptionHandlerIntegrationTest {

    @RestController
    static class TestController {
        @GetMapping("/not-found")
        public String notFound() {
            throw new EntityNotFoundException("missing-entity");
        }

        @GetMapping("/constraint")
        public String constraint() {
            @SuppressWarnings("unchecked")
            ConstraintViolation<Object> cv = mock(ConstraintViolation.class);
            Path p = mock(Path.class);
            when(p.toString()).thenReturn("dto.age");
            when(cv.getPropertyPath()).thenReturn(p);
            when(cv.getMessage()).thenReturn("must be >= 0");
            Set<ConstraintViolation<?>> set = Collections.singleton((ConstraintViolation<?>) cv);
            throw new ConstraintViolationException(set);
        }

        @GetMapping("/runtime")
        public String runtime() {
            throw new RuntimeException("boom");
        }

        @GetMapping("/validation")
        public String validation() throws NoSuchMethodException, MethodArgumentNotValidException {
            BeanPropertyBindingResult br = new BeanPropertyBindingResult(new Object(), "obj");
            br.addError(new FieldError("obj", "name", "must not be blank"));
            MethodParameter mp = new MethodParameter(DummyController.class.getMethod("dummy", String.class), 0);
            throw new MethodArgumentNotValidException(mp, br);
        }
    }

    static class DummyController {
        public void dummy(String input) {}
    }

    @Test
    void notFoundEndpoint_returnsMappedResponse() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler()).build();

        mvc.perform(get("/not-found").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("missing-entity"));
    }

    @Test
    void constraintEndpoint_returnsBadRequestWithMessages() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler()).build();

        mvc.perform(get("/constraint").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message[0]").value(containsString("dto.age: must be >= 0")));
    }

    @Test
    void runtimeEndpoint_returnsInternalServerError() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler()).build();

        mvc.perform(get("/runtime").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("boom"));
    }

    @Test
    void validationEndpoint_returnsBadRequestWithFieldMessages() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler()).build();

        mvc.perform(get("/validation").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message[0]").value(containsString("name: must not be blank")));
    }
}
