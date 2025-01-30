package com.scaler.parking_lot.models;

import java.util.List;

public class ParkingLot extends BaseModel{

    private final List<ParkingFloor> parkingFloors;

    private final List<Gate> gates;

    private final String name;
    private final String address;

    public ParkingLot(long id, List<ParkingFloor> parkingFloors, List<Gate> gates, String name, String address) {
        super(id);
        this.parkingFloors = parkingFloors;
        this.gates = gates;
        this.name = name;
        this.address = address;
    }

    public List<ParkingFloor> getParkingFloors() {
        return parkingFloors;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
