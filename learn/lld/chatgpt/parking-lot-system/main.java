// Demo implementation of ParkingLot core flows in Java
// Focus: correctness of park and exit flows, thread-safety, clear domain models
// Note: This is a single-file demo. In production split into packages and files.

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ParkingLotDemo {

    public static void main(String[] args) throws InterruptedException {
        // Setup demo parking lot with 2 floors and a few slots
        ParkingLotConfig cfg = new ParkingLotConfig(2);
        ParkingLot lot = ParkingLot.getInstance("P1", "CentralLot", cfg);

        // Create floors and slots
        for (int f = 1; f <= 2; f++) {
            Floor floor = new Floor(f);
            // Add 3 car slots and 2 bike slots per floor
            for (int i = 1; i <= 3; i++) {
                Slot s = SlotFactory.createSlot(f, i, SlotSize.REGULAR, VehicleType.CAR);
                floor.addSlot(s);
            }
            for (int i = 4; i <= 5; i++) {
                Slot s = SlotFactory.createSlot(f, i, SlotSize.COMPACT, VehicleType.BIKE);
                floor.addSlot(s);
            }
            lot.addFloor(floor);
        }

        // Wire up repositories and services
        SlotRepository slotRepo = new InMemorySlotRepository(lot);
        TicketRepository ticketRepo = new InMemoryTicketRepository();
        PricingService pricingService = new PricingService();
        pricingService.registerStrategy(VehicleType.CAR, new HourlyPricing(BigDecimal.valueOf(30))); // ₹30 per hour
        pricingService.registerStrategy(VehicleType.BIKE, new HourlyPricing(BigDecimal.valueOf(10))); // ₹10 per hour

        SlotAllocator allocator = new GreedySlotAllocator(slotRepo);
        EventPublisher eventPublisher = new SimpleEventPublisher();
        ParkingService parkingService = new ParkingService(allocator, ticketRepo, slotRepo, pricingService, eventPublisher);

        // Demo park a vehicle
        Vehicle v1 = new Vehicle("KA-01-AB-1234", VehicleType.CAR, "Red", null);
        System.out.println("Parking vehicle: " + v1.getRegistrationNumber());
        try {
            Ticket t1 = parkingService.parkVehicle(v1, "Gate-1");
            System.out.println("Parked. Ticket: " + t1.getTicketId() + " Slot: " + t1.getSlotId());

            // Sleep to simulate time passage
            Thread.sleep(2_000);

            // Exit
            ExitResult r = parkingService.exitVehicle(t1.getTicketId(), "Gate-1");
            System.out.println("Exited. Amount charged: " + r.getAmount() + " Duration(s): " + r.getDurationSeconds());

        } catch (ParkingException e) {
            System.err.println("Operation failed: " + e.getMessage());
        }

        // Park multiple vehicles concurrently to validate thread-safety
        Runnable parkTask = () -> {
            Vehicle v = new Vehicle(UUID.randomUUID().toString().substring(0, 8), VehicleType.CAR, "Blue", null);
            try {
                Ticket t = parkingService.parkVehicle(v, "Gate-2");
                System.out.println(Thread.currentThread().getName() + " parked " + v.getRegistrationNumber() + " -> " + t.getSlotId());
            } catch (ParkingException ex) {
                System.err.println(Thread.currentThread().getName() + " failed to park: " + ex.getMessage());
            }
        };

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Thread th = new Thread(parkTask, "Worker-" + i);
            threads.add(th);
            th.start();
        }
        for (Thread th : threads) th.join();

        System.out.println("Available counts after concurrent parking:");
        lot.listFloors().forEach(f -> System.out.println("Floor " + f.getFloorNumber() + " -> " + f.getAvailableCounts()));
    }
}

// ---------------------------
// Domain and DTOs
// ---------------------------

class ParkingLotConfig {
    private final int floors;
    public ParkingLotConfig(int floors) { this.floors = floors; }
    public int getFloors() { return floors; }
}

class ParkingLot {
    private static final AtomicReference<ParkingLot> INSTANCE = new AtomicReference<>();

