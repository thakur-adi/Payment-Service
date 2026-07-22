package dev.aditya.paymentservice.Configuaration;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {
    @Value("${razorpay.key_id}") //environment variables
    private String razorpayKey;

    @Value("${razorpay.key_secret}")
    private String razorpaySecret;
    @Bean
    public RazorpayClient getRazorpayClient() throws RazorpayException {
        return new RazorpayClient(razorpayKey, razorpaySecret);
    }
}
