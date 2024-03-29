package com.ayyildizbank.auctionservice.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
public class ApplicationException extends RuntimeException {

    static final long serialVersionUID = 1L;

    private final ErrorResponse errorResponse;
    private final Map<String, Object> messageArguments;

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public ApplicationException(ErrorResponse errorResponse, Map<String, Object> messageArguments) {
        this.errorResponse = errorResponse;
        this.messageArguments = messageArguments;
    }

    public ApplicationException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
        this.messageArguments = null;
    }

    @Override
    public String getMessage() {
        return errorResponse.getMessage();
    }
}
