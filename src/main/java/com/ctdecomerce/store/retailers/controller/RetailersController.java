package com.ctdecomerce.store.retailers.controller;

import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.cart.repo.CartRepo;
import com.ctdecomerce.store.dto.IdRequest;
import com.ctdecomerce.store.orders.model.OrdersModel;
import com.ctdecomerce.store.orders.repository.OrdersRepo;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.retailers.dto.ConnectedAccountDTO;
import com.ctdecomerce.store.retailers.dto.ConnectedAccountRequest;
import com.ctdecomerce.store.retailers.service.RetailersService;
import com.ctdecomerce.store.user.model.UserModel;
import com.ctdecomerce.store.user.repository.UserRepo;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
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
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public RetailersController(RetailersService retailersService, CartRepo cartRepo, UserRepo userRepo, OrdersRepo ordersRepo) {
        this.retailersService = retailersService;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.ordersRepo = ordersRepo;
    }

    @PostMapping("/create")
    public ResponseEntity<ConnectedAccountDTO> createAccount(@RequestBody ConnectedAccountRequest connectedAccountRequest) throws StripeException {
        return new ResponseEntity<>(retailersService.createNewRetailer(connectedAccountRequest), HttpStatus.CREATED);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> retailersWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {
        if (sigHeader == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD REQUEST MISSING WEBHOOK SIGNITURE");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
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
        if ("checkout.session.completed".equals(event.getType())) {
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            if (dataObjectDeserializer.getObject().isPresent()) {
                StripeObject stripeObject = dataObjectDeserializer.getObject().get();
                Session session = (Session) stripeObject;
                Map<String, String> metadata = session.getMetadata();
                String stringifiedList = metadata.get("cartIds");
                if (stringifiedList != null) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<String> carts = mapper.readValue(stringifiedList, new TypeReference<List<String>>(){});
                        List<CartModel> finalCarts = new ArrayList<>();
                        for (String cartId : carts) {
                            CartModel cart = cartRepo.findById(UUID.fromString(cartId)).orElse(null);
                            finalCarts.add(cart);
                        }
                        UserModel user = userRepo.findUserModelByUserId(metadata.get("userId"));
                        for (CartModel cart : finalCarts) {
                            cart.setShowing(false);
                            cartRepo.save(cart);
                        }
                        System.out.println(finalCarts);
                        OrdersModel order = new OrdersModel();
                        order.setUser(user);
                        order.setCart(finalCarts);
                        System.out.println(order);
                        ordersRepo.save(order);
                        ordersRepo.delete(order);
                        return ResponseEntity.status(HttpStatus.OK).body("Complete");
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed");
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deserialization failed");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).
                body("No event found");
    }
}

