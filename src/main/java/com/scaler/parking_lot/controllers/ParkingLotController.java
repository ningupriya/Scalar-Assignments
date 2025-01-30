package com.scaler.parking_lot.controllers;

import com.scaler.parking_lot.dtos.GetParkingLotCapacityRequestDto;
import com.scaler.parking_lot.dtos.GetParkingLotCapacityResponseDto;
import com.scaler.parking_lot.dtos.Response;
import com.scaler.parking_lot.dtos.ResponseStatus;
import com.scaler.parking_lot.exceptions.GetParkingLotRequestValidationException;
import com.scaler.parking_lot.exceptions.InvalidParkingLotException;
import com.scaler.parking_lot.models.ParkingFloor;
import com.scaler.parking_lot.models.VehicleType;
import com.scaler.parking_lot.services.ParkingLotService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    public GetParkingLotCapacityResponseDto getParkingLotCapacity(GetParkingLotCapacityRequestDto getParkingLotCapacityRequestDto) {
        GetParkingLotCapacityResponseDto result = new GetParkingLotCapacityResponseDto();
        if(getParkingLotCapacityRequestDto.getParkingLotId() < 0) result.setResponse(new Response(ResponseStatus.FAILURE,"Invalid parking lot id"));
        try{
            List<VehicleType> vt = getParkingLotCapacityRequestDto.getVehicleTypes() == null || getParkingLotCapacityRequestDto.getVehicleTypes().isEmpty()? new LinkedList<>() : getParkingLotCapacityRequestDto.getVehicleTypes().stream().map(x-> VehicleType.valueOf(x)).collect(Collectors.toList());
        result.setCapacityMap(parkingLotService
                .getParkingLotCapacity(getParkingLotCapacityRequestDto.getParkingLotId(), getParkingLotCapacityRequestDto.getParkingFloorIds(), vt));
        result.setResponse(new Response(ResponseStatus.SUCCESS,"The request is succesfull"));
        }
        catch (InvalidParkingLotException e) {
            result.setResponse(new Response(ResponseStatus.FAILURE,"No Parking Lot found"));
        }
        return result;
    }

}
