package com.example.hotel;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simplified Java implementation focused on core flows:
 * - Availability search and booking creation
 * - Payment processing (mock gateway)
 * - Check-in and check-out (invoice generation + housekeeping task creation)
 *
 * This is not production-ready. It is an interview-focused, runnable skeleton.
 */

/* ----------------------------- Domain Models ----------------------------- */

class Hotel {
    final UUID hotelId = UUID.randomUUID();
    String name;
    String address;
    Map<UUID, Room> rooms = new ConcurrentHashMap<>();

    public void addRoom(Room r) { rooms.put(r.roomId, r); }
    public void removeRoom(UUID id) { rooms.remove(id); }
    public Optional<Room> getRoom(UUID id) { return Optional.ofNullable(rooms.get(id)); }
}

class Room {
    final UUID roomId = UUID.randomUUID();
    String number;
    RoomType type;
    int capacity;
    RoomStatus status = RoomStatus.AVAILABLE;
    double rate;
    UUID currentBookingId;

    public boolean isAvailable(LocalDate from, LocalDate to, BookingRepository bookingRepo) {
        // check status and existing bookings overlap
        if (status != RoomStatus.AVAILABLE && status != RoomStatus.RESERVED) return false;
        List<Booking> bookings = bookingRepo.findByRoomId(roomId);
        for (Booking b : bookings) {
            if (b.status == BookingStatus.CANCELLED) continue;
            if (datesOverlap(from, to, b.checkin.toLocalDate(), b.checkout.toLocalDate())) return false;
        }
        return true;
    }

    private boolean datesOverlap(LocalDate a1, LocalDate a2, LocalDate b1, LocalDate b2) {
        return !(a2.isBefore(b1) || a1.isAfter(b2.minusDays(1)));
    }
}

enum RoomType { SINGLE, DOUBLE, SUITE }
enum RoomStatus { AVAILABLE, OCCUPIED, CLEANING, MAINTENANCE, RESERVED }

class Guest {
    final UUID guestId = UUID.randomUUID();
    String firstName;
    String lastName;
    String email;
}

class Booking {
    final UUID bookingId = UUID.randomUUID();
    UUID guestId;
    List<UUID> roomIds = new ArrayList<>();
    BookingStatus status = BookingStatus.PENDING;
    ZonedDateTime checkin;
    ZonedDateTime checkout;
    double totalAmount;
    UUID paymentId;

    public boolean overlaps(LocalDate from, LocalDate to) {
        LocalDate ci = checkin.toLocalDate();
        LocalDate co = checkout.toLocalDate();
        return !(to.isBefore(ci) || from.isAfter(co.minusDays(1)));
    }
}

enum BookingStatus { PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED, NO_SHOW }

class Payment {
    final UUID paymentId = UUID.randomUUID();
    UUID bookingId;
    double amount;
    PaymentStatus status = PaymentStatus.INIT;
    String gatewayRef;
}

enum PaymentStatus { INIT, AUTHORIZED, CAPTURED, REFUNDED, FAILED }

class Invoice {
    final UUID invoiceId = UUID.randomUUID();
    UUID bookingId;
    List<InvoiceItem> items = new ArrayList<>();
    double subtotal;
    double tax;
    double total;
    InvoiceStatus status = InvoiceStatus.DRAFT;
}

class InvoiceItem { String desc; double amount; }
enum InvoiceStatus { DRAFT, ISSUED, PAID, CANCELLED }

class HousekeepingTask {
    final UUID taskId = UUID.randomUUID();
    UUID roomId;
    UUID assignedTo;
    TaskStatus status = TaskStatus.PENDING;
    String notes;
}

enum TaskStatus { PENDING, IN_PROGRESS, DONE }

/* ----------------------------- Repositories ------------------------------ */

interface RoomRepository {
    void save(Room r);
    Optional<Room> findById(UUID id);
    List<Room> findAll();
}

interface BookingRepository {
    void save(Booking b);
    Optional<Booking> findById(UUID id);
    List<Booking> findByGuestId(UUID guestId);
    List<Booking> findByRoomId(UUID roomId);
    List<Booking> findAll();
}

interface PaymentRepository { void save(Payment p); Optional<Payment> findById(UUID id); }
interface InvoiceRepository { void save(Invoice i); Optional<Invoice> findById(UUID id); }
interface HousekeepingRepository { void save(HousekeepingTask t); }

/* In-memory implementations for demo and tests */
class InMemoryRoomRepo implements RoomRepository {
    Map<UUID, Room> m = new ConcurrentHashMap<>();
    public void save(Room r) { m.put(r.roomId, r); }
    public Optional<Room> findById(UUID id) { return Optional.ofNullable(m.get(id)); }
    public List<Room> findAll() { return new ArrayList<>(m.values()); }
}

