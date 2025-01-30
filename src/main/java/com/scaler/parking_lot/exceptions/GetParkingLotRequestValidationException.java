package com.scaler.parking_lot.exceptions;

public class GetParkingLotRequestValidationException extends Exception {
    public GetParkingLotRequestValidationException() {
    }

    public GetParkingLotRequestValidationException(String message) {
        super(message);
    }
}
