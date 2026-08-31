package dev.aditya.paymentservice.Controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import dev.aditya.paymentservice.Exceptions.CustomPaymentGatewayException;
import dev.aditya.paymentservice.Model.PaymentGateway;
import dev.aditya.paymentservice.Service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe")
//This class contains all the code related to success path or Webhooks etc.
public class StripeController {

    @Autowired
    private IPaymentService paymentService;

    @Value("${stripe.endpoint_secret}")
    private String endpointSecret;


    //Not a good idea to put it in Payment Controller the remaining payment gateways have their own way of implementing the success path,
    //so will have to write a separate code for each of them, better to use webhooks.

    // This endpoint is not an API call made by code.
    // It is a browser address bar redirect, and web browsers can only open pages via GET requests.
    // Q. Why @RequestParam?
    // A-> Because a browser redirect is forced to use a GET request, it cannot inject a hidden JSON payload or a structured Java object.
    // The only place Stripe can pass data to your server is by printing it right into the text of the URL string itself.
    // just the session-id is passed because of url length limit
    @PostMapping("/success")
    public void capturePaymentSuccess(@RequestParam("session_id") String session_id) {
        try {
            Session session = Session.retrieve(session_id);
            paymentService.saveTransactionDetails(session.getPaymentIntent(),session.getMetadata().get("order-id")
                                                ,session.getAmountTotal(),"SUCCESS" //session.getPaymentStatus()
                                                , session.getPaymentMethodTypes().get(0),session.getClientReferenceId()
                                                ,PaymentGateway.STRIPE.toString());
        } catch (StripeException e) {
            throw new CustomPaymentGatewayException("There seems to be some issue with the Gateway at the moment! "
                    + "Please try again later or select any other Gateway!!");
        }

    }


    //This handles the .setCancelUrl(), this isn't webhook
    @PostMapping("/failure")
    public void capturePaymentFailure(@RequestParam("session_id") String session_id) {
        try {
            Session session = Session.retrieve(session_id);
            paymentService.saveTransactionDetails(session.getPaymentIntent(),session.getMetadata().get("order-id")
                    ,session.getAmountTotal(),"FAILURE" //session.getPaymentStatus()
                    , session.getPaymentMethodTypes().get(0),session.getClientReferenceId()
                    ,PaymentGateway.STRIPE.toString());
        } catch (StripeException e) {
            throw new CustomPaymentGatewayException("There seems to be some issue with the Gateway at the moment! "
                    + "Please try again later or select any other Gateway!!");
        }
    }


    //This handles the actual webhook. Stripe expects response otherwise it'll be stuck in a webhook retry loop(will keep on sending a new event).
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signHeader) {
        try {
            // Verify and unpack the event envelope
            Event event = Webhook.constructEvent(payload, signHeader, endpointSecret);
            Session session = (Session) event.getDataObjectDeserializer().getObject().get();
            if ("checkout.session.completed".equals(event.getType())) {
                // Open the envelope to get the Session object
                paymentService.saveTransactionDetails(session.getPaymentIntent(),session.getMetadata().get("order-id")
                                                    ,session.getAmountTotal(),session.getPaymentStatus()
                                                    , session.getPaymentMethodTypes().get(0),session.getClientReferenceId()
                                                    ,PaymentGateway.STRIPE.toString());
                return  new ResponseEntity<>("Webhook processed successfully!",HttpStatus.OK);
            }
            else if ("checkout.session.expired".equals((event.getType()))) {
                throw new CustomPaymentGatewayException("Session Expired! Payment Failed, please try again later!!");
            }
        } catch (SignatureVerificationException e) {
            throw new CustomPaymentGatewayException("Verification Failed");
        }
        return  new ResponseEntity<>("Webhook couldn't be processed, Retry!", HttpStatusCode.valueOf(400));
    }
}