class InMemoryBookingRepo implements BookingRepository {
    Map<UUID, Booking> m = new ConcurrentHashMap<>();
    public void save(Booking b) { m.put(b.bookingId, b); }
    public Optional<Booking> findById(UUID id) { return Optional.ofNullable(m.get(id)); }
    public List<Booking> findByGuestId(UUID guestId) { return m.values().stream().filter(b -> guestId.equals(b.guestId)).collect(Collectors.toList()); }
    public List<Booking> findByRoomId(UUID roomId) { return m.values().stream().filter(b -> b.roomIds.contains(roomId)).collect(Collectors.toList()); }
    public List<Booking> findAll() { return new ArrayList<>(m.values()); }
}

class InMemoryPaymentRepo implements PaymentRepository {
    Map<UUID, Payment> m = new ConcurrentHashMap<>();
    public void save(Payment p) { m.put(p.paymentId, p); }
    public Optional<Payment> findById(UUID id) { return Optional.ofNullable(m.get(id)); }
}

class InMemoryInvoiceRepo implements InvoiceRepository {
    Map<UUID, Invoice> m = new ConcurrentHashMap<>();
    public void save(Invoice i) { m.put(i.invoiceId, i); }
    public Optional<Invoice> findById(UUID id) { return Optional.ofNullable(m.get(id)); }
}

class InMemoryHousekeepingRepo implements HousekeepingRepository {
    Map<UUID, HousekeepingTask> m = new ConcurrentHashMap<>();
    public void save(HousekeepingTask t) { m.put(t.taskId, t); }
}

/* ------------------------------- Services -------------------------------- */

/** Simple event bus used for cross-cutting notifications. */
class EventBus {
    private final List<EventHandler> handlers = new ArrayList<>();
    public void publish(DomainEvent e) { handlers.forEach(h -> h.handle(e)); }
    public void subscribe(EventHandler h) { handlers.add(h); }
}

interface DomainEvent {}
interface EventHandler { void handle(DomainEvent e); }

class BookingCreatedEvent implements DomainEvent { final UUID bookingId; BookingCreatedEvent(UUID id) { this.bookingId = id; } }

/** Basic payment service that simulates a gateway.
 *  It is idempotent per booking id for demo purposes. */
class PaymentService {
    final PaymentRepository paymentRepo;

    PaymentService(PaymentRepository paymentRepo) { this.paymentRepo = paymentRepo; }

    public Payment processPayment(UUID bookingId, double amount) {
        Payment p = new Payment();
        p.bookingId = bookingId;
        p.amount = amount;
        p.status = PaymentStatus.AUTHORIZED; // mock immediate authorize
        p.gatewayRef = "MOCK-GW-" + UUID.randomUUID();
        paymentRepo.save(p);
        // capture
        p.status = PaymentStatus.CAPTURED;
        paymentRepo.save(p);
        return p;
    }
}

class BillingService {
    final InvoiceRepository invoiceRepo;

    BillingService(InvoiceRepository invoiceRepo) { this.invoiceRepo = invoiceRepo; }

    public Invoice generateInvoice(Booking b, RoomRepository roomRepo) {
        Invoice inv = new Invoice();
        inv.bookingId = b.bookingId;
        double subtotal = 0;
        for (UUID rid : b.roomIds) {
            Room r = roomRepo.findById(rid).orElseThrow();
            InvoiceItem it = new InvoiceItem();
            it.desc = "Room " + r.number + " (" + r.type + ")";
            it.amount = r.rate * daysBetween(b.checkin, b.checkout);
            subtotal += it.amount;
            inv.items.add(it);
        }
        inv.subtotal = subtotal;
        inv.tax = subtotal * 0.12; // fixed tax for demo
        inv.total = inv.subtotal + inv.tax;
        inv.status = InvoiceStatus.ISSUED;
        invoiceRepo.save(inv);
        return inv;
    }

    private long daysBetween(ZonedDateTime a, ZonedDateTime b) {
        return Duration.between(a.toLocalDate().atStartOfDay(a.getZone()), b.toLocalDate().atStartOfDay(b.getZone())).toDays();
    }
}

class HousekeepingService implements EventHandler {
    final HousekeepingRepository hkRepo;
    HousekeepingService(HousekeepingRepository hkRepo) { this.hkRepo = hkRepo; }

    public HousekeepingTask createTask(UUID roomId, String notes) {
        HousekeepingTask t = new HousekeepingTask();
        t.roomId = roomId; t.notes = notes; t.status = TaskStatus.PENDING;
        hkRepo.save(t);
        return t;
    }

    @Override
    public void handle(DomainEvent e) {
        if (e instanceof BookingCreatedEvent) {
            // no-op for demo
        }
    }
}

/** Core reservation service. Contains main flow logic. */
class ReservationService {
    final RoomRepository roomRepo;
    final BookingRepository bookingRepo;
    final EventBus eventBus;

    ReservationService(RoomRepository rr, BookingRepository br, EventBus eb) {
        this.roomRepo = rr; this.bookingRepo = br; this.eventBus = eb;
    }

