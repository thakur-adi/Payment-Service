package dev.aditya.paymentservice.Strategy;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import dev.aditya.paymentservice.Exceptions.CustomStripeException;
import dev.aditya.paymentservice.Model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("StripePaymentGateway")
public class StripePaymentGateway implements IPaymentGateway {

    @Value("${stripe.api_key}")
    private String stripeApiKey;


    @Override
    //we are using Stripe's checkout session object instead of payment links for various benefits over it.
    public String generatePaymentLink(long orderId, long amount,User user) {
        Stripe.apiKey = stripeApiKey;
        /*This creates a payment link object which remains active forever, and doesn't contain buyer's info.
        But since we have the buyer information they suggest to directly create a checkout session object which payment link also does under the hood.
        Checkout session remains active for just 1 day. We can also pass in the user details this user does not have to always enter their details at the checkout page, they can just enter their payment details and move on.

            **PaymentLink Object**
        PaymentLinkCreateParams params = PaymentLinkCreateParams.builder()
                                        .addLineItem(PaymentLinkCreateParams.LineItem.builder()
                                                    .setPrice(String.valueOf(price))
                                                    .build())
                                        .setAfterCompletion(PaymentLinkCreateParams.AfterCompletion.builder()
                                                            .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                                                            .setRedirect(PaymentLinkCreateParams.AfterCompletion
                                                                            .Redirect.builder()
                                                                            .setUrl("https://google.com")
                                                                            .build())
                                                            .build())
                                        .setInvoiceCreation(PaymentLinkCreateParams.InvoiceCreation.builder() //sends an invoice with default format to users directly for proof of payment.These contain more info that receipts
                                                            .setEnabled(true).build())
                                        .build();
        try {
            paymentLink = PaymentLink.create(params);
        } catch (StripeException e) {
            throw new CustomStripeException("There seems to be some issue with the Gateway at the moment! Please try again later or select any other Gateway!!");
        }

        //We don't get a transaction-id until after the customer has made payment by clicking submit button on the gateway.
        // So no sense in returning a transaction here. Will just return a payment link.
        return convertToTransaction(paymentLink,amount,user);
        */

        SessionCreateParams params =
                SessionCreateParams.builder()
                        // This gets triggered once customer hits submit button, regardless of whether the transaction is successful or not. For payment success check we have webhooks.
                        // Stripe automatically replaces checkout_session_id with the actual session_id of the session object generated on call back.
                        .setSuccessUrl("https://example.com/stripe/success{CHECKOUT_SESSION_ID}")
                        .setCancelUrl("https://example.com/stripe/failure")// This gets triggered when customers shuts the browser window w/o completing the payment, or they click "Back" or "Cancel" button etc.
                        .putMetadata("order-id",String.valueOf(orderId))
                        // REQUIRED - What they're buying
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(createPrice(amount))
                                        .setQuantity(1L)
                                        .build())
                        //This tells what mode is it payment(once), recurring, etc.
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomer(createCustomer(user))// This helps avoid user to input their details everytime since we already know whose making the purchase better to send in the customer details beforehand.
                        //This sends a default invoice to user everytime they make a payment. Contains all details like customer details,order details(the line items), amount etc.
                        .setInvoiceCreation(SessionCreateParams.InvoiceCreation.builder()
                                            .setEnabled(true)
                                            .build())
                        .build();

        try {
            Session session = Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            throw new CustomStripeException("There seems to be some issue with the Gateway at the moment! "
                                            + "Please try again later or select any other Gateway!!");
        }
    }


    //Helper methods
    private String createPrice(long amount) {
        PriceCreateParams params =
                PriceCreateParams.builder()
                        .setCurrency("INR")
                        .setUnitAmount(amount)
                        .build();

        Price price = null;
        try {
            price = Price.create(params);
        } catch (StripeException e) {
            throw new CustomStripeException("There seems to be some issue with the Gateway at the moment! "
                    + "Please try again later or select any other Gateway!!");

        }
        return price.getId();
    }

    private String createCustomer(User user) {

        if(user.getStripeCustomerId() == null) {
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .setName(user.getName())
                    .setPhone(user.getPhoneNumber())
                    .build();

            Customer customer = null;
            try {
                customer = Customer.create(customerParams);
                // customer.getId() returns something like "cus_1234567890".
                // We just need Customer_Id, Stripe is smart enough to link all this together at payout.
                // Everything gets added to the session object which can be retrieved at success endpoint after transaction.
                return customer.getId();
            } catch (StripeException e) {
                throw new CustomStripeException("There seems to be some issue with the Gateway at the moment!" +
                        " Please try again later or select any other Gateway!!");
            }
        }
         return user.getStripeCustomerId();
    }


//    private Transaction convertToTransaction(PaymentLink paymentLink, long amount, User user){
//        Transaction transaction = new Transaction();
//        transaction.setId(Long.parseLong(paymentLink.getId()));
//        transaction.setAmount(amount);
//        transaction.setUser(user);
//        return transaction;
//    }
}
