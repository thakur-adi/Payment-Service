package dev.aditya.paymentservice.Service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService implements IPaymentService {
    @Override
    public String generatePaymentLink() {
        return "";
    }
}
