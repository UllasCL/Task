package com.bizdirect.orders.proto.messages;

import com.bizdirect.proto.messages.BizdirectCartMessages;
import com.bizdirect.proto.services.BizdirectCartServiceGrpc;
import com.google.gson.Gson;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class KafkaTestCases extends BaseTest {


    private static final Logger logger = Logger.getLogger(KafkaTestCases.class.getName());
    Utils utils = new Utils();


    BizdirectCartServiceGrpc.BizdirectCartServiceBlockingStub blockingStubCart;


    String orderUid;


    @Test(description = "verify order state in DB,when payment success and refund success, callback from kafka")
    public void createCartOrderPaymentTC1() {


        BizdirectCartMessages.UserIdentifier userIdentifier = BizdirectCartMessages.UserIdentifier
                .newBuilder()
                .setDeviceId("D" + utils.randomAlphaNumeric(5))
                .setPartnerId(1)
                .setUserUid("U" + utils.randomAlphaNumeric(5))
                .build();

        BizdirectCartMessages.CreateCartRequest createCartRequest = BizdirectCartMessages.CreateCartRequest
                .newBuilder()
                .setUserIdentifier(userIdentifier)
                .setLatitude(1.2323)
                .setLongitude(1.57767)
                .setProviderId(1)
                .setCategoryId(1)
                .build();


        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
        BizdirectCartMessages.CreateCartResponse createCartResponse = blockingStubCart.createCart(createCartRequest);


        System.out.println(createCartResponse);

        System.out.println("\n\n\n\n cartUid  :" + createCartResponse.getCartUid());


        BizdirectCartMessages.PriceDetails priceDetails = BizdirectCartMessages.PriceDetails
                .newBuilder()
                .setPrice(1000)
                .setDiscount(5)
                .setTax(2)
                .setCurrencyCode("INR")
                .build();

        BizdirectCartMessages.AddItemRequest cartItem = BizdirectCartMessages.AddItemRequest
                .newBuilder()
                .setCartUid(createCartResponse.getCartUid())
                .setDeviceId("D" + utils.randomAlphaNumeric(4))
                .setItemPriceDetails(priceDetails)
                .build();


        BizdirectCartMessages.AddItemResponse itemResponse;

        itemResponse = blockingStubCart.addItem(cartItem);
        System.out.println(itemResponse);


        BizdirectCartMessages.CreateOrderRedirectRequest redirectRequest = BizdirectCartMessages.CreateOrderRedirectRequest
                .newBuilder()
                .setCartUid(createCartResponse.getCartUid())
                .build();


        BizdirectCartMessages.CreateOrderRedirectResponse createOrderRedirectResponse;


        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
        createOrderRedirectResponse = blockingStubCart.createOrderRequest(redirectRequest);
        System.out.println("Transaction_id ; "+createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        System.out.println("Order_id : "+createOrderRedirectResponse.getResponseMap().get("order_uid"));
        orderUid = createOrderRedirectResponse.getOrderUid();


        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            System.out.println(e);
        }



        RequestSpecification request1 = given();
        request1.header("Content-Type", "application/json");
        request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");


        JsonHeader dto = new JsonHeader();
        Map<String, String> payments_info = new HashMap<>();
        payments_info.put("transaction_id", createOrderRedirectResponse.getResponseMapMap().get("transaction_id"));
        payments_info.put("order_uid", createOrderRedirectResponse.getResponseMap().get("order_uid"));

        dto.setRequestMap(payments_info);

        Gson gson = new Gson();
        String json1 = gson.toJson(dto);

        System.out.println(json1);

        request1.body(json1);

        Response response1 = request1.post(properties.getProperty("payment.gateway.payload.url"));
        int code1 = response1.getStatusCode();
        response1.getBody().print();
        Assert.assertEquals(code1, 200);

        System.out.println("\n\n\n");




        RequestSpecification request = given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");

        JSONObject json = new JSONObject();
        json.put("transaction_id", createOrderRedirectResponse.getResponseMapMap().get("transaction_id"));
        json.put("callback", "true");
        json.put("success", "true");

        System.out.println(json + "\n\n\n");

        request.body(json.toJSONString());

        Response responsePost = request.post(properties.getProperty("payment.gateway.payment.url"));
        int code = responsePost.getStatusCode();
        responsePost.getBody().print();
        Assert.assertEquals(code, 200);


        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {

            String query1 = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String statusP = utils.getValueFromDB(conPayment, query1);
            System.out.println("Payment : " + statusP);


            assertEquals("SUCCESS", statusP);


            String query2 = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";

            String statusO = utils.getValueFromDB(conOrder, query2);
            System.out.println("Order : " + statusO);


            assertEquals("FULFILLMENT_INITIATED", statusO);

            String query3 = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusC = utils.getValueFromDB(conCart, query3);
            System.out.println("Cart : " + statusC);

            assertEquals("FULFILLMENT_IN_PROGRESS", statusC);


            /**
             * Time delay to update cart db.
             */

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            /**
             * PostFulfillment form vendor
             */


            BizdirectCartMessages.PostFulfillmentRequest postFulfillmentRequest = BizdirectCartMessages.PostFulfillmentRequest
                    .newBuilder()
                    .setOrderUid(orderUid)
                    .setProviderId(1)
                    .setDescription("Something")
                    .setFulfillmentStatus(BizdirectCartMessages.FulfillmentStatus.FULFILLMENT_FAILURE)
                    .build();


            BizdirectCartMessages.PostFulfillmentResponse postFulfillmentResponse;
            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
            postFulfillmentResponse = blockingStubCart.postFulfillmentCall(postFulfillmentRequest);
            System.out.println(postFulfillmentResponse);
            System.out.println("Transaction id : "+postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get("transaction_id"));


            String queryCart = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusCart = utils.getValueFromDB(conCart, queryCart);
            System.out.println("\n\n\n\n\n\n" + statusCart);

            assertEquals("FULFILLMENT_FAILED_REFUND_INITIATED", statusCart);


            if ("FULFILLMENT_FAILED_REFUND_INITIATED".equals(statusCart)) {




                RequestSpecification requestRk = given();
                requestRk.header("Content-Type", "application/json");
                requestRk.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");

                JSONObject jsonRk = new JSONObject();
                jsonRk.put("transaction_id", postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get("transaction_id"));
                jsonRk.put("callback", "true");
                jsonRk.put("success", "true");

                System.out.println("\n\n" + jsonRk);

                requestRk.body(jsonRk.toJSONString());

                Response responseRk = requestRk.post(properties.getProperty("payment.gateway.payment.url"));
                responseRk.getBody().print();
                Assert.assertEquals(200, responseRk.getStatusCode());



                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (Exception e) {
                    System.out.println(e);
                }


                System.out.println("\n\n\n\n\n");


                String queryPR = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get("transaction_id") + "'";

                String statusPR = utils.getValueFromDB(conPayment, queryPR);
                System.out.println("Payment : " + statusPR);


                assertEquals(statusPR, "SUCCESS");


                String queryOR = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get("order_uid") + "'";

                String statusOR = utils.getValueFromDB(conOrder, queryOR);
                System.out.println("Order : " + statusOR);


                assertEquals("REFUND_COMPLETED",statusOR);

                String queryCR = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

                String statusCR = utils.getValueFromDB(conCart, queryCR);
                System.out.println("Cart : "+statusCR);

                assertEquals("CART_FULL_REFUND_COMPLETED",statusCR);



            }


        } catch (Exception e) {

        }
    }

    @Test(description = "Verify order state In DB when payment is success and refund is failed")
    public void createCartOrderPaymentTC2() {


        BizdirectCartMessages.UserIdentifier userIdentifier = BizdirectCartMessages.UserIdentifier
                .newBuilder()
                .setDeviceId("D" + utils.randomAlphaNumeric(5))
                .setPartnerId(1)
                .setUserUid("U" + utils.randomAlphaNumeric(5))
                .build();

        BizdirectCartMessages.CreateCartRequest createCartRequest = BizdirectCartMessages.CreateCartRequest
                .newBuilder()
                .setUserIdentifier(userIdentifier)
                .setLatitude(1.2323)
                .setLongitude(1.57767)
                .setProviderId(1)
                .setCategoryId(1)
                .build();


        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
        BizdirectCartMessages.CreateCartResponse createCartResponse;

        createCartResponse = blockingStubCart.createCart(createCartRequest);

        System.out.println(createCartResponse);

        System.out.println("\n\n\n\nuse this cartUid  :" + createCartResponse.getCartUid());


        BizdirectCartMessages.PriceDetails priceDetails = BizdirectCartMessages.PriceDetails
                .newBuilder()
                .setPrice(1000)
                .setDiscount(5)
                .setTax(2)
                .setCurrencyCode("INR")
                .build();

        BizdirectCartMessages.AddItemRequest cartItem = BizdirectCartMessages.AddItemRequest
                .newBuilder()
                .setCartUid(createCartResponse.getCartUid())
                .setDeviceId("1")
                .setItemPriceDetails(priceDetails)
                .build();


        BizdirectCartMessages.AddItemResponse itemResponse;

        itemResponse = blockingStubCart.addItem(cartItem);
        System.out.println(itemResponse);


        BizdirectCartMessages.CreateOrderRedirectRequest redirectRequest = BizdirectCartMessages.CreateOrderRedirectRequest
                .newBuilder()
                .setCartUid(createCartResponse.getCartUid())
                .build();


        BizdirectCartMessages.CreateOrderRedirectResponse createOrderRedirectResponse;


        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
        createOrderRedirectResponse = blockingStubCart.createOrderRequest(redirectRequest);
        System.out.println(createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        System.out.println(createOrderRedirectResponse);
        orderUid = createOrderRedirectResponse.getOrderUid();


        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            System.out.println(e);
        }



        RequestSpecification request1 = given();
        request1.header("Content-Type", "application/json");
        request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");


        JsonHeader dto = new JsonHeader();
        Map<String, String> payments_info = new HashMap<>();
        payments_info.put("transaction_id", createOrderRedirectResponse.getResponseMapMap().get("transaction_id"));
        payments_info.put("order_uid", createOrderRedirectResponse.getOrderUid());

        dto.setRequestMap(payments_info);

        Gson gson = new Gson();
        String json1 = gson.toJson(dto);

        System.out.println(json1);

        request1.body(json1);

        Response response1 = request1.post(properties.getProperty("payment.gateway.payload.url"));
        int code1 = response1.getStatusCode();
        response1.getBody().print();
        Assert.assertEquals(code1, 200);

        System.out.println("\n\n\n");




        RequestSpecification request = given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");

        JSONObject json = new JSONObject();
        json.put("transaction_id", createOrderRedirectResponse.getResponseMapMap().get("transaction_id"));
        json.put("callback", "true");
        json.put("success", "true");

        System.out.println(json + "\n\n\n");

        request.body(json.toJSONString());

        Response responsePost = request.post(properties.getProperty("payment.gateway.payment.url"));
        int code = responsePost.getStatusCode();
        responsePost.getBody().print();
        Assert.assertEquals(code, 200);


        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {

            String query1 = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String statusP = utils.getValueFromDB(conPayment, query1);
            System.out.println("Payment : " + statusP);


            assertEquals("SUCCESS", statusP);


            String query2 = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";

            String statusO = utils.getValueFromDB(conOrder, query2);
            System.out.println("Order : " + statusO);


            assertEquals("FULFILLMENT_INITIATED", statusO);

            String query3 = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusC = utils.getValueFromDB(conCart, query3);
            System.out.println("Cart : " + statusC);

            assertEquals("FULFILLMENT_IN_PROGRESS", statusC);


            /**
             * Time delay to update cart db.
             */

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            /**
             * PostFulfillment form vendor
             */


            BizdirectCartMessages.PostFulfillmentRequest postFulfillmentRequest = BizdirectCartMessages.PostFulfillmentRequest
                    .newBuilder()
                    .setOrderUid(orderUid)
                    .setProviderId(1)
                    .setDescription("Something")
                    .setFulfillmentStatus(BizdirectCartMessages.FulfillmentStatus.FULFILLMENT_FAILURE)
                    .build();

            BizdirectCartMessages.PostFulfillmentResponse postFulfillmentResponse;
            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
            postFulfillmentResponse = blockingStubCart.postFulfillmentCall(postFulfillmentRequest);
            System.out.println(postFulfillmentResponse);


            String queryCart = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusCart = utils.getValueFromDB(conCart, queryCart);
            System.out.println("\n\n\n\n\n\n" + statusCart);


            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            if ("FULFILLMENT_FAILED_REFUND_INITIATED".equals(statusCart)) {




                RequestSpecification requestRk = given();
                requestRk.header("Content-Type", "application/json");
                requestRk.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");

                JSONObject jsonRk = new JSONObject();
                jsonRk.put("transaction_id", postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get("transaction_id"));
                jsonRk.put("callback", "true");
                jsonRk.put("success", "false");

                System.out.println("\n\n" + jsonRk);

                requestRk.body(jsonRk.toJSONString());

                Response responseRk = requestRk.post(properties.getProperty("payment.gateway.payment.url"));
                responseRk.getBody().print();
                Assert.assertEquals(200, responseRk.getStatusCode());


                System.out.println("\n\n\n\n\n");
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (Exception e) {
                    System.out.println(e);
                }



                String queryPR = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get("transaction_id") + "'";

                String statusPR = utils.getValueFromDB(conPayment, queryPR);
                System.out.println("Payment : " + statusPR);


                assertEquals( "FAILED",statusPR);


                String queryOR = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get("order_uid") + "'";

                String statusOR = utils.getValueFromDB(conOrder, queryOR);
                System.out.println("Order : " + statusOR);


                assertEquals( "REFUND_FAILED",statusOR);

                String queryCR = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

                String statusCR = utils.getValueFromDB(conCart, queryCR);
                System.out.println("cart  : "+statusCR);

                assertEquals( "CART_FULL_REFUND_FAILED",statusCR);


            }


        } catch (Exception e) {

        }
    }


    @Test(description = "verify order state in DB,when payment failed")
    public void createCartOrderPaymentTC3() {


        BizdirectCartMessages.UserIdentifier userIdentifier = BizdirectCartMessages.UserIdentifier
                .newBuilder()
                .setDeviceId("D" + utils.randomAlphaNumeric(5))
                .setPartnerId(1)
                .setUserUid("U" + utils.randomAlphaNumeric(5))
                .build();

        BizdirectCartMessages.CreateCartRequest createCartRequest = BizdirectCartMessages.CreateCartRequest
                .newBuilder()
                .setUserIdentifier(userIdentifier)
                .setLatitude(1.2323)
                .setLongitude(1.57767)
                .setProviderId(1)
                .setCategoryId(1)
                .build();


        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
        BizdirectCartMessages.CreateCartResponse createCartResponse;

        createCartResponse = blockingStubCart.createCart(createCartRequest);

        System.out.println(createCartResponse);

        System.out.println("\n\n\n\nuse this cartUid  :" + createCartResponse.getCartUid());


        BizdirectCartMessages.PriceDetails priceDetails = BizdirectCartMessages.PriceDetails
                .newBuilder()
                .setPrice(1000)
                .setDiscount(5)
                .setTax(2)
                .setCurrencyCode("INR")
                .build();

        BizdirectCartMessages.AddItemRequest cartItem = BizdirectCartMessages.AddItemRequest
                .newBuilder()
                .setCartUid(createCartResponse.getCartUid())
                .setDeviceId("1")
                .setItemPriceDetails(priceDetails)
                .build();


        BizdirectCartMessages.AddItemResponse itemResponse;

        itemResponse = blockingStubCart.addItem(cartItem);
        System.out.println(itemResponse);


        BizdirectCartMessages.CreateOrderRedirectRequest redirectRequest = BizdirectCartMessages.CreateOrderRedirectRequest
                .newBuilder()
                .setCartUid(createCartResponse.getCartUid())
                .build();


        BizdirectCartMessages.CreateOrderRedirectResponse createOrderRedirectResponse;


        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
        createOrderRedirectResponse = blockingStubCart.createOrderRequest(redirectRequest);
        System.out.println(createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        System.out.println(createOrderRedirectResponse);
        orderUid = createOrderRedirectResponse.getOrderUid();


        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            System.out.println(e);
        }



        RequestSpecification request1 = given();
        request1.header("Content-Type", "application/json");
        request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");


        JsonHeader dto = new JsonHeader();
        Map<String, String> payments_info = new HashMap<>();
        payments_info.put("transaction_id", createOrderRedirectResponse.getResponseMapMap().get("transaction_id"));
        payments_info.put("order_uid", createOrderRedirectResponse.getOrderUid());

        dto.setRequestMap(payments_info);

        Gson gson = new Gson();
        String json1 = gson.toJson(dto);

        System.out.println(json1);

        request1.body(json1);

        Response response1 = request1.post(properties.getProperty("payment.gateway.payload.url"));
        int code1 = response1.getStatusCode();
        response1.getBody().print();
        Assert.assertEquals(code1, 200);

        System.out.println("\n\n\n");




        RequestSpecification request = given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");

        JSONObject json = new JSONObject();
        json.put("transaction_id", createOrderRedirectResponse.getResponseMapMap().get("transaction_id"));
        json.put("callback", "true");
        json.put("success", "false");

        System.out.println(json + "\n\n");

        request.body(json.toJSONString());

        Response responsePost = request.post(properties.getProperty("payment.gateway.payment.url"));
        responsePost.getBody().print();
        Assert.assertEquals(responsePost.getStatusCode(), 200);


        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {


            String query1 = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String statusP = utils.getValueFromDB(conPayment, query1);
            System.out.println("Payment : " + statusP);


            assertEquals(statusP, "FAILED");


            String query2 = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + createOrderRedirectResponse.getOrderUid() + "'";

            String statusO = utils.getValueFromDB(conOrder, query2);
            System.out.println("Order : " + statusO);


            assertEquals("PAYMENT_FAILED", statusO);

            String query3 = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusC = utils.getValueFromDB(conCart, query3);
            System.out.println("Cart : " + statusC);

            assertEquals("PAYMENT_FAILED", statusC);


        } catch (Exception e) {

        }
    }


    /**
     * No kafka callback . The DB will be updated using Scheduler.
     */
//    @Test(description = "callback false, success false, we can check working of scheduler here.")
//    public void createCartOrderPaymentTC4() {
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
//        System.out.println(createCartResponse);
//
//        System.out.println("\n\n\n\nuse this cartUid  :" + createCartResponse.getCartUid());
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
//                .setDeviceId("1")
//                .setItemPriceDetails(priceDetails)
//                .build();
//
//
//        BizdirectCartMessages.AddItemResponse itemResponse;
//
//        itemResponse = blockingStubCart.addItem(cartItem);
//        System.out.println(itemResponse);
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
//        System.out.println(createOrderRedirectResponse.getResponseMap().get("transaction_id"));
//        System.out.println(createOrderRedirectResponse);
//        orderUid = createOrderRedirectResponse.getOrderUid();
//
//
//        try {
//
//
//            String query2 = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";
//
//            String statusO = utils.getValueFromDB(conOrder, query2);
//            System.out.println("Order : " + statusO);
//
//            String query1 = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";
//
//            String statusP = utils.getValueFromDB(conPayment, query1);
//            System.out.println("Payment : " + statusP);
//        } catch (Exception e) {
//
//        }
//
//
//        String url1 = "http://payment-gateway.172.16.1.0.nip.io/api/payment-gateway/v1/mock/gateway/mobile/payload";
//
//        RequestSpecification request1 = given();
//        request1.header("Content-Type", "application/json");
//        request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//
//        JsonHeader dto = new JsonHeader();
//        Map<String, String> payments_info = new HashMap<>();
//        payments_info.put("transaction_id", createOrderRedirectResponse.getResponseMapMap().get("transaction_id"));
//        payments_info.put("order_uid", createOrderRedirectResponse.getOrderUid());
//
//        dto.setRequestMap(payments_info);
//
//        Gson gson = new Gson();
//        String json1 = gson.toJson(dto);
//
//        System.out.println(json1);
//
//        request1.body(json1);
//
//        Response response1 = request1.post(url1);
//        int code1 = response1.getStatusCode();
//        response1.getBody().print();
//        Assert.assertEquals(code1, 200);
//
//        System.out.println("\n\n\n");
//
//
//        String url = "http://payment-gateway.172.16.1.0.nip.io/api/payment-gateway/v1/mock/gateway/payment";
//
//
//        RequestSpecification request = given();
//        request.header("Content-Type", "application/json");
//        request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//        JSONObject json = new JSONObject();
//        json.put("transaction_id", createOrderRedirectResponse.getResponseMapMap().get("transaction_id"));
//        json.put("callback", "false");
//        json.put("success", "false");
//
//        System.out.println(json + "\n\n\n");
//
//        request.body(json.toJSONString());
//
//        Response responsePost = request.post(url);
//        responsePost.getBody().print();
//        Assert.assertEquals(responsePost.getStatusCode(), 200);
//
//
//
//
//        try {
//
//            String query1 = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";
//
//            String statusP = utils.getValueFromDB(conPayment, query1);
//            System.out.println("Payment : " + statusP);
//
//
//            assertEquals("FAILED",statusP);
//
//            String query2 = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";
//
//            String statusO = utils.getValueFromDB(conOrder, query2);
//            System.out.println("Order : " + statusO);
//
//
//            assertEquals("PAYMENT_FAILED", statusO);
//
//            String query3 = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";
//
//            String statusC = utils.getValueFromDB(conCart, query3);
//            System.out.println(statusC);
//
//            assertEquals("PAYMENT_FAILED", statusC);
//
//
//        } catch (Exception e) {
//        }
//    }
//

}







