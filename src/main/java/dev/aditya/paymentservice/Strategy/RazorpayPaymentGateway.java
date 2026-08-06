package dev.aditya.paymentservice.Strategy;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import dev.aditya.paymentservice.Exceptions.CustomPaymentGatewayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("RazorpayPaymentGateway")
public class RazorpayPaymentGateway implements IPaymentGateway{

    @Autowired
    private RazorpayClient razorpayClient;


    @Override
    public String generatePaymentLink(long orderId, long amount, Long userId, String userName , String phoneNumber, String userEmail) {

        /* Razorpay Payment Link object(json)
        {
        "amount": 1000,
        "currency": "INR",
        "accept_partial": true,
        "first_min_partial_amount": 100,
        "expire_by": 1691097057,
        "reference_id": "TS1989",
        "description": "Payment for policy no #23456",
        "customer": {
                "name" : "Umang",
                },
                                "notify": {
                "sms": true,
                "email": true
        },
         */
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount",amount);
            paymentLinkRequest.put("currency","INR");
            paymentLinkRequest.put("accept_partial",true);
            paymentLinkRequest.put("first_min_partial_amount",100);
            paymentLinkRequest.put("expire_by",System.currentTimeMillis() + 60000); //important expiry time for the link
            paymentLinkRequest.put("reference_id",String.valueOf(orderId));
            paymentLinkRequest.put("description","Test payment for Payment service class");

            JSONObject customer = new JSONObject();
            customer.put("name",userName);
            customer.put("contact",phoneNumber);
            customer.put("email",userEmail);

            paymentLinkRequest.put("customer",customer);

            JSONObject notify = new JSONObject();
            notify.put("sms",true);
            notify.put("email",true);


            paymentLinkRequest.put("reminder_enable",true);

            JSONObject notes = new JSONObject();
            notes.put("policy_name","Jeevan Bima");
            paymentLinkRequest.put("notes",notes);
            paymentLinkRequest.put("callback_url","https://google.com/");
            paymentLinkRequest.put("callback_method","get");

        PaymentLink payment = null;
        try {
            payment = razorpayClient.paymentLink.create(paymentLinkRequest);
        } catch (RazorpayException e) {
            throw new CustomPaymentGatewayException("There seems to be some issue with the Gateway at the moment! "
                    + "Please try again later or select any other Gateway!!");
        }
        return payment.get("short_url");
    }
}