    private final String id;
    private final String name;
    private final int totalFloors;
    private final Map<Integer, Floor> floors = new ConcurrentHashMap<>();

    private ParkingLot(String id, String name, ParkingLotConfig cfg) {
        this.id = id;
        this.name = name;
        this.totalFloors = cfg.getFloors();
    }

    public static ParkingLot getInstance(String id, String name, ParkingLotConfig cfg) {
        INSTANCE.compareAndSet(null, new ParkingLot(id, name, cfg));
        return INSTANCE.get();
    }

    public Floor getFloor(int n) { return floors.get(n); }
    public void addFloor(Floor f) { floors.put(f.getFloorNumber(), f); }
    public void removeFloor(int n) { floors.remove(n); }
    public List<Floor> listFloors() { return new ArrayList<>(floors.values()); }
}

class Floor {
    private final int floorNumber;
    private final Map<String, Slot> slots = new ConcurrentHashMap<>();
    private final Map<VehicleType, AtomicInteger> availableCount = new ConcurrentHashMap<>();

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        for (VehicleType vt : VehicleType.values()) availableCount.put(vt, new AtomicInteger(0));
    }

    public int getFloorNumber() { return floorNumber; }

    public void addSlot(Slot s) {
        slots.put(s.getId(), s);
        availableCount.get(s.getAllowedType()).incrementAndGet();
    }

    public void removeSlot(String id) {
        Slot removed = slots.remove(id);
        if (removed != null && removed.isAvailable()) {
            availableCount.get(removed.getAllowedType()).decrementAndGet();
        }
    }

    public Optional<Slot> findNearestAvailableSlot(VehicleType type) {
        // Simple heuristic: smallest slot id first
        return slots.values().stream()
            .filter(slot -> slot.getAllowedType() == type && slot.isAvailable())
            .sorted(Comparator.comparing(Slot::getId))
            .findFirst();
    }

    public boolean hasAvailableSlot(VehicleType type) {
        return availableCount.get(type).get() > 0;
    }

    public Map<VehicleType, Integer> getAvailableCounts() {
        return availableCount.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));
    }
}

enum VehicleType { CAR, BIKE, TRUCK }
enum SlotSize { COMPACT, REGULAR, LARGE }
enum SlotStatus { AVAILABLE, OCCUPIED, RESERVED, OUT_OF_SERVICE }
enum TicketStatus { ACTIVE, CLOSED, CANCELLED }

class Slot {
    private final String id; // e.g., F1-S1
    private final VehicleType allowedType;
    private final SlotSize size;
    private final AtomicReference<SlotStatus> status = new AtomicReference<>(SlotStatus.AVAILABLE);
    private volatile Ticket occupiedBy; // write under status change
    private final long createdAt = Instant.now().toEpochMilli();
    private volatile long updatedAt = createdAt;

    public Slot(String id, VehicleType allowedType, SlotSize size) {
        this.id = id;
        this.allowedType = allowedType;
        this.size = size;
    }

    public String getId() { return id; }
    public VehicleType getAllowedType() { return allowedType; }
    public SlotSize getSize() { return size; }
    public SlotStatus getStatus() { return status.get(); }

    public boolean isAvailable() { return status.get() == SlotStatus.AVAILABLE; }

    /**
     * Attempt to occupy the slot. Uses CAS on status to guarantee correctness.
     * Returns true if occupation succeeded and ticket recorded.
     */
    public boolean occupy(Ticket ticket) {
        boolean changed = status.compareAndSet(SlotStatus.AVAILABLE, SlotStatus.OCCUPIED);
        if (!changed) return false;
        this.occupiedBy = ticket;
        this.updatedAt = Instant.now().toEpochMilli();
        return true;
    }

    /**
     * Vacate the slot. Sets status back to AVAILABLE.
     */
    public boolean vacate() {
        boolean changed = status.compareAndSet(SlotStatus.OCCUPIED, SlotStatus.AVAILABLE);
        if (!changed) return false;
        this.occupiedBy = null;
        this.updatedAt = Instant.now().toEpochMilli();
        return true;
    }

