package com.ctdecomerce.store.retailers.service;

import com.ctdecomerce.store.delivery.model.DeliveryModel;
import com.ctdecomerce.store.delivery.repository.DeliveryRepo;
import com.ctdecomerce.store.dto.AcctIdRequest;
import com.ctdecomerce.store.dto.LoginLinkRes;
import com.ctdecomerce.store.orders.repository.OrdersRepo;
import com.ctdecomerce.store.product.repository.ProductRepo;
import com.ctdecomerce.store.retailers.dto.*;
import com.ctdecomerce.store.retailers.mappers.OrderMapper;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import com.ctdecomerce.store.retailers.repository.RetailersRepo;
import com.ctdecomerce.store.user.model.UserModel;
import com.ctdecomerce.store.user.repository.UserRepo;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.LoginLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.AccountLoginLinkCreateParams;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Setter
@ToString
@AllArgsConstructor
public class RetailersService {
    private final RetailersRepo retailersRepo;
    private final UserRepo userRepo;
    private final OrdersRepo ordersRepo;
    private final DeliveryRepo deliveryRepo;
    private final OrderMapper orderMapper;
    private final ProductRepo productRepo;
    private StripeClient stripeClient;


    @Transactional
    public LoginLinkRes generateLoginLink(AcctIdRequest acctIdRequest) throws StripeException {
        AccountLoginLinkCreateParams params = AccountLoginLinkCreateParams.builder().build();
        LoginLink loginLink = stripeClient.v1().accounts().loginLinks().create(acctIdRequest.getAccountId(), params);
        return new LoginLinkRes(loginLink.getUrl());
    }

    @Transactional
    public ConnectedAccountDTO createNewRetailer(ConnectedAccountRequest connectedAccountRequest) throws StripeException {
        AccountCreateParams accountCreateParams = AccountCreateParams
                .builder()
                .putMetadata("name", connectedAccountRequest.getName())
                .putMetadata("userId", connectedAccountRequest.getUserId())
                .setType(AccountCreateParams.Type.EXPRESS)
                .build();
        Account account = Account.create(accountCreateParams);

        AccountLinkCreateParams accountLinkCreateParams = AccountLinkCreateParams.builder()
                .setAccount(account.getId())
                .setRefreshUrl("http://localhost:3000")
                .setReturnUrl("http://localhost:3000")
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        AccountLink accountLink = AccountLink.create(accountLinkCreateParams);
        return new ConnectedAccountDTO(accountLink.getUrl());
    }

    @Transactional
    public void createAccountToDB(String name, String accountId, String userId) {
        RetailersModel retailersModel = new RetailersModel();
        retailersModel.setName(name);
        retailersModel.setAccountId(accountId);
        UserModel user = userRepo.findUserModelByUserId(userId);
        retailersModel.setUser(user);
        retailersRepo.save(retailersModel);
    }

    @Transactional
    public IsRetailer checkIfRetailer(UserIdRequest userIdRequest) {
        System.out.println(userIdRequest);
        try {
            var user = userRepo.findUserModelByUserId(userIdRequest.getUserId());
            System.out.println(user);
            var retailer = retailersRepo.findRetailerByUser(user);
            System.out.println(retailer);
            System.out.println(user.toString() + ": " + retailer.getUser().toString());
            if (retailer.getUser() == user) {
                return new IsRetailer(true);
            }
            return new IsRetailer(false);
        } catch (NoSuchElementException | NullPointerException e) {
            System.out.println("passed and except");
            return new IsRetailer(false);
        }
    }


    @Transactional
    public RetailersModel findRetailerFromUser(UserIdRequest userIdRequest) {
        try {
            var user = userRepo.findUserModelByUserId(userIdRequest.getUserId());
            var retailer = retailersRepo.findRetailerByUser(user);
            if (retailer.getUser() == user) {
                return retailer;
            }
            return null;
        } catch (NoSuchElementException | NullPointerException e) {
            return null;
        }
    }

    @Transactional
    public List<OrderItemDto> findRetailerOrders(RetailerIdRequest retailerIdRequest) {
        var retailer = retailersRepo.findById(retailerIdRequest.getRetailer_id()).orElseThrow(() -> new RuntimeException("Retailer not Found"));
        List<DeliveryModel> deliveryModel = deliveryRepo.findByRetailerId(retailer.getId());
        return deliveryModel.
                stream()
                .map(DeliveryModel::getOrder)
                .map(orderMapper::toDto)
                .toList();
    }

}