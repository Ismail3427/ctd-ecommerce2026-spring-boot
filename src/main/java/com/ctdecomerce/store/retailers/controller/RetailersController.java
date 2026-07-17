package com.ctdecomerce.store.retailers.controller;

import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.cart.repo.CartRepo;
import com.ctdecomerce.store.delivery.dto.CreateDeliveryDTO;
import com.ctdecomerce.store.delivery.service.DeliveryService;
import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.discounts.repository.DiscountsRepo;
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
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.Transfer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.TransferCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/retailers")
public class RetailersController {

    private final RetailersService retailersService;
    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final OrdersRepo ordersRepo;
    private final DeliveryService deliveryService;
    private final DiscountsRepo discountsRepo;
    private final ProductRepo productRepo;

    /*
     * Webhook secret for:
     * Events from: Your account
     * Event: checkout.session.completed
     */
    @Value("${stripe.webhook.checkout-secret}")
    private String checkoutWebhookSecret;

    /*
     * Webhook secret for:
     * Events from: Connected accounts
     * Event: account.updated
     */
    @Value("${stripe.webhook.connect-secret}")
    private String connectWebhookSecret;

    public RetailersController(
            RetailersService retailersService,
            CartRepo cartRepo,
            UserRepo userRepo,
            OrdersRepo ordersRepo,
            DeliveryService deliveryService,
            DiscountsRepo discountsRepo,
            ProductRepo productRepo
    ) {
        this.retailersService = retailersService;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.ordersRepo = ordersRepo;
        this.deliveryService = deliveryService;
        this.discountsRepo = discountsRepo;
        this.productRepo = productRepo;
    }

    @PostMapping("/create")
    public ResponseEntity<ConnectedAccountDTO> createAccount(
            @RequestBody ConnectedAccountRequest request
    ) throws StripeException {

        ConnectedAccountDTO account =
                retailersService.createNewRetailer(request);

        return ResponseEntity.status(201).body(account);
    }

    /*
     * Stripe dashboard webhook:
     *
     * Endpoint:
     * https://YOUR-BACKEND/retailers/webhook/connect
     *
     * Listen to:
     * account.updated
     *
     * Source:
     * Connected accounts
     */
    @PostMapping("/webhook/connect")
    public ResponseEntity<String> connectWebhook(
            @RequestBody String payload,
            @RequestHeader(
                    value = "Stripe-Signature",
                    required = false
            ) String signature
    ) {
        if (signature == null || signature.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Missing Stripe-Signature header");
        }

        Event event;

        try {
            event = Webhook.constructEvent(
                    payload,
                    signature,
                    connectWebhookSecret
            );
        } catch (SignatureVerificationException exception) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid webhook signature");
        }

        if (!"account.updated".equals(event.getType())) {
            return ResponseEntity.ok("Event ignored");
        }

        StripeObject stripeObject = event
                .getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (!(stripeObject instanceof Account account)) {
            return ResponseEntity
                    .badRequest()
                    .body("Unable to deserialize connected account");
        }

        /*
         * chargesEnabled can be null, so don't use:
         *
         * if (account.getChargesEnabled())
         */
        if (!Boolean.TRUE.equals(account.getChargesEnabled())) {
            return ResponseEntity.ok("Account is not ready for charges");
        }

        Map<String, String> metadata = account.getMetadata();

        if (metadata == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Connected account has no metadata");
        }

        String name = metadata.get("name");
        String userId = metadata.get("userId");

        if (name == null || name.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Missing account metadata: name");
        }

        if (userId == null || userId.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Missing account metadata: userId");
        }

        retailersService.createAccountToDB(
                name,
                account.getId(),
                userId
        );