    public void markReserved() {
        status.set(SlotStatus.RESERVED);
        this.updatedAt = Instant.now().toEpochMilli();
    }

    public void markOutOfService() {
        status.set(SlotStatus.OUT_OF_SERVICE);
        this.updatedAt = Instant.now().toEpochMilli();
    }
}

class Vehicle {
    private final String registrationNumber;
    private final VehicleType type;
    private final String color;
    private final String ownerId;

    public Vehicle(String registrationNumber, VehicleType type, String color, String ownerId) {
        this.registrationNumber = registrationNumber;
        this.type = type;
        this.color = color;
        this.ownerId = ownerId;
    }
    public String getRegistrationNumber() { return registrationNumber; }
    public VehicleType getType() { return type; }
    public String getColor() { return color; }
}

class Ticket {
    private final String ticketId;
    private final String parkingLotId;
    private final String slotId;
    private final String vehicleRegistration;
    private final VehicleType vehicleType;

    private final long entryTimestamp;
    private Long exitTimestamp; // nullable
    private TicketStatus status;
    private BigDecimal amountCharged;

    public Ticket(String ticketId, String parkingLotId, String slotId, String vehicleRegistration, VehicleType vehicleType) {
        this.ticketId = ticketId;
        this.parkingLotId = parkingLotId;
        this.slotId = slotId;
        this.vehicleRegistration = vehicleRegistration;
        this.vehicleType = vehicleType;
        this.entryTimestamp = Instant.now().toEpochMilli();
        this.status = TicketStatus.ACTIVE;
    }

    public String getTicketId() { return ticketId; }
    public String getSlotId() { return slotId; }
    public String getVehicleRegistration() { return vehicleRegistration; }
    public long getEntryTimestamp() { return entryTimestamp; }

    public synchronized void markExit(long exitTs, BigDecimal amount) {
        if (this.status != TicketStatus.ACTIVE) throw new IllegalStateException("Ticket not active");
        this.exitTimestamp = exitTs;
        this.amountCharged = amount;
        this.status = TicketStatus.CLOSED;
    }

    public long getDurationMillis() {
        long end = (exitTimestamp == null) ? Instant.now().toEpochMilli() : exitTimestamp;
        return Math.max(0, end - entryTimestamp);
    }

    public BigDecimal getAmountCharged() { return amountCharged; }
}

// ---------------------------
// Repositories (in-memory implementations for demo)
// ---------------------------

interface SlotRepository {
    Optional<Slot> findAvailableSlot(VehicleType type);
    Optional<Slot> findById(String id);
    void updateSlotStatus(String id, SlotStatus status);
    List<Slot> findAllSlots();
}

class InMemorySlotRepository implements SlotRepository {
    private final ParkingLot lot;
    private final Map<String, Slot> slotIndex = new ConcurrentHashMap<>();

    public InMemorySlotRepository(ParkingLot lot) {
        this.lot = lot;
        lot.listFloors().forEach(f -> f.getAvailableCounts());
        // Build index
        for (Floor floor : lot.listFloors()) {
            for (Slot s : floor.slots.values()) {
                slotIndex.put(s.getId(), s);
            }
        }
    }

    @Override
    public Optional<Slot> findAvailableSlot(VehicleType type) {
        // Simple search across all slots
        return slotIndex.values().stream()
            .filter(s -> s.getAllowedType() == type && s.isAvailable())
            .sorted(Comparator.comparing(Slot::getId))
            .findFirst();
    }

    @Override
    public Optional<Slot> findById(String id) { return Optional.ofNullable(slotIndex.get(id)); }

    @Override
    public void updateSlotStatus(String id, SlotStatus status) {
        Slot s = slotIndex.get(id);
        if (s == null) return;
        switch (status) {
            case AVAILABLE: s.markOutOfService(); break; // not used in demo
            case OCCUPIED: /* handled by occupy on slot */ break;
            case RESERVED: s.markReserved(); break;
            case OUT_OF_SERVICE: s.markOutOfService(); break;
        }
    }

