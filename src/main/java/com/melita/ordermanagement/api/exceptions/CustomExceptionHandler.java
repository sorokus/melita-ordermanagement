package com.melita.ordermanagement.api.exceptions;

import com.melita.ordermanagement.base.exceptions.BusinessException;
import com.melita.ordermanagement.base.exceptions.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/*
 * This class customizes handling of standard and custom (BusinessException.class, SystemException.class) exceptions,
 * propagated to a level of Controller.
 * It wraps exceptions in a unified structured JSON for further propagation to the client.
 *
 * @author sorokus.dev@gmail.com
 */

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        String error = "Business Logic Exception";
        List<ApiSubError> subErrors = new ArrayList<>();
        subErrors.add(new BusinessError(ex.getMessage()));
        LOGGER.error("[" + error + "] " + ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, error, null, subErrors));
    }

    @ExceptionHandler({SystemException.class})
    public ResponseEntity<Object> handleBusinessException(SystemException ex) {
        String error = "System Exception";
        List<ApiSubError> subErrors = new ArrayList<>();
        subErrors.add(new SystemError(ex.getMessage(), ex.getCause()));
        LOGGER.error("[" + error + "] " + ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, null, subErrors));
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String error = "Malformed JSON request";
        LOGGER.debug("[" + error + "] " + ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String error = "Missing/Wrong input parameter";
        StringBuilder sb = new StringBuilder();
        List<ApiSubError> subErrors = new ArrayList<>();
        for (ObjectError err : ex.getBindingResult().getAllErrors()) {
            if (err instanceof FieldError) {
                subErrors.add(new ApiValidationError(err.getObjectName(),
                                                     ((FieldError) err).getField(),
                                                     ((FieldError) err).getRejectedValue(),
                                                     err.getDefaultMessage()));
            }
            else {
                sb.append(err.getDefaultMessage()).append("\n");
            }
        }
        if (sb.length() > 0) {sb.setLength(sb.length() - 1);}
        LOGGER.debug("[" + error + "] " + ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, sb.toString(), subErrors));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
