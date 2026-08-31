package dev.aditya.paymentservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {

    private Long orderId;
    private String paymentStatus;
    private Long paymentId;
    private String paymentMethod;
    private Long totalAmount;
    private String paymentGateway;
}
