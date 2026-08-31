package dev.aditya.paymentservice.Controller;


import dev.aditya.paymentservice.Service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/razorpay")
public class RazorpayController {

    @Autowired
    private IPaymentService paymentService;

    @Value("${razorpay.key_secret}")
    private String razorpaySecret;

    //These are just placeholders, would need to implement the APIs appropriately in accordance with RazorPay docs.

    @PostMapping("/success")
    public ResponseEntity<String> capturePaymentSuccess(@RequestParam("order_id") String orderId){
        //This is created just for testing purposes.
        //For testing purposes we'll consider sending an order_Id as a request param, which probably isn't given by RazorPay in reality.
        //In Production would need to rewrite this code as this isn't provided by RazorPay, actually.
        paymentService.saveTransactionDetailsTest(orderId,"SUCCESS","RAZORPAY");

        return new ResponseEntity<>("Transaction Complete! Thank you for shopping with us", HttpStatus.OK);}

    @PostMapping("/failure")
    public ResponseEntity<String> capturePaymentFailure(@RequestParam("order_id") String orderId) {
        //This is created just for testing purposes.
        //For testing purposes we'll consider sending an order_Id as a request param, which probably isn't given by RazorPay in reality.
        //In Production would need to rewrite this code as this isn't provided by RazorPay, actually.
        paymentService.saveTransactionDetailsTest(orderId,"FAILURE","RAZORPAY");

        return new ResponseEntity<>("Could not complete transaction! Please try again later!!", HttpStatus.BAD_REQUEST);
    }

    //This goes back to Razorpay.
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Razorpay-Signature") String signHeader) {
        return new ResponseEntity<>("Transaction Recorded!", HttpStatus.OK);
        }
}
