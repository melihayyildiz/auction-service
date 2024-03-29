package com.ayyildizbank.auctionservice.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse {
    private boolean success;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;


    public static CustomResponse success(Object data) {
        return CustomResponse.builder()
            .message("success")
            .data(data)
            .success(true)
            .build();
    }

    public static CustomResponse success() {
        return CustomResponse.builder()
            .message("success")
            .success(true)
            .build();
    }
}