    @Override
    public List<Slot> findAllSlots() { return new ArrayList<>(slotIndex.values()); }
}

interface TicketRepository {
    Ticket save(Ticket t);
    Optional<Ticket> findById(String id);
    Optional<Ticket> findActiveByVehicleReg(String reg);
    void update(Ticket t);
}

class InMemoryTicketRepository implements TicketRepository {
    private final Map<String, Ticket> store = new ConcurrentHashMap<>();

    @Override
    public Ticket save(Ticket t) { store.put(t.getTicketId(), t); return t; }

    @Override
    public Optional<Ticket> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public Optional<Ticket> findActiveByVehicleReg(String reg) {
        return store.values().stream()
            .filter(t -> t.getVehicleRegistration().equals(reg))
            .filter(t -> t.getAmountCharged() == null) // simplistic active check
            .findFirst();
    }

    @Override
    public void update(Ticket t) { store.put(t.getTicketId(), t); }
}

// ---------------------------
// SlotAllocator implementations
// ---------------------------

interface SlotAllocator {
    Optional<Slot> allocate(VehicleType type, String gateId) throws ParkingException;
    void free(Slot slot);
}

class GreedySlotAllocator implements SlotAllocator {
    private final SlotRepository slotRepo;

    public GreedySlotAllocator(SlotRepository slotRepo) { this.slotRepo = slotRepo; }

    @Override
    public Optional<Slot> allocate(VehicleType type, String gateId) throws ParkingException {
        // Attempt to find and occupy a slot. We must ensure atomicity.
        Optional<Slot> candidate = slotRepo.findAvailableSlot(type);
        if (!candidate.isPresent()) return Optional.empty();
        Slot s = candidate.get();

        // Try to occupy using CAS on slot
        boolean ok = s.occupy(new Ticket("temp", "", s.getId(), "", type));
        if (ok) return Optional.of(s);

        // If CAS failed due to race, retry a few times
        int attempts = 0;
        while (attempts < 3) {
            Optional<Slot> next = slotRepo.findAvailableSlot(type);
            if (!next.isPresent()) return Optional.empty();
            s = next.get();
            if (s.occupy(new Ticket("temp", "", s.getId(), "", type))) return Optional.of(s);
            attempts++;
        }
        return Optional.empty();
    }

    @Override
    public void free(Slot slot) {
        slot.vacate();
    }
}

// ---------------------------
// Pricing
// ---------------------------

interface PricingStrategy {
    BigDecimal calculate(Ticket ticket, long durationMillis);
}

class HourlyPricing implements PricingStrategy {
    private final BigDecimal perHour;

    public HourlyPricing(BigDecimal perHour) { this.perHour = perHour; }

    @Override
    public BigDecimal calculate(Ticket ticket, long durationMillis) {
        long hours = Math.max(1, Duration.ofMillis(durationMillis).toHours());
        return perHour.multiply(BigDecimal.valueOf(hours));
    }
}

class PricingService {
    private final Map<VehicleType, PricingStrategy> strategies = new HashMap<>();
    public void registerStrategy(VehicleType t, PricingStrategy s) { strategies.put(t, s); }
    public BigDecimal computeCharge(Ticket ticket) {
        PricingStrategy strat = strategies.get(ticket.vehicleType);
        if (strat == null) throw new IllegalStateException("No pricing strategy for " + ticket.vehicleType);
        return strat.calculate(ticket, ticket.getDurationMillis());
    }
}

// ---------------------------
// Payment stub and events
// ---------------------------

interface EventPublisher {
    void publish(Event e);
}

class SimpleEventPublisher implements EventPublisher {
    @Override
    public void publish(Event e) {
        System.out.println("Event published: " + e.getClass().getSimpleName());
    }
}

abstract class Event {}
class VehicleParkedEvent extends Event { final Ticket t; VehicleParkedEvent(Ticket t){this.t=t;} }
class VehicleExitedEvent extends Event { final Ticket t; VehicleExitedEvent(Ticket t){this.t=t;} }

