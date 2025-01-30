package com.scaler.parking_lot.models;

public class ParkingSpot extends BaseModel{

    private final int number;
    private final VehicleType supportedVehicleType;

    private ParkingSpotStatus status;

    public ParkingSpot(int id, int number, VehicleType supportedVehicleType) {
        super(id);
        this.number = number;
        this.supportedVehicleType = supportedVehicleType;
        this.status = ParkingSpotStatus.AVAILABLE;
    }

    public int getNumber() {
        return number;
    }

    public VehicleType getSupportedVehicleType() {
        return supportedVehicleType;
    }

    public ParkingSpotStatus getStatus() {
        return status;
    }

    public void setStatus(ParkingSpotStatus status) {
        this.status = status;
    }
}
