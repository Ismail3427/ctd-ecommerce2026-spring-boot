package com.ctdecomerce.store.retailers.controller;

import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.cart.repo.CartRepo;
import com.ctdecomerce.store.delivery.dto.CreateDeliveryDTO;
import com.ctdecomerce.store.delivery.service.DeliveryService;
import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.discounts.repository.DiscountsRepo;
import com.ctdecomerce.store.dto.IdRequest;
import com.ctdecomerce.store.orders.model.OrdersModel;
import com.ctdecomerce.store.orders.repository.OrdersRepo;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.repository.ProductRepo;
import com.ctdecomerce.store.retailers.dto.ConnectedAccountDTO;
import com.ctdecomerce.store.retailers.dto.ConnectedAccountRequest;
import com.ctdecomerce.store.retailers.service.RetailersService;
import com.ctdecomerce.store.user.model.UserModel;
import com.ctdecomerce.store.user.repository.UserRepo;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.TransferCreateParams;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController("RetailersController")
@RequestMapping("/retailers")
@Setter
public class RetailersController {
    private final RetailersService retailersService;
    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final OrdersRepo ordersRepo;
    private final DeliveryService deliveryService;
    private final DiscountsRepo discountsRepo;
    private final ProductRepo productRepo;
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;
    @Value("${w2}")
    private String webhooks2;

    public RetailersController(RetailersService retailersService, CartRepo cartRepo, UserRepo userRepo, OrdersRepo ordersRepo, DeliveryService deliveryService, DiscountsRepo discountsRepo, ProductRepo productRepo) {
        this.retailersService = retailersService;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.ordersRepo = ordersRepo;
        this.deliveryService = deliveryService;
        this.discountsRepo = discountsRepo;
        this.productRepo = productRepo;
    }

    @PostMapping("/create")
    public ResponseEntity<ConnectedAccountDTO> createAccount(@RequestBody ConnectedAccountRequest connectedAccountRequest) throws StripeException {
        return new ResponseEntity<>(retailersService.createNewRetailer(connectedAccountRequest), HttpStatus.CREATED);
    }

    @PostMapping("/webhook-checkout")
    public ResponseEntity<String> checkoutWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {
        if (sigHeader == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD REQUEST MISSING WEBHOOK SIGNITURE");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature verification failed");
        }
        if ("checkout.session.completed".equals(event.getType())) {
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            if (dataObjectDeserializer.getObject().isPresent()) {
                StripeObject stripeObject = dataObjectDeserializer.getObject().get();

                Session session = (Session) stripeObject;
                String payId = session.getPaymentIntent();
                PaymentIntent paymentIntent = PaymentIntent.retrieve(payId);
                String chargId = paymentIntent.getLatestCharge();
                Map<String, String> metadata = session.getMetadata();
                UserModel user = userRepo.findUserModelByUserId(metadata.get("userId"));
                List<CartModel> carts = cartRepo.findCartModelsByUserId(user, true);
                carts.forEach(cart -> {
                    cart.setShowing(false);
                    cartRepo.save(cart);
                    OrdersModel order = new OrdersModel();
                    order.setCart(cart);
                    ProductModel product = cart.getProduct();
                    product.setStock(product.getStock() - cart.getQuantity());
                    productRepo.save(product);
                    DiscountsModel discount = discountsRepo.findDiscountsModelByProduct(cart.getProduct());
                    if (discount != null) {
                        double finalPrice = (cart.getProduct().getPriceInCents() - (cart.getProduct().getPriceInCents() * discount.getOffer())) * cart.getQuantity();
                        order.setUser(user);
                        System.out.println(finalPrice);
                        order.setFinalPriceInCents(finalPrice);
                        ordersRepo.save(order);
                        deliveryService.createNewDelivery(new CreateDeliveryDTO(order.getId(), order.getCart().getProduct().getOwner().getId()));
                        try {
                            TransferCreateParams transferParams = TransferCreateParams.builder()
                                    .setAmount((long) (finalPrice * 0.87))
                                    .setCurrency("usd")
                                    .setDestination(cart.getProduct().getOwner().getAccountId())
                                    .setSourceTransaction(chargId)
                                    .setDescription("Transfer to merchant account")
                                    .build();
                            Transfer transfer = Transfer.create(transferParams);
                        } catch (StripeException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        double finalPrice = cart.getProduct().getPriceInCents() * cart.getQuantity();
                        order.setUser(user);
                        order.setFinalPriceInCents(finalPrice);
                        ordersRepo.save(order);
                        deliveryService.createNewDelivery(new CreateDeliveryDTO(order.getId(), order.getCart().getProduct().getOwner().getId()));
                        try {
                            TransferCreateParams transferParams = TransferCreateParams.builder()
                                    .setAmount((long) (finalPrice * 0.87))
                                    .setCurrency("usd")
                                    .setDestination(cart.getProduct().getOwner().getAccountId())
                                    .setSourceTransaction(chargId)
                                    .setDescription("Transfer to merchant account")
                                    .build();
                            Transfer transfer = Transfer.create(transferParams);
                        } catch (StripeException e) {
                            throw new RuntimeException(e);
                        }
                    }

                });
                return ResponseEntity.status(HttpStatus.OK).body("Complete");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deserialization failed");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).
                body("No event found");
    }

    @PostMapping("/webhook-retailer")
    public ResponseEntity<String> retailersWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {

        if (sigHeader == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD REQUEST MISSING WEBHOOK SIGNITURE");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhooks2);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature verification failed");
        }
        if ("account.updated".equals(event.getType())) {
            Account account = Account.retrieve(event.getAccount());
            if (account.getChargesEnabled()) {
                Map<String, String> metadata = account.getMetadata();
                retailersService.createAccountToDB(metadata.get("name"), account.getId(), metadata.get("userId"));
                return ResponseEntity.status(HttpStatus.OK).body("Success");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("No event");
    }
}

