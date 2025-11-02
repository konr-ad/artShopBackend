package com.artshop.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ProblemDetail> handleMax(MaxUploadSizeExceededException ex,
                                                   HttpServletRequest req) {
        log.warn("413 on {} {} -> maxUploadSize={}B, Content-Length={}",
                req.getMethod(), req.getRequestURI(), ex.getMaxUploadSize(), req.getHeader("Content-Length"));
        long limit = ex.getMaxUploadSize(); // może być -1 zależnie od implementacji
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.PAYLOAD_TOO_LARGE);
        pd.setTitle("Payload Too Large");
        pd.setDetail("Upload too large. Limit: " + (limit >= 0 ? limit + " B" : "unknown"));
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(pd);
    }
}
