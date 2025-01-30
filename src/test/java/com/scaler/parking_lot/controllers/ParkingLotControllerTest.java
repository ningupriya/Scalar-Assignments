package com.scaler.parking_lot.controllers;

import com.scaler.parking_lot.dtos.GetParkingLotCapacityRequestDto;
import com.scaler.parking_lot.dtos.GetParkingLotCapacityResponseDto;
import com.scaler.parking_lot.dtos.ResponseStatus;
import com.scaler.parking_lot.models.*;
import com.scaler.parking_lot.respositories.ParkingLotRepository;
import com.scaler.parking_lot.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Constructor;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingLotControllerTest {

    private ParkingLotRepository parkingLotRepository;
    private ParkingLotService parkingLotService;
    private ParkingLotController parkingLotController;

    @BeforeEach
    public void setupTest() throws Exception {
        initializeComponents();
    }

    private void initializeComponents() throws Exception {
        initializeRepositories();
        initializeParkingLotService();
        initializeParkingLotController();
    }

    private <T> T createInstance(Class<T> interfaceClass, Reflections reflections) throws Exception {
        Set<Class<? extends T>> implementations = reflections.getSubTypesOf(interfaceClass);
        if (implementations.isEmpty()) {
            throw new Exception("No implementation for " + interfaceClass.getSimpleName() + " found");
        }

        Class<? extends T> implementationClass = implementations.iterator().next();
        Constructor<? extends T> constructor = implementationClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private <T> T createInstanceWithArgs(Class<T> interfaceClass, Reflections reflections, List<Object> dependencies) throws Exception {
        Set<Class<? extends T>> implementations = reflections.getSubTypesOf(interfaceClass);
        if (implementations.isEmpty()) {
            throw new Exception("No implementation for " + interfaceClass.getSimpleName() + " found");
        }
        Class<? extends T> implementationClass = implementations.iterator().next();
        Constructor<?>[] constructors = implementationClass.getConstructors();
        Constructor<?> constructor = Arrays.stream(constructors)
                .filter(constructor1 -> constructor1.getParameterCount() == dependencies.size())
                .findFirst().orElseThrow(() -> new Exception("No constructor with " + dependencies.size() + " arguments found"));
        constructor.setAccessible(true);
        Object[] args = new Object[constructor.getParameterCount()];
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            for (Object dependency : dependencies) {
                if (constructor.getParameterTypes()[i].isInstance(dependency)) {
                    args[i] = dependency;
                    break;
                }
            }
        }
        return (T) constructor.newInstance(args);
    }

    private void initializeRepositories() throws Exception {
        Reflections repositoryReflections = new Reflections(ParkingLotRepository.class.getPackageName(), new SubTypesScanner(false));
        this.parkingLotRepository = createInstance(ParkingLotRepository.class, repositoryReflections);
    }

    private void initializeParkingLotService() throws Exception {
        Reflections serviceReflections = new Reflections(ParkingLotService.class.getPackageName(), new SubTypesScanner(false));
        this.parkingLotService = createInstanceWithArgs(ParkingLotService.class, serviceReflections, Arrays.asList(this.parkingLotRepository));
    }

    private void initializeParkingLotController() throws Exception {
        this.parkingLotController = new ParkingLotController(this.parkingLotService);
    }

    @Test
    public void testGetParkingLotCapacityWithNegativeParkingLotId(){
        setupTest(2, Map.of(VehicleType.CAR, 2, VehicleType.TRUCK, 2), 1, 1, "Bangalore");
        GetParkingLotCapacityRequestDto getParkingLotCapacityRequestDto = new GetParkingLotCapacityRequestDto();
        getParkingLotCapacityRequestDto.setParkingLotId(-1);
        getParkingLotCapacityRequestDto.setParkingFloorIds(null);
        getParkingLotCapacityRequestDto.setVehicleTypes(null);
        GetParkingLotCapacityResponseDto parkingLotCapacity = parkingLotController.getParkingLotCapacity(getParkingLotCapacityRequestDto);
        assertEquals(parkingLotCapacity.getResponse().getResponseStatus(), ResponseStatus.FAILURE);
    }

    @Test
    public void testGetParkingLotCapacityWithInvalidParkingLotId(){
        setupTest(2, Map.of(VehicleType.CAR, 2, VehicleType.TRUCK, 2), 1, 1, "Bangalore");
        GetParkingLotCapacityRequestDto getParkingLotCapacityRequestDto = new GetParkingLotCapacityRequestDto();
        getParkingLotCapacityRequestDto.setParkingLotId(2);
        getParkingLotCapacityRequestDto.setParkingFloorIds(null);
        getParkingLotCapacityRequestDto.setVehicleTypes(null);
        GetParkingLotCapacityResponseDto parkingLotCapacity = parkingLotController.getParkingLotCapacity(getParkingLotCapacityRequestDto);
        assertEquals(parkingLotCapacity.getResponse().getResponseStatus(), ResponseStatus.FAILURE);
    }

    @Test
    public void testGetParkingLotCapacityWithNoVehiclesParked(){
        setupTest(2, Map.of(VehicleType.CAR, 2, VehicleType.TRUCK, 2), 1, 1, "Bangalore");
        GetParkingLotCapacityRequestDto getParkingLotCapacityRequestDto = new GetParkingLotCapacityRequestDto();
        getParkingLotCapacityRequestDto.setParkingLotId(1);
        getParkingLotCapacityRequestDto.setParkingFloorIds(null);
        getParkingLotCapacityRequestDto.setVehicleTypes(null);
        GetParkingLotCapacityResponseDto parkingLotCapacity = parkingLotController.getParkingLotCapacity(getParkingLotCapacityRequestDto);
        assertEquals(parkingLotCapacity.getResponse().getResponseStatus(), ResponseStatus.SUCCESS);
        assertEquals(parkingLotCapacity.getCapacityMap().size(), 2);

        Optional<ParkingLot> parkingLotOptional = parkingLotRepository.getParkingLotById(1);
        ParkingLot parkingLot = parkingLotOptional.get();
        ParkingFloor parkingFloor = parkingLot.getParkingFloors().get(0);
        assertNotNull(parkingLotCapacity.getCapacityMap().get(parkingFloor));
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingFloor).get(VehicleType.CAR.name()), 2);
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingFloor).get(VehicleType.TRUCK.name()), 2);

        parkingFloor = parkingLot.getParkingFloors().get(1);
        assertNotNull(parkingLotCapacity.getCapacityMap().get(parkingFloor));
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingFloor).get(VehicleType.CAR.name()), 2);
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingFloor).get(VehicleType.TRUCK.name()), 2);
    }

    @Test
    public void testGetParkingLotCapacityWithFewSpotsOccupied(){
        setupTest(2, Map.of(VehicleType.CAR, 2, VehicleType.TRUCK, 2), 1, 1, "Bangalore");
        Optional<ParkingLot> parkingLotByGateId = parkingLotRepository.getParkingLotByGateId(1);
        ParkingLot parkingLot = parkingLotByGateId.get();
        // Set car on floor 1, spot 1
        ParkingFloor parkingFloor = parkingLot.getParkingFloors().get(0);
        parkingFloor.getSpots().stream().filter(spot -> spot.getSupportedVehicleType().equals(VehicleType.CAR)).findFirst().get().setStatus(ParkingSpotStatus.OCCUPIED);
        // Set truck on floor 2, spot 1
        parkingFloor = parkingLot.getParkingFloors().get(1);
        parkingFloor.getSpots().stream().filter(spot -> spot.getSupportedVehicleType().equals(VehicleType.TRUCK)).findFirst().get().setStatus(ParkingSpotStatus.OCCUPIED);


        GetParkingLotCapacityRequestDto getParkingLotCapacityRequestDto = new GetParkingLotCapacityRequestDto();
        getParkingLotCapacityRequestDto.setParkingLotId(1);
        getParkingLotCapacityRequestDto.setParkingFloorIds(null);
        getParkingLotCapacityRequestDto.setVehicleTypes(null);
        GetParkingLotCapacityResponseDto parkingLotCapacity = parkingLotController.getParkingLotCapacity(getParkingLotCapacityRequestDto);
        assertEquals(parkingLotCapacity.getResponse().getResponseStatus(), ResponseStatus.SUCCESS);
        assertEquals(parkingLotCapacity.getCapacityMap().size(), 2);
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(0)).get(VehicleType.CAR.name()), 1);
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(0)).get(VehicleType.TRUCK.name()), 2);

        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)).get(VehicleType.CAR.name()), 2);
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)).get(VehicleType.TRUCK.name()), 1);
    }

    @Test
    public void testGetParkingLotCapacityWithFewSpotsOccupiedAndJust2ndFloor(){
        setupTest(2, Map.of(VehicleType.CAR, 2, VehicleType.TRUCK, 2), 1, 1, "Bangalore");
        Optional<ParkingLot> parkingLotByGateId = parkingLotRepository.getParkingLotByGateId(1);
        ParkingLot parkingLot = parkingLotByGateId.get();
        // Set car on floor 1, spot 1
        ParkingFloor parkingFloor = parkingLot.getParkingFloors().get(0);
        parkingFloor.getSpots().stream().filter(spot -> spot.getSupportedVehicleType().equals(VehicleType.CAR)).findFirst().get().setStatus(ParkingSpotStatus.OCCUPIED);
        // Set truck on floor 2, spot 1
        parkingFloor = parkingLot.getParkingFloors().get(1);
        parkingFloor.getSpots().stream().filter(spot -> spot.getSupportedVehicleType().equals(VehicleType.TRUCK)).findFirst().get().setStatus(ParkingSpotStatus.OCCUPIED);


        GetParkingLotCapacityRequestDto getParkingLotCapacityRequestDto = new GetParkingLotCapacityRequestDto();
        getParkingLotCapacityRequestDto.setParkingLotId(1);
        getParkingLotCapacityRequestDto.setParkingFloorIds(Arrays.asList(2L));
        getParkingLotCapacityRequestDto.setVehicleTypes(null);
        GetParkingLotCapacityResponseDto parkingLotCapacity = parkingLotController.getParkingLotCapacity(getParkingLotCapacityRequestDto);
        assertEquals(parkingLotCapacity.getResponse().getResponseStatus(), ResponseStatus.SUCCESS);
        assertEquals(parkingLotCapacity.getCapacityMap().size(), 1);

        assertNotNull(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)));
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)).get(VehicleType.CAR.name()), 2);
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)).get(VehicleType.TRUCK.name()), 1);
    }

    @Test
    public void testGetParkingLotCapacityWithFewSpotsOccupiedAndVehicleTypeCar(){
        setupTest(2, Map.of(VehicleType.CAR, 2, VehicleType.TRUCK, 2), 1, 1, "Bangalore");
        Optional<ParkingLot> parkingLotByGateId = parkingLotRepository.getParkingLotByGateId(1);
        ParkingLot parkingLot = parkingLotByGateId.get();
        // Set car on floor 1, spot 1
        ParkingFloor parkingFloor = parkingLot.getParkingFloors().get(0);
        parkingFloor.getSpots().stream().filter(spot -> spot.getSupportedVehicleType().equals(VehicleType.CAR)).findFirst().get().setStatus(ParkingSpotStatus.OCCUPIED);
        // Set truck on floor 2, spot 1
        parkingFloor = parkingLot.getParkingFloors().get(1);
        parkingFloor.getSpots().stream().filter(spot -> spot.getSupportedVehicleType().equals(VehicleType.TRUCK)).findFirst().get().setStatus(ParkingSpotStatus.OCCUPIED);


        GetParkingLotCapacityRequestDto getParkingLotCapacityRequestDto = new GetParkingLotCapacityRequestDto();
        getParkingLotCapacityRequestDto.setParkingLotId(1);
        getParkingLotCapacityRequestDto.setParkingFloorIds(null);
        getParkingLotCapacityRequestDto.setVehicleTypes(Arrays.asList(VehicleType.CAR.name()));
        GetParkingLotCapacityResponseDto parkingLotCapacity = parkingLotController.getParkingLotCapacity(getParkingLotCapacityRequestDto);
        assertEquals(parkingLotCapacity.getResponse().getResponseStatus(), ResponseStatus.SUCCESS);
        assertEquals(parkingLotCapacity.getCapacityMap().size(), 2);

        assertNotNull(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(0)));
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(0)).get(VehicleType.CAR.name()), 1);
        assertNull(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(0)).get(VehicleType.TRUCK.name()));



        assertNotNull(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)));
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)).get(VehicleType.CAR.name()), 2);
        assertNull(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)).get(VehicleType.TRUCK.name()));
    }


    @Test
    public void testGetParkingLotCapacityWithFewSpotsOccupiedAndVehicleTypeCarAnd2ndFloor(){
        setupTest(2, Map.of(VehicleType.CAR, 2, VehicleType.TRUCK, 2), 1, 1, "Bangalore");
        Optional<ParkingLot> parkingLotByGateId = parkingLotRepository.getParkingLotByGateId(1);
        ParkingLot parkingLot = parkingLotByGateId.get();
        // Set car on floor 1, spot 1
        ParkingFloor parkingFloor = parkingLot.getParkingFloors().get(0);
        parkingFloor.getSpots().stream().filter(spot -> spot.getSupportedVehicleType().equals(VehicleType.CAR)).findFirst().get().setStatus(ParkingSpotStatus.OCCUPIED);
        // Set truck on floor 2, spot 1
        parkingFloor = parkingLot.getParkingFloors().get(1);
        parkingFloor.getSpots().stream().filter(spot -> spot.getSupportedVehicleType().equals(VehicleType.TRUCK)).findFirst().get().setStatus(ParkingSpotStatus.OCCUPIED);


        GetParkingLotCapacityRequestDto getParkingLotCapacityRequestDto = new GetParkingLotCapacityRequestDto();
        getParkingLotCapacityRequestDto.setParkingLotId(1);
        getParkingLotCapacityRequestDto.setParkingFloorIds(List.of(2L));
        getParkingLotCapacityRequestDto.setVehicleTypes(List.of(VehicleType.CAR.name()));
        GetParkingLotCapacityResponseDto parkingLotCapacity = parkingLotController.getParkingLotCapacity(getParkingLotCapacityRequestDto);
        assertEquals(parkingLotCapacity.getResponse().getResponseStatus(), ResponseStatus.SUCCESS);
        assertEquals(parkingLotCapacity.getCapacityMap().size(), 1);

        assertNotNull(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)));
        assertEquals(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)).get(VehicleType.CAR.name()), 2);
        assertNull(parkingLotCapacity.getCapacityMap().get(parkingLot.getParkingFloors().get(1)).get(VehicleType.TRUCK.name()));
    }

    public void setupTest(int numOfFloors, Map<VehicleType, Integer> numOfSpotsPerVehicleTypePerFloor, int numOfEntryGates, int numOfExitGates, String address) {
        ParkingLot parkingLot = setupParkingLot(numOfFloors, numOfSpotsPerVehicleTypePerFloor, numOfEntryGates, numOfExitGates, address);
        this.parkingLotRepository.save(parkingLot);
    }

    public static ParkingLot setupParkingLot(int numOfFloors, Map<VehicleType, Integer> numOfSpotsPerVehicleTypePerFloor, int numOfEntryGates, int numOfExitGates, String address){
        int parkingSpotId = 1;
        int parkingFloorId = 1;
        List<ParkingFloor> parkingFloors = new ArrayList<>();
        for(int i=0; i<numOfFloors; i++){
            List<ParkingSpot> spots = new ArrayList<>();
            for(Map.Entry<VehicleType, Integer> entry: numOfSpotsPerVehicleTypePerFloor.entrySet()){
                for(int j=0; j<entry.getValue(); j++){
                    parkingSpotId++;
                    ParkingSpot parkingSpot = new ParkingSpot(parkingSpotId,parkingSpotId,  entry.getKey());
                    spots.add(parkingSpot);
                }
            }
            ParkingFloor parkingFloor = new ParkingFloor(parkingFloorId++, spots, parkingFloorId-1,  FloorStatus.OPERATIONAL);
            parkingFloors.add(parkingFloor);
        }
        List<Gate> gates = new ArrayList<>();
        int parkingAttendantId = 1;
        for(int i=0; i<numOfEntryGates; i++){
            ParkingAttendant parkingAttendant = new ParkingAttendant(parkingAttendantId, String.valueOf(parkingAttendantId), parkingAttendantId +"@gmail.com");
            gates.add(new Gate(parkingAttendantId, String.valueOf(parkingAttendantId), GateType.ENTRY, parkingAttendant));
            parkingAttendantId++;
        }
        for(int i=0; i<numOfExitGates; i++){
            ParkingAttendant parkingAttendant = new ParkingAttendant(parkingAttendantId, String.valueOf(parkingAttendantId), parkingAttendantId+"@gmail.com");
            gates.add(new Gate(parkingAttendantId, String.valueOf(parkingAttendantId), GateType.EXIT, parkingAttendant));
            parkingAttendantId++;
        }
        return new ParkingLot(1, parkingFloors, gates, "Test Parking Lot", address);
    }


}
