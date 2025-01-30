package com.scaler.parking_lot.services;

    import com.scaler.parking_lot.models.ParkingSpotStatus;
import com.scaler.parking_lot.respositories.ParkingLotRepository;
import com.scaler.parking_lot.exceptions.InvalidParkingLotException;
import com.scaler.parking_lot.models.ParkingFloor;
import com.scaler.parking_lot.models.ParkingLot;
import com.scaler.parking_lot.models.VehicleType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ParkingLotServiceImpl implements ParkingLotService {
    ParkingLotRepository repo;

    public ParkingLotServiceImpl(ParkingLotRepository repo) {
        this.repo = repo;
    }

    @Override
    public Map<ParkingFloor, Map<String, Integer>> getParkingLotCapacity(long parkingLotId, List<Long> parkingFloors, List<VehicleType> vehicleTypes) throws InvalidParkingLotException, InvalidParkingLotException {
        if (!repo.getParkingLotById(parkingLotId).isPresent()) {
            throw new InvalidParkingLotException();
        }
        ParkingLot parkingLot = repo.getParkingLotById(parkingLotId).get();
        Map<ParkingFloor, Map<String, Integer>> res = new HashMap<>();
        parkingLot.getParkingFloors().forEach(parkingFloor -> {
                    if ( parkingFloors == null || parkingFloors.isEmpty() || parkingFloors.contains(parkingFloor.getId())) {
                        Map<String, Integer> innerMap = new ConcurrentHashMap<>();
                        parkingFloor.getSpots().stream().filter(x -> x.getStatus() == ParkingSpotStatus.AVAILABLE).forEach(spot -> {
                            if (innerMap.containsKey(spot.getSupportedVehicleType().toString())) {
                                innerMap.put(spot.getSupportedVehicleType().toString(), innerMap.get(spot.getSupportedVehicleType().toString()) + 1);
                            } else {
                                innerMap.put(spot.getSupportedVehicleType().toString(), 1);
                            }
                        });
                        innerMap.keySet().forEach(vehicleType -> {
                            System.out.println("Key present in the parking lot: "+ vehicleType);
                            if (vehicleTypes != null && !vehicleTypes.isEmpty() && !vehicleTypes.contains(VehicleType.valueOf(vehicleType))) {
                                innerMap.remove(vehicleType);
                            }
                        });
//                        System.out.println("The parking floor is " + parkingFloor.getFloorNumber() + "number of keys present = " + innerMap.values().size());
                        res.put(parkingFloor, innerMap);

                    }
                    ;
                }
        );
        return res;

    }
}
