package com.payment.gateway.exceptions;

import com.payment.gateway.utils.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomRestExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        ResponseModel apiError =
                new ResponseModel("01", errors.get(0));
        return handleExceptionInternal(
                ex, apiError, headers, HttpStatus.OK, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ResponseModel apiError =
                new ResponseModel("415","Media type not supported.");
        return handleExceptionInternal(
                ex, apiError, headers, HttpStatus.OK, request);
      // return super.handleHttpMediaTypeNotSupported(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ResponseModel apiError =
                new ResponseModel("405","Method not allowed.");
        return handleExceptionInternal(
                ex, apiError, headers, HttpStatus.OK, request);
        //return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
    }



    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
            ResponseModel apiError = new ResponseModel(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getLocalizedMessage(), "error occurred");
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }



    //    @Override
//    protected ResponseEntity<Object> handleMissingServletRequestParameter(
//            MissingServletRequestParameterException ex, HttpHeaders headers,
//            HttpStatus status, WebRequest request) {
//        String error = ex.getParameterName() + " parameter is missing";
//
//        ApiError apiError =
//                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
//        return new ResponseEntity<Object>(
//                apiError, new HttpHeaders(), apiError.getStatus());
//    }
//
//    @ExceptionHandler({ConstraintViolationException.class})
//    public ResponseEntity<Object> handleConstraintViolation(
//            ConstraintViolationException ex, WebRequest request) {
//        List<String> errors = new ArrayList<String>();
//        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
//            errors.add(violation.getRootBeanClass().getName() + " " +
//                    violation.getPropertyPath() + ": " + violation.getMessage());
//        }
//
//        ApiError apiError =
//                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
//        return new ResponseEntity<Object>(
//                apiError, new HttpHeaders(), apiError.getStatus());
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
//            HttpRequestMethodNotSupportedException ex,
//            HttpHeaders headers,
//            HttpStatus status,
//            WebRequest request) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(ex.getMethod());
//        builder.append(
//                " method is not supported for this request. Supported methods are ");
//        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
//
//        ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED,
//                ex.getLocalizedMessage(), builder.toString());
//        return new ResponseEntity<Object>(
//                apiError, new HttpHeaders(), apiError.getStatus());
//    }
//
//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
//        ApiError apiError = new ApiError(
//                HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
//        return new ResponseEntity<Object>(
//                apiError, new HttpHeaders(), apiError.getStatus());
//    }
}