    /** Search available rooms by type and occupancy dates. */
    public List<Room> searchAvailable(RoomType type, LocalDate from, LocalDate to) {
        List<Room> all = roomRepo.findAll();
        return all.stream()
                .filter(r -> r.type == type)
                .filter(r -> r.isAvailable(from, to, bookingRepo))
                .collect(Collectors.toList());
    }

    /** Create booking. Steps:
     * 1) find availability
     * 2) reserve rooms
     * 3) persist booking
     * 4) publish BookingCreated event
     */
    public Booking createBooking(Guest guest, RoomType type, LocalDate checkinDate, LocalDate checkoutDate) {
        List<Room> candidates = searchAvailable(type, checkinDate, checkoutDate);
        if (candidates.isEmpty()) throw new IllegalStateException("No rooms available");
        Room chosen = candidates.get(0); // simple allocation

        Booking b = new Booking();
        b.guestId = guest.guestId;
        b.roomIds.add(chosen.roomId);
        b.checkin = checkinDate.atStartOfDay(ZoneId.systemDefault());
        b.checkout = checkoutDate.atStartOfDay(ZoneId.systemDefault());
        b.status = BookingStatus.CONFIRMED;
        b.totalAmount = chosen.rate * Duration.between(b.checkin, b.checkout).toDays();

        // mark room reserved
        chosen.status = RoomStatus.RESERVED; chosen.currentBookingId = b.bookingId;
        roomRepo.save(chosen);

        bookingRepo.save(b);
        eventBus.publish(new BookingCreatedEvent(b.bookingId));
        return b;
    }

    /** Check-in flow: mark booking checked in and room occupied. */
    public void checkIn(UUID bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow();
        if (b.status != BookingStatus.CONFIRMED) throw new IllegalStateException("Booking not confirmable for check-in");
        for (UUID rid : b.roomIds) {
            Room r = roomRepo.findById(rid).orElseThrow();
            r.status = RoomStatus.OCCUPIED; r.currentBookingId = b.bookingId; roomRepo.save(r);
        }
        b.status = BookingStatus.CHECKED_IN; bookingRepo.save(b);
    }

    /** Check-out flow: generate invoice, mark room for cleaning and free room. */
    public Invoice checkOut(UUID bookingId, BillingService billingService, HousekeepingService hkService) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow();
        if (b.status != BookingStatus.CHECKED_IN) throw new IllegalStateException("Booking not checked in");
        Invoice inv = billingService.generateInvoice(b, roomRepo);
        // free rooms and create housekeeping tasks
        for (UUID rid : b.roomIds) {
            Room r = roomRepo.findById(rid).orElseThrow();
            r.status = RoomStatus.CLEANING; r.currentBookingId = null; roomRepo.save(r);
            hkService.createTask(r.roomId, "Cleaning after checkout: " + b.bookingId);
        }
        b.status = BookingStatus.CHECKED_OUT; bookingRepo.save(b);
        return inv;
    }
}

/* ------------------------------ Quick Demo -------------------------------- */
class Demo {
    public static void main(String[] args) {
        // setup repos
        RoomRepository roomRepo = new InMemoryRoomRepo();
        BookingRepository bookingRepo = new InMemoryBookingRepo();
        PaymentRepository paymentRepo = new InMemoryPaymentRepo();
        InvoiceRepository invoiceRepo = new InMemoryInvoiceRepo();
        HousekeepingRepository hkRepo = new InMemoryHousekeepingRepo();

        // services
        EventBus eb = new EventBus();
        HousekeepingService hkService = new HousekeepingService(hkRepo); eb.subscribe(hkService);
        ReservationService rs = new ReservationService(roomRepo, bookingRepo, eb);
        PaymentService ps = new PaymentService(paymentRepo);
        BillingService bill = new BillingService(invoiceRepo);

        // seed hotel rooms
        Room r1 = new Room(); r1.number = "101"; r1.type = RoomType.SINGLE; r1.capacity = 1; r1.rate = 3000; roomRepo.save(r1);
        Room r2 = new Room(); r2.number = "102"; r2.type = RoomType.DOUBLE; r2.capacity = 2; r2.rate = 4500; roomRepo.save(r2);

        // guest and booking
        Guest g = new Guest(); g.firstName = "Alice"; g.lastName = "K"; g.email = "alice@example.com";
        Booking b = rs.createBooking(g, RoomType.SINGLE, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        System.out.println("Created booking: " + b.bookingId + " amount: " + b.totalAmount);

        // payment
        Payment p = ps.processPayment(b.bookingId, b.totalAmount);
        System.out.println("Payment processed: " + p.paymentId + " status: " + p.status);
        b.paymentId = p.paymentId; // link payment

        // check-in and check-out
        rs.checkIn(b.bookingId);
        Invoice inv = rs.checkOut(b.bookingId, bill, hkService);
        System.out.println("Invoice generated: " + inv.invoiceId + " total: " + inv.total);
    }
}
