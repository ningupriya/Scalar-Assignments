package com.scaler.parking_lot.models;

import java.util.List;

public class ParkingFloor extends BaseModel{

    public ParkingFloor(int id, List<ParkingSpot> spots, int floorNumber, FloorStatus status) {
        super(id);
        this.spots = spots;
        this.floorNumber = floorNumber;
        this.status = status;
    }

    private final List<ParkingSpot> spots;
    private final int floorNumber;

    private FloorStatus status;

    public final List<ParkingSpot> getSpots() {
        return spots;
    }

    public final int getFloorNumber() {
        return floorNumber;
    }

    public final FloorStatus getStatus() {
        return status;
    }

    public void setStatus(FloorStatus status) {
        this.status = status;
    }
}
