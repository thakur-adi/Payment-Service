package dev.aditya.paymentservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private long orderId;
    private long amount;
    private long userId;
    private String username;
    private String phoneNumber;
    private String userEmail;
}
