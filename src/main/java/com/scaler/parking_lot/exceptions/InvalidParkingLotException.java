package com.scaler.parking_lot.exceptions;

public class InvalidParkingLotException extends Exception {
    public InvalidParkingLotException() {
    }

    public InvalidParkingLotException(String message) {
        super(message);
    }
}
