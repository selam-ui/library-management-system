package service;

import dto.ReservationDTO;
import exception.ValidationException;

import java.util.List;

public interface ReservationService {
    List<ReservationDTO> listMyReservations() throws ValidationException;
    ReservationDTO reserveBook(int bookId) throws ValidationException;
    int countReadyNotifications() throws ValidationException;
}
