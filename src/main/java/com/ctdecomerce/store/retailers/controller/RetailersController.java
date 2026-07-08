package com.ctdecomerce.store.retailers.controller;

import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.cart.repo.CartRepo;
import com.ctdecomerce.store.dto.IdRequest;
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
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController("RetailersController")
@RequestMapping("/retailers")
public class RetailersController {
    private final RetailersService retailersService;
    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public RetailersController(RetailersService retailersService, CartRepo cartRepo, UserRepo userRepo) {
        this.retailersService = retailersService;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
    }

    @RateLimiter(name = "apiRateLimiter", fallbackMethod = "rateLimiterFallback")
    @PostMapping("/create")
    public ResponseEntity<ConnectedAccountDTO> createAccount(@RequestBody ConnectedAccountRequest connectedAccountRequest) throws StripeException {
        return new ResponseEntity<>(retailersService.createNewRetailer(connectedAccountRequest), HttpStatus.CREATED);
    }

    @RateLimiter(name = "apiRateLimiter", fallbackMethod = "rateLimiterFallback")
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
                UserModel user = userRepo.findUserModelByUserId(metadata.get("userId"));
                List<CartModel> carts = cartRepo.findCartModelsByUserId(user);
                cartRepo.deleteAll(carts);
                return ResponseEntity.status(HttpStatus.OK).body("Complete");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deserialization failed");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).
                body("No event found");
    }

    public ResponseEntity<String> rateLimiterFallback(RequestNotPermitted exception) {
        return ResponseEntity.status(429).body("TOO MANY REQUESTS");
    }
}

