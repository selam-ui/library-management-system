package mapper;

import dto.ReservationDTO;
import enumtypes.ReservationStatus;
import model.Reservation;

public class ReservationMapper {
    public static ReservationDTO toDTO(Reservation r, String bookTitle, String isbn) {
        if (r == null) return null;
        String status = r.isFulfilled() ? ReservationStatus.FULFILLED.name()
                : r.isNotified() ? ReservationStatus.READY.name()
                : ReservationStatus.PENDING.name();
        return new ReservationDTO(r.getId(), r.getBookId(), bookTitle, isbn, status, r.getReservedAt());
    }
}
