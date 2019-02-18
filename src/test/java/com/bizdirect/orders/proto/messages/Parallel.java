//package com.bizdirect.orders.proto.messages;
//
//import com.bizdirect.proto.messages.BizdirectCartMessages;
//import com.bizdirect.proto.services.BizdirectCartServiceGrpc;
//import com.google.gson.Gson;
//import io.restassured.response.Response;
//import io.restassured.specification.RequestSpecification;
//import net.minidev.json.JSONObject;
//import org.junit.Assert;
//import org.junit.runner.RunWith;
//import org.testng.annotations.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//import java.util.logging.Logger;
//
//
//import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
//
//
//@RunWith(ConcurrentTestRunner.class)
//public class Parallel extends BaseTest
//{
//
//    private static final Logger logger = Logger.getLogger(OrderStateMachine.class.getName());
//    Utils utils = new Utils();
//
//
//    String orderUid;
//    String transactionId;
//
//
//    BizdirectCartServiceGrpc.BizdirectCartServiceBlockingStub blockingStubCart;
//
//
//
//    @Test(description = "verify order state in DB,when payment success, callback from kafka",threadPoolSize = 2,invocationCount = 3,timeOut = 1000)
//    public void createCartOrderPaymentTC1() {
//
//
//        BizdirectCartMessages.UserIdentifier userIdentifier = BizdirectCartMessages.UserIdentifier
//                .newBuilder()
//                .setDeviceId("D" + utils.randomAlphaNumeric(5))
//                .setPartnerId(1)
//                .setUserUid("U" + utils.randomAlphaNumeric(5))
//                .build();
//
//        BizdirectCartMessages.CreateCartRequest createCartRequest = BizdirectCartMessages.CreateCartRequest
//                .newBuilder()
//                .setUserIdentifier(userIdentifier)
//                .setLatitude(1.2323)
//                .setLongitude(1.57767)
//                .setProviderId(1)
//                .setCategoryId(1)
//                .build();
//
//
//        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
//        BizdirectCartMessages.CreateCartResponse createCartResponse;
//
//        createCartResponse = blockingStubCart.createCart(createCartRequest);
//
//       // System.out.println(createCartResponse);
//
//        System.out.println("\nuse this cartUid  :" + createCartResponse.getCartUid());
//
//
//
//        BizdirectCartMessages.PriceDetails priceDetails = BizdirectCartMessages.PriceDetails
//                .newBuilder()
//                .setPrice(1000)
//                .setDiscount(5)
//                .setTax(2)
//                .setCurrencyCode("INR")
//                .build();
//
//        BizdirectCartMessages.AddItemRequest cartItem = BizdirectCartMessages.AddItemRequest
//                .newBuilder()
//                .setCartUid(createCartResponse.getCartUid())
//                .setDeviceId("D"+utils.randomAlphaNumeric(4))
//                .setItemPriceDetails(priceDetails)
//                .build();
//
//
//        BizdirectCartMessages.AddItemResponse itemResponse;
//
//        itemResponse = blockingStubCart.addItem(cartItem);
//        System.out.println("\nItem response Code"+itemResponse.getResponse().getResponseCode());
//
//
//
//
//        BizdirectCartMessages.CreateOrderRedirectRequest redirectRequest = BizdirectCartMessages.CreateOrderRedirectRequest
//                .newBuilder()
//                .setCartUid(createCartResponse.getCartUid())
//                .build();
//
//
//        BizdirectCartMessages.CreateOrderRedirectResponse createOrderRedirectResponse;
//
//
//        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
//        createOrderRedirectResponse = blockingStubCart.createOrderRequest(redirectRequest);
//        System.out.println("\nTransaction id"+createOrderRedirectResponse.getResponseMap().get("TRANSACTION_ID"));
//        //System.out.println(createOrderRedirectResponse.getResponseMap());
//        orderUid = createOrderRedirectResponse.getOrderUid();
//
//
//    }
//}
