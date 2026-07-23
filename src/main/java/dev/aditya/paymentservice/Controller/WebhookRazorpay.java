package dev.aditya.paymentservice.Controller;


import dev.aditya.paymentservice.Service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/razorpay")
public class WebhookRazorpay {

    @Autowired
    private IPaymentService paymentService;

    @Value("${Razorpay.endpoint_secret}")
    private String endpointSecret;

    //These are just placeholders, would need to implement the APIs appropriately in accordance with RazorPay docs.

    @PostMapping("/success")
    public ResponseEntity<String> capturePaymentSuccess(@RequestParam("session_id") String session_id){
        return new ResponseEntity<>("Transaction Complete! Thank you for shopping with us", HttpStatus.OK);}

    @PostMapping("/failure")
    public ResponseEntity<String> capturePaymentFailure() {
        return new ResponseEntity<>("Could not complete transaction! Please try again later!!", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Razorpay-Signature") String signHeader) {
        return new ResponseEntity<>("Transaction Complete! Thank you for shopping with us", HttpStatus.OK);
        }
}
