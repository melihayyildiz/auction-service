package com.ayyildizbank.auctionservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorEnum implements ErrorResponse{
    AUCTION_WITH_PRODUCT_EXISTS( "AUCTION_WITH_PRODUCT_EXISTS", HttpStatus.BAD_REQUEST, "An auction exists for this product"),
    NO_PERMISSION( "NO_PERMISSION", HttpStatus.FORBIDDEN, "You do not have permission for this operation"),
    AUCTION_DOES_NOT_EXIST( "AUCTION_DOES_NOT_EXIST", HttpStatus.BAD_REQUEST, "Auction does not exist"),
    EMAIL_EXISTS( "EMAIL_EXISTS",HttpStatus.BAD_REQUEST, "Same email exists in the system"),
    ROLE_DOES_NOT_EXIST( "ROLE_DOES_NOT_EXIST",HttpStatus.BAD_REQUEST, "Role does not exist");

    final String key;
    final HttpStatus httpStatus;

    final String defaultMessage;

    ErrorEnum(String key, HttpStatus httpStatus, String defaultMessage) {
        this.key = key;
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