// ---------------------------
// ParkingService: core flow correctness
// ---------------------------

class ParkingService {
    private final SlotAllocator allocator;
    private final TicketRepository ticketRepo;
    private final SlotRepository slotRepo;
    private final PricingService pricingService;
    private final EventPublisher eventPublisher;

    public ParkingService(SlotAllocator allocator,
                          TicketRepository ticketRepo,
                          SlotRepository slotRepo,
                          PricingService pricingService,
                          EventPublisher eventPublisher) {
        this.allocator = allocator;
        this.ticketRepo = ticketRepo;
        this.slotRepo = slotRepo;
        this.pricingService = pricingService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Park vehicle flow:
     * 1. Allocate slot (thread-safe CAS on slot)
     * 2. Create persistent ticket
     * 3. If persistence fails, free slot
     */
    public Ticket parkVehicle(Vehicle vehicle, String gateId) throws ParkingException {
        // Basic validations
        if (vehicle == null) throw new ParkingException("Vehicle required");

        // Check if vehicle already parked
        Optional<Ticket> active = ticketRepo.findActiveByVehicleReg(vehicle.getRegistrationNumber());
        if (active.isPresent()) throw new ParkingException("Vehicle already parked");

        Optional<Slot> opt = allocator.allocate(vehicle.getType(), gateId);
        if (!opt.isPresent()) throw new ParkingException("No available slot for vehicle type: " + vehicle.getType());
        Slot slot = opt.get();

        // Build ticket
        String ticketId = UUID.randomUUID().toString();
        Ticket ticket = new Ticket(ticketId, "P1", slot.getId(), vehicle.getRegistrationNumber(), vehicle.getType());

        // Persist ticket. If fails, free slot to avoid leak.
        try {
            ticketRepo.save(ticket);
            // Successful. Publish event and return ticket.
            eventPublisher.publish(new VehicleParkedEvent(ticket));
            return ticket;
        } catch (RuntimeException ex) {
            // rollback slot occupation
            allocator.free(slot);
            throw new ParkingException("Failed to persist ticket: " + ex.getMessage());
        }
    }

    /**
     * Exit vehicle flow:
     * 1. Fetch ticket
     * 2. Compute duration and charge
     * 3. Mark ticket exit and persist
     * 4. Free slot
     */
    public ExitResult exitVehicle(String ticketId, String gateId) throws ParkingException {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow(() -> new ParkingException("Invalid ticket"));
        if (ticket.getAmountCharged() != null) throw new ParkingException("Ticket already closed");

        long exitTs = Instant.now().toEpochMilli();
        long duration = exitTs - ticket.getEntryTimestamp();
        BigDecimal amount = pricingService.computeCharge(ticket);

        // Simulate payment - in demo we assume success
        // Commit ticket changes
        ticket.markExit(exitTs, amount);
        ticketRepo.update(ticket);

        // Free slot
        Optional<Slot> sOpt = slotRepo.findById(ticket.getSlotId());
        sOpt.ifPresent(slot -> allocator.free(slot));

        eventPublisher.publish(new VehicleExitedEvent(ticket));
        return new ExitResult(amount, duration / 1000);
    }
}

class ExitResult {
    private final BigDecimal amount;
    private final long durationSeconds;
    public ExitResult(BigDecimal amount, long durationSeconds) { this.amount = amount; this.durationSeconds = durationSeconds; }
    public BigDecimal getAmount() { return amount; }
    public long getDurationSeconds() { return durationSeconds; }
}

// ---------------------------
// Slot factory
// ---------------------------

class SlotFactory {
    public static Slot createSlot(int floor, int number, SlotSize size, VehicleType type) {
        String id = "F" + floor + "-S" + number;
        return new Slot(id, type, size);
    }
}

// ---------------------------
// Exceptions
// ---------------------------

class ParkingException extends Exception {
    public ParkingException(String message) { super(message); }
}
