package dev.aditya.paymentservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStatusUpdateRequestDto {
    private String transactionId;
    private String orderId;
    private Long amount;
    private String userId;


}
