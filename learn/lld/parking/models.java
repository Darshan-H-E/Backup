// ParkingLot file


public class ParkingLot {
  // attributes
  // methods
  //  add floor, setFeeStrategy, setParkingStrategy

  public Optional<ParkingTicket> parkVehicle(Vehicle vehicle) { Optional<ParkingSpot> availableSpot = ParkingStrategy.findSpot(floors, vehicle);
    if (availableSpot.isPresent()) {
      ParkingSpot spot = availableSpot.get();
      spot.parkVehicle(vehicle);
      ParkingTicket ticket = new ParkingTicket(vehicle, spot);
      activeTickets.put(vehicle.getLicenseNumber(), ticket);
      SOP("%s parked at %s. Ticket: %s\n", vehicle.getLicenseNumber(), spot.getSpotId(), ticket.getTicketId());
      return Optional.of(ticket);
    }

    SOP("No available spot");
    return Optional.empty();
  }

  public Optional<ParkingTicket> parkVehicle(Vehicle vehicle) { Optional<ParkingSpot> availableSpot = ParkingStrategy.findSpot(floors, vehicle);
    ParkingTicket ticket = activeTickets.remove(licenseNumber);
    if (availableSpot.isPresent()) {
      ParkingSpot spot = availableSpot.get();
      spot.parkVehicle(vehicle);
      activeTickets.put(vehicle.getLicenseNumber(), ticket);
      SOP("%s parked at %s. Ticket: %s\n", vehicle.getLicenseNumber(), spot.getSpotId(), ticket.getTicketId());
      return Optional.of(ticket);
    }

    SOP("No available spot");
    return Optional.empty();
  }
}
