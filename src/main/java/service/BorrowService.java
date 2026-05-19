package service;

import dto.BorrowRecordDTO;
import exception.ValidationException;
import java.util.List;

public interface BorrowService {
    List<BorrowRecordDTO> listBorrowRecords();
    BorrowRecordDTO borrowBook(int bookId) throws ValidationException;
    BorrowRecordDTO returnBook(int borrowRecordId) throws ValidationException;
}
