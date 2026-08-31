package dev.aditya.paymentservice.Strategy;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import dev.aditya.paymentservice.Exceptions.CustomPaymentGatewayException;
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
    public String generatePaymentLink(long orderId, long amount, Long userId, String userName , String phoneNumber, String userEmail) {

        /*    Checkout Session Object :-
              "id": "cs_test_a11YYufWQzNY63zpQ6QSNRQhkUpVph4WRmzW0zWJO2znZKdVujZ0N0S22u",
              "object": "checkout.session",
              "after_expiration": null,
              "allow_promotion_codes": null,
              "amount_subtotal": 2198,
              "amount_total": 2198,
              "automatic_tax": {
                                    "enabled": false,
                                    "liability": null,
                                    "status": null
              },
              "billing_address_collection": null,
              "cancel_url": null,
              "client_reference_id": null,
              "consent": null,
              "consent_collection": null,
              "created": 1679600215,
              "currency": "usd",
              "custom_fields": [],
              "custom_text": {
                                "shipping_address": null,
                                "submit": null
              },
              "customer": null,
              "customer_creation": "if_required",
              "customer_details": null,
              "customer_email": null,
              "expires_at": 1679686615,
              "invoice": null,
              "invoice_creation": {
                                    "enabled": false,
                                    "invoice_data": {
                                                      "account_tax_ids": null,
                                                      "custom_fields": null,
                                                      "description": null,
                                                      "footer": null,
                                                      "issuer": null,
                                                      "metadata": {},
                                                      "rendering_options": null
                                                     }
                                     },
              "livemode": false,
              "locale": null,
              "metadata": {},
              "mode": "payment",
              "payment_intent": null,
              "payment_link": null,
              "payment_method_collection": "always",
              "payment_method_options": {},
              "payment_method_types": [ "card" ],
              "payment_status": "unpaid",
              "phone_number_collection": {
                                            "enabled": false
                                        },
              "recovered_from": null,
              "setup_intent": null,
              "shipping_address_collection": null,
              "shipping_cost": null,
              "shipping_details": null,
              "shipping_options": [],
              "status": "open",
              "submit_type": null,
              "subscription": null,
              "success_url": "https://example.com/success",
              "total_details": {
                                    "amount_discount": 0,
                                    "amount_shipping": 0,
                                    "amount_tax": 0
                              },
              "url": "https://checkout.stripe.com/c/pay/cs_test_a11YYufWQzNY63zpQ6QSNRQhkUpVph4WRmzW0zWJO2znZKdVujZ0N0S22u#fidkdWxOYHwnPyd1blpxYHZxWjA0SDdPUW5JbmFMck1wMmx9N2BLZjFEfGRUNWhqTmJ%2FM2F8bUA2SDRySkFdUV81T1BSV0YxcWJcTUJcYW5rSzN3dzBLPUE0TzRKTTxzNFBjPWZEX1NKSkxpNTVjRjN8VHE0YicpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
              "return_url": null,
              "ui_mode": "hosted_page"
            }
         */

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
            throw new CustomPaymentGatewayException("There seems to be some issue with the Gateway at the moment! Please try again later or select any other Gateway!!");
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
                        .setCancelUrl("https://example.com/stripe/failure{CHECKOUT_SESSION_ID}")// This gets triggered when customers shuts the browser window w/o completing the payment, or they click "Back" or "Cancel" button etc.
                        .putMetadata("order-id",String.valueOf(orderId))
                        // REQUIRED - What they're buying
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(createPrice(amount))
                                        .setQuantity(1L)
                                        .build())
                        //This tells what mode is it payment(once), recurring, etc.
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomer(createCustomer(userName ,phoneNumber,userEmail))// This helps avoid user to input their details everytime since we already know whose making the purchase better to send in the customer details beforehand.
                        // For the very first time when user initiates the payment, when the response comes back in....
                        // how would we map it to any user?
                        // For that reason we have to send in our userid so that later we can map and update the stripe customer id.
                        .setClientReferenceId(String.valueOf(userId))
                        //This sends a default invoice to user everytime they make a payment. Contains all details like customer details,order details(the line items), amount etc.
                        .setInvoiceCreation(SessionCreateParams.InvoiceCreation.builder()
                                            .setEnabled(true)
                                            .build())
                        .build();

        try {
            Session session = Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            throw new CustomPaymentGatewayException("There seems to be some issue with the Gateway at the moment! "
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
            throw new CustomPaymentGatewayException("There seems to be some issue with the Gateway at the moment! "
                    + "Please try again later or select any other Gateway!!");

        }
        return price.getId();
    }

    private String createCustomer(String userName , String phoneNumber, String userEmail) {
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setEmail(userEmail)
                    .setName(userName)
                    .setPhone(phoneNumber)
                    .build();
            Customer customer = null;
            try {
                customer = Customer.create(customerParams);
                // customer.getId() returns something like "cus_1234567890".
                // We just need Customer_Id, Stripe is smart enough to link all this together at payout.
                // Everything gets added to the session object which can be retrieved at success endpoint after transaction.
                return customer.getId();
            } catch (StripeException e) {
                throw new CustomPaymentGatewayException("There seems to be some issue with the Gateway at the moment!" +
                        " Please try again later or select any other Gateway!!");
            }
        }
}
