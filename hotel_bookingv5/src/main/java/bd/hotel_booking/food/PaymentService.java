package bd.hotel_booking.food;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public boolean process(PaymentMethod method, String cardNumber, String bkashNumber) {
        if (method == PaymentMethod.CASH) return true;
        return true;
    }
}