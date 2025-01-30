package com.scaler.parking_lot.models;

public class Vehicle extends BaseModel{

    private final String registrationNumber;
    private final VehicleType vehicleType;

    public Vehicle(long id, String registrationNumber, VehicleType vehicleType) {
        super(id);
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}