        return ResponseEntity.ok("Connected account saved");
    }

    /*
     * Stripe dashboard webhook:
     *
     * Endpoint:
     * https://YOUR-BACKEND/retailers/webhook/checkout
     *
     * Listen to:
     * checkout.session.completed
     *
     * Source:
     * Your account
     */
    @Transactional
    @PostMapping("/webhook/checkout")
    public ResponseEntity<String> checkoutWebhook(
            @RequestBody String payload,
            @RequestHeader(
                    value = "Stripe-Signature",
                    required = false
            ) String signature
    ) {
        if (signature == null || signature.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Missing Stripe-Signature header");
        }

        Event event;

        try {
            event = Webhook.constructEvent(
                    payload,
                    signature,
                    checkoutWebhookSecret
            );
        } catch (SignatureVerificationException exception) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid webhook signature");
        }

        if (!"checkout.session.completed".equals(event.getType())) {
            return ResponseEntity.ok("Event ignored");
        }

        StripeObject stripeObject = event
                .getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (!(stripeObject instanceof Session session)) {
            return ResponseEntity
                    .badRequest()
                    .body("Unable to deserialize Checkout Session");
        }

        /*
         * Prevent creating orders before Stripe confirms payment.
         */
        if (!"paid".equals(session.getPaymentStatus())) {
            return ResponseEntity.ok("Checkout completed but payment is not paid");
        }

        Map<String, String> metadata = session.getMetadata();

        if (metadata == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Checkout Session has no metadata");
        }

        String userId = metadata.get("userId");

        if (userId == null || userId.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Missing Checkout Session metadata: userId");
        }

        UserModel user = userRepo.findUserModelByUserId(userId);

        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body("User does not exist");
        }

        String paymentIntentId = session.getPaymentIntent();

        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Checkout Session has no PaymentIntent");
        }

        final String chargeId;

        try {
            PaymentIntent paymentIntent =
                    PaymentIntent.retrieve(paymentIntentId);

            chargeId = paymentIntent.getLatestCharge();
        } catch (StripeException exception) {
            return ResponseEntity
                    .internalServerError()
                    .body("Unable to retrieve PaymentIntent");
        }

        if (chargeId == null || chargeId.isBlank()) {
            return ResponseEntity
                    .internalServerError()
                    .body("PaymentIntent has no successful charge");
        }

        List<CartModel> carts =
                cartRepo.findCartModelsByUserId(user, true);

        if (carts == null || carts.isEmpty()) {
            return ResponseEntity.ok("No active cart items found");
        }

        try {
            for (CartModel cart : carts) {
                processCartItem(
                        cart,
                        user,
                        chargeId
                );
            }
        } catch (IllegalStateException exception) {
            /*
             * IllegalStateException represents invalid local data,
             * such as insufficient stock.
             */
            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        } catch (StripeException exception) {
            /*
             * Returning a non-2xx status causes Stripe to retry the webhook.
             */
            return ResponseEntity
                    .internalServerError()
                    .body("Unable to transfer retailer payment");
        }

        return ResponseEntity.ok("Checkout processed successfully");
    }

    private void processCartItem(
            CartModel cart,
            UserModel user,
            String chargeId
    ) throws StripeException {

        ProductModel product = cart.getProduct();

        if (product == null) {
            throw new IllegalStateException(
                    "Cart item does not contain a product"
            );
        }

//        if (cart.getQuantity() || cart.getQuantity() <= 0) {
//            throw new IllegalStateException(
//                    "Cart contains an invalid quantity"
//            );
//        }

        if (product.getStock() < cart.getQuantity()) {
            throw new IllegalStateException(
                    "Not enough stock for product: " + product.getName()
            );
        }

        if (product.getOwner() == null) {
            throw new IllegalStateException(
                    "Product does not have a retailer"
            );
        }

        String connectedAccountId =
                product.getOwner().getAccountId();

        if (connectedAccountId == null ||
                connectedAccountId.isBlank()) {
            throw new IllegalStateException(
                    "Retailer does not have a Stripe account"
            );
        }

        long finalPriceInCents =
                calculateFinalPriceInCents(cart);

        if (finalPriceInCents <= 0) {
            throw new IllegalStateException(
                    "Final price must be greater than zero"
            );
        }

        /*
         * Mark the cart item as no longer active.
         */
        cart.setShowing(false);
        cartRepo.save(cart);

        /*
         * Remove purchased quantity from stock.
         */
        product.setStock(
                product.getStock() - cart.getQuantity()
        );
        productRepo.save(product);

        /*
         * Create the order.
         */
        OrdersModel order = new OrdersModel();
        order.setCart(cart);
        order.setUser(user);

        /*
         * Change OrdersModel.finalPriceInCents to Long if possible.
         * Currency should generally be stored as an integer number of cents.
         */
        order.setFinalPriceInCents(finalPriceInCents);

        OrdersModel savedOrder = ordersRepo.save(order);

        deliveryService.createNewDelivery(
                new CreateDeliveryDTO(
                        savedOrder.getId(),
                        product.getOwner().getId()
                )
        );

        /*
         * The retailer receives 87%.
         * Your platform keeps 13%.
         */
        long retailerAmountInCents =
                Math.round(finalPriceInCents * 0.87);

        TransferCreateParams transferParams =
                TransferCreateParams.builder()
                        .setAmount(retailerAmountInCents)
                        .setCurrency("usd")
                        .setDestination(connectedAccountId)
                        .setSourceTransaction(chargeId)
                        .setDescription(
                                "Payment for order " + savedOrder.getId()
                        )
                        .putMetadata(
                                "orderId",
                                savedOrder.getId().toString()
                        )
                        .build();

        Transfer.create(transferParams);
    }

    private long calculateFinalPriceInCents(CartModel cart) {
        ProductModel product = cart.getProduct();

        long originalTotal =
                (long) product.getPriceInCents() *
                        cart.getQuantity();

        DiscountsModel discount =
                discountsRepo.findDiscountsModelByProduct(product);

        if (discount == null) {
            return originalTotal;
        }

        double offer = discount.getOffer();

        /*
         * Assumes an offer of 0.20 means 20% off.
         */
        if (offer < 0 || offer > 1) {
            throw new IllegalStateException(
                    "Discount offer must be between 0 and 1"
            );
        }

        return Math.round(originalTotal * (1 - offer));
    }
}