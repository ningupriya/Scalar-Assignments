package com.scaler.parking_lot.respositories;

import com.scaler.parking_lot.models.ParkingLot;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLotRepositoryImpl implements ParkingLotRepository {
    private ConcurrentHashMap<Long, ParkingLot> parkingLot = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, ParkingLot> gateIdParkingLot = new ConcurrentHashMap<>();

    @Override
    public Optional<ParkingLot> getParkingLotByGateId(long gateId) {
        return Optional.ofNullable(gateIdParkingLot.get(gateId));
    }

    @Override
    public Optional<ParkingLot> getParkingLotById(long id) {
        return Optional.ofNullable(parkingLot.get(id));
    }

    @Override
    public ParkingLot save(ParkingLot parkingLot) {
        this.parkingLot.put(parkingLot.getId(), parkingLot);
        parkingLot.getGates().forEach(gate -> {
            this.gateIdParkingLot.put(gate.getId(), parkingLot);
        });
        return parkingLot;
    }
}
