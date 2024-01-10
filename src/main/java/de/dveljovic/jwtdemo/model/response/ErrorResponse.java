package de.dveljovic.jwtdemo.model.response;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus httpStatus, String message) {

}
