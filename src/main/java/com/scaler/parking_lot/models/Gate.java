package com.scaler.parking_lot.models;

public class Gate extends BaseModel{

    private final String name;
    private final GateType type;

    private ParkingAttendant parkingAttendant;

    public Gate(long id, String name, GateType type) {
        super(id);
        this.name = name;
        this.type = type;
    }

    public Gate(long id, String name, GateType type, ParkingAttendant parkingAttendant) {
        super(id);
        this.name = name;
        this.type = type;
        this.parkingAttendant = parkingAttendant;
    }

    public String getName() {
        return name;
    }

    public GateType getType() {
        return type;
    }

    public ParkingAttendant getParkingAttendant() {
        return parkingAttendant;
    }

    public void setParkingAttendant(ParkingAttendant parkingAttendant) {
        this.parkingAttendant = parkingAttendant;
    }
}
