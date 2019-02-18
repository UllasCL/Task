//package com.bizdirect.proto.messages;
//
//import com.bizdirect.proto.services.BizdirectCartServiceGrpc;
//import com.google.gson.Gson;
//import io.restassured.response.Response;
//import io.restassured.specification.RequestSpecification;
//import net.minidev.json.JSONObject;
//import org.junit.Assert;
//import org.testng.annotations.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import static io.restassured.RestAssured.given;
//import static org.junit.Assert.assertEquals;
//
//
//public class OrderStateMachine extends BaseTest {
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
//    @Test(description = "Check order state after order created")
//    public void AacreateOrderAndCheckResponseAssertTC1() {
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
//
//        BizdirectCartMessages.CreateCartResponse createCartResponse = blockingStubCart.createCart(createCartRequest);
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
//        BizdirectCartMessages.AddItemResponse itemResponse = blockingStubCart.addItem(cartItem);
//
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
//        System.out.println("Transaction_id : " + createOrderRedirectResponse.getResponseMap().get("transaction_id"));
//        System.out.println(createOrderRedirectResponse);
//        orderUid = createOrderRedirectResponse.getOrderUid();
//
//        try {
//
//
//            String querryOrderstate = "SELECT order_state from nuclei_orders.orders where order_uid = '" + createOrderRedirectResponse.getOrderUid() + "'";
//            String order_State = utils.getValueFromDB(conOrder, querryOrderstate);
//            System.out.println("Order state : " + order_State);
//
//            if (createOrderRedirectResponse.getResponseMap().get("transaction_id") == null) {
//                assertEquals(order_State, "PAYMENT_INITIATION_FAILED");
//                /* Again creating order using same cartId*/
//            } else {
//                assertEquals(order_State, "PAYMENT_INITIATED");
//
//                    /*
//                        call next test case
//                    */
//            }
//
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//
//    }
//
//
//    /**
//     * Order will only initiate payment
//     */
//    @Test(description = "Verify order state when payment is success")
//    public void AbinitiatePaymentRequestTC1() {
//
//        String OrderUid;
//        try {
//
//
//            String queryOrderUid = "SELECT order_uid from nuclei_orders.orders where order_state= \"PAYMENT_INITIATED\" order by id desc limit 1";
//
//            OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//            System.out.println(OrderUid);
//
//            String queryTransactionId = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//            transactionId = utils.getValueFromDB(conPayment, queryTransactionId);
//            System.out.println(transactionId);
//
//
//
//            RequestSpecification request1 = given();
//            request1.header("Content-Type", "application/json");
//            request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//
//            JsonHeader dto = new JsonHeader();
//            Map<String, String> payments_info = new HashMap<>();
//            payments_info.put("transaction_id", transactionId);
//            payments_info.put("order_uid", OrderUid);
//
//            dto.setRequestMap(payments_info);
//
//            Gson gson = new Gson();
//            String json1 = gson.toJson(dto);
//
//            System.out.println(json1);
//
//            request1.body(json1);
//
//            Response response1 = request1.post(properties.getProperty("payment.gateway.payload.url"));
//            int code1 = response1.getStatusCode();
//            response1.getBody().print();
//            Assert.assertEquals(code1, 200);
//
//            System.out.println("\n\n\n");
//
//
//
//
//            RequestSpecification request = given();
//            request.header("Content-Type", "application/json");
//            request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//            JSONObject json = new JSONObject();
//            json.put("transaction_id", transactionId);
//            json.put("callback", "true");
//            json.put("success", "true");
//
//            System.out.println(json + "\n\n\n");
//
//            request.body(json.toJSONString());
//
//            Response responsePost = request.post(properties.getProperty("payment.gateway.payment.url"));
//            int code = responsePost.getStatusCode();
//            responsePost.getBody().print();
//            Assert.assertEquals(code, 200);
//
//
//            try {
//                TimeUnit.SECONDS.sleep(10);
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//
//            try {
//
//                String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//
//                transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//                System.out.println(transactionId);
//
//                String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//                String status = utils.getValueFromDB(conOrder, query2);
//                System.out.println(status);
//
//                assertEquals("FULFILLMENT_INITIATED", status);
//
//
//
//
//
//                if ("FULFILLMENT_INITIATED".equals(status)) {
//                    System.out.println("Payment fulfillment initiated ");
//
//                } else {
//                    System.out.println("Failure : create new order.");
//                }
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e);
//            return;
//        }
//
//    }
//
//
//    @Test(description = "Verify the order state when fulfillment is success")
//    public void AcfulfillmentUpdateTC1() {
//
//        String OrderUid;
//
//        try {
//
//            String queryOrderUid = "SELECT order_uid from nuclei_orders.orders where order_state=\"FULFILLMENT_INITIATED\" order by id desc limit 1";
//
//
//            OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//            System.out.println("OrderUid : " + OrderUid);
//
//            String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//
//            transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//            System.out.println("TransactionID : " + transactionId);
//
//
//            BizdirectCartMessages.PostFulfillmentRequest postFulfillmentRequest = BizdirectCartMessages.PostFulfillmentRequest
//                    .newBuilder()
//                    .setOrderUid(OrderUid)
//                    .setProviderId(1)
//                    .setDescription("Something")
//                    .setFulfillmentStatus(BizdirectCartMessages.FulfillmentStatus.FULFILLMENT_SUCCESS)
//                    .build();
//
//
//            BizdirectCartMessages.PostFulfillmentResponse postFulfillmentResponse;
//            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
//            postFulfillmentResponse = blockingStubCart.postFulfillmentCall(postFulfillmentRequest);
//            System.out.println(postFulfillmentResponse);
//            //System.out.println(postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get(""));
//
//
//            if (ResponseCode.SUCCESS.equals(postFulfillmentResponse.getEmpty().getResponse().getResponseCode())) {
//                String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//                String status = utils.getValueFromDB(conOrder, query2);
//                System.out.println(status);
//                assertEquals("ORDER_COMPLETED", status);
//
//            }
//
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e);
//        }
//    }
//
//
//    @Test(description = "Check order state after order created")
//    public void BacreateOrderAndCheckResponseAssertTC2() {
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
//        try {
//
//            String querrOrderstate = "SELECT order_state from nuclei_orders.orders where order_uid = '" + orderUid + "'";
//            String order_State = utils.getValueFromDB(conOrder, querrOrderstate);
//            System.out.println("Order state : " + order_State);
//
//            if (createOrderRedirectResponse.getResponseMap().get("transaction_id") == null) {
//                assertEquals(order_State, "PAYMENT_INITIATION_FAILED");
//
//                /* Again creating order using same cartId*/
//            } else {
//                assertEquals(order_State, "PAYMENT_INITIATED");
//                    /*
//                        call next test case
//                    */
//            }
//
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//
//    }
//
//
//    @Test(description = "Verify order state when payment is success")
//    public void BbinitiatePaymentRequestTC2() {
//
//        String OrderUid;
//        try {
//
//
//            String queryOrderUid = "SELECT order_uid from nuclei_orders.orders where order_state= \"PAYMENT_INITIATED\" order by id desc limit 1";
//
//            OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//            System.out.println(OrderUid);
//
//            String queryTransactionId = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//            transactionId = utils.getValueFromDB(conPayment, queryTransactionId);
//            System.out.println(transactionId);
//
//
//
//            RequestSpecification request1 = given();
//            request1.header("Content-Type", "application/json");
//            request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//
//            JsonHeader dto = new JsonHeader();
//            Map<String, String> payments_info = new HashMap<>();
//            payments_info.put("transaction_id", transactionId);
//            payments_info.put("order_uid", OrderUid);
//
//            dto.setRequestMap(payments_info);
//
//            Gson gson = new Gson();
//            String json1 = gson.toJson(dto);
//
//            System.out.println(json1);
//
//            request1.body(json1);
//
//            Response response1 = request1.post(properties.getProperty("payment.gateway.payload.url"));
//            int code1 = response1.getStatusCode();
//            response1.getBody().print();
//            Assert.assertEquals(code1, 200);
//
//            System.out.println("\n\n\n");
//
//
//
//
//            RequestSpecification request = given();
//            request.header("Content-Type", "application/json");
//            request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//            JSONObject json = new JSONObject();
//            json.put("transaction_id", transactionId);
//            json.put("callback", "true");
//            json.put("success", "true");
//
//            System.out.println(json + "\n\n\n");
//
//            request.body(json.toJSONString());
//
//            Response responsePost = request.post(properties.getProperty("payment.gateway.payment.url"));
//            int code = responsePost.getStatusCode();
//            responsePost.getBody().print();
//            Assert.assertEquals(code, 200);
//
//
//            try {
//                TimeUnit.SECONDS.sleep(10);
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//
//            try {
//
//                String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//
//                transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//                System.out.println(transactionId);
//
//                String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//                String status = utils.getValueFromDB(conOrder, query2);
//                System.out.println(status);
//
//                assertEquals("FULFILLMENT_INITIATED", status);
//
//                if ("FULFILLMENT_INITIATED".equals(status)) {
//                    System.out.println("Payment success ");
//
//                }
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e);
//
//        }
//
//    }
//
//
//    @Test(description = "Verify the order state when fulfillment Fails")
//    public void BcfulfillmentUpdateTC2() {
//
//        String OrderUid;
//
//        try {
//
//            String queryOrderUid = "SELECT order_uid from nuclei_orders.orders where order_state= \"FULFILLMENT_INITIATED\" order by id desc limit 1";
//
//            OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//            System.out.println(OrderUid);
//
//            String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//
//            transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//            System.out.println(transactionId);
//
//
//            BizdirectCartMessages.PostFulfillmentRequest postFulfillmentRequest = BizdirectCartMessages.PostFulfillmentRequest
//                    .newBuilder()
//                    .setOrderUid(OrderUid)
//                    .setProviderId(1)
//                    .setDescription("Something")
//                    .setFulfillmentStatus(BizdirectCartMessages.FulfillmentStatus.FULFILLMENT_FAILURE)
//                    .build();
//
//
//            BizdirectCartMessages.PostFulfillmentResponse postFulfillmentResponse;
//            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
//            postFulfillmentResponse = blockingStubCart.postFulfillmentCall(postFulfillmentRequest);
//            System.out.println(postFulfillmentResponse);
//
//
//            try {
//                TimeUnit.SECONDS.sleep(5);
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//
//            String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get("order_uid") + "'";
//
//            String statusOrder = utils.getValueFromDB(conOrder, query2);
//            System.out.println("Order : " + statusOrder);
//
//            assertEquals("REFUND_INITIATED", statusOrder);
//
//            String querryCartUid = "SELECT cart_uid from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//            String cartUid = utils.getValueFromDB(conOrder, querryCartUid);
//            System.out.println("cart uid " + cartUid);
//
//            String querryCartState = "SELECT state from nuclei_cart.cart_info where cart_uid = '" + cartUid + "'";
//            String cartState = utils.getValueFromDB(conCart, querryCartState);
//            System.out.println("cart : " + cartState);
//
//            assertEquals("FULFILLMENT_FAILED_REFUND_INITIATED", cartState);
//
//            if ("FULFILLMENT_FAILED_REFUND_INITIATED".equals(cartState)) {
//
//
//
//                RequestSpecification request = given();
//                request.header("Content-Type", "application/json");
//                request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//                JSONObject json = new JSONObject();
//                json.put("transaction_id", postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get("transaction_id"));
//                json.put("callback", "true");
//                json.put("success", "true");
//
//                System.out.println(json + "\n\n\n");
//
//                request.body(json.toJSONString());
//
//                Response responsePost = request.post(properties.getProperty("payment.gateway.payment.url"));
//                int code = responsePost.getStatusCode();
//                responsePost.getBody().print();
//                Assert.assertEquals(code, 200);
//
//
//                try {
//                    TimeUnit.SECONDS.sleep(10);
//                } catch (Exception e) {
//                    System.out.println(e);
//                }
//
//
//                try {
//
//
//                    String queryOrderstate = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//                    String OrderState = utils.getValueFromDB(conOrder, queryOrderstate);
//                    System.out.println("Order : " + OrderState);
//
//                    assertEquals("REFUND_COMPLETED", OrderState);
//
//
//                    String querryCartid = "SELECT cart_uid from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//                    String cartid = utils.getValueFromDB(conOrder, querryCartid);
//                    System.out.println(cartid);
//
//                    String querryCartstate = "SELECT state from nuclei_cart.cart_info where cart_uid = '" + cartUid + "'";
//                    String cartstate = utils.getValueFromDB(conCart, querryCartstate);
//                    System.out.println("Cart : " + cartstate);
//
//
//                    assertEquals("CART_FULL_REFUND_COMPLETED", cartstate);
//
//
//                } catch (Exception e) {
//                    System.out.println(e);
//                }
//            }
//
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e);
//        }
//    }
//
//
//    @Test(description = "Check order state after order created")
//    public void CacreateOrderAndCheckResponseAssertTC3() {
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
//        try {
//
//            String querrOrderstate = "SELECT order_state from nuclei_orders.orders where order_uid = '" + orderUid + "'";
//            String order_State = utils.getValueFromDB(conOrder, querrOrderstate);
//            System.out.println("Order state : " + order_State);
//
//            if (createOrderRedirectResponse.getResponseMap().get("transaction_id") == null) {
//                assertEquals(order_State, "PAYMENT_INITIATION_FAILED");
//
//                /* Again creating order using same cartId*/
//            } else {
//                assertEquals(order_State, "PAYMENT_INITIATED");
//                    /*
//                        call next test case
//                    */
//            }
//
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//
//    }
//
//
//    @Test(description = "Verify order state when payment is success")
//    public void CbinitiatePaymentRequestTC3() {
//
//        String OrderUid;
//        try {
//
//
//            String queryOrderUid = "SELECT order_uid from nuclei_orders.orders where order_state= \"PAYMENT_INITIATED\"order by id desc limit 1";
//
//            OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//            System.out.println(OrderUid);
//
//            String queryTransactionId = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//            transactionId = utils.getValueFromDB(conPayment, queryTransactionId);
//            System.out.println(transactionId);
//
//
//
//            RequestSpecification request1 = given();
//            request1.header("Content-Type", "application/json");
//            request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//
//            //System.out.println(request1);
//
//
//            JsonHeader dto = new JsonHeader();
//            Map<String, String> payments_info = new HashMap<>();
//            payments_info.put("transaction_id", transactionId);
//            payments_info.put("order_uid", OrderUid);
//
//            dto.setRequestMap(payments_info);
//
//            Gson gson = new Gson();
//            String json1 = gson.toJson(dto);
//
//            System.out.println(json1);
//
//            request1.body(json1);
//
//            Response response1 = request1.post(properties.getProperty("payment.gateway.payload.url"));
//            int code1 = response1.getStatusCode();
//            response1.getBody().print();
//            Assert.assertEquals(code1, 200);
//
//            System.out.println("\n\n\n");
//
//
//
//
//            RequestSpecification request = given();
//            request.header("Content-Type", "application/json");
//            request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//            JSONObject json = new JSONObject();
//            json.put("transaction_id", transactionId);
//            json.put("callback", "true");
//            json.put("success", "true");
//
//            System.out.println(json + "\n\n\n");
//
//            request.body(json.toJSONString());
//
//            Response responsePost = request.post(properties.getProperty("payment.gateway.payment.url"));
//            int code = responsePost.getStatusCode();
//            responsePost.getBody().print();
//            Assert.assertEquals(code, 200);
//
//
//            try {
//                TimeUnit.SECONDS.sleep(10);
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//
//            try {
//
//                String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//
//                transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//                System.out.println(transactionId);
//
//                String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//                String status = utils.getValueFromDB(conOrder, query2);
//                System.out.println(status);
//
//                assertEquals("FULFILLMENT_INITIATED", status);
//
//                if ("FULFILLMENT_INITIATED".equals(status)) {
//                    System.out.println("Payment success ");
//
//                }
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e);
//            return;
//        }
//
//    }
//
//
//    @Test(description = "Verify the order state when fulfillment is Fails")
//    public void CcfulfillmentUpdateTC3() {
//
//        String OrderUid;
//
//        try {
//
//            String queryOrderUid = "SELECT order_uid from nuclei_orders.orders where order_state= \"FULFILLMENT_INITIATED\" order by id desc limit 1";
//
//            OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//            System.out.println(OrderUid);
//
//            String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//
//            transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//            System.out.println(transactionId);
//
//
//            BizdirectCartMessages.PostFulfillmentRequest postFulfillmentRequest = BizdirectCartMessages.PostFulfillmentRequest
//                    .newBuilder()
//                    .setOrderUid(OrderUid)
//                    .setProviderId(1)
//                    .setDescription("Something")
//                    .setFulfillmentStatus(BizdirectCartMessages.FulfillmentStatus.FULFILLMENT_FAILURE)
//                    .build();
//
//
//            BizdirectCartMessages.PostFulfillmentResponse postFulfillmentResponse;
//            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
//            postFulfillmentResponse = blockingStubCart.postFulfillmentCall(postFulfillmentRequest);
//            System.out.println(postFulfillmentResponse);
//
//            try {
//                TimeUnit.SECONDS.sleep(5);
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//
//            String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//            String statusOrder = utils.getValueFromDB(conOrder, query2);
//            System.out.println(statusOrder);
//
//            assertEquals("REFUND_INITIATED", statusOrder);
//
//            String querryCartUid = "SELECT cart_uid from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//            String cartUid = utils.getValueFromDB(conOrder, querryCartUid);
//            //System.out.println(cartUid);
//
//            String querryCartState = "SELECT state from nuclei_cart.cart_info where cart_uid = '" + cartUid + "'";
//            String cartState = utils.getValueFromDB(conCart, querryCartState);
//            System.out.println(cartState);
//
//            assertEquals("FULFILLMENT_FAILED_REFUND_INITIATED", cartState);
//
//            if ("FULFILLMENT_FAILED_REFUND_INITIATED".equals(cartState)) {
//
//
//
//
//                RequestSpecification request = given();
//                request.header("Content-Type", "application/json");
//                request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//                JSONObject json = new JSONObject();
//                json.put("transaction_id", postFulfillmentResponse.getPostFulfillmentFailedResponse().getResponseMap().get("transaction_id"));
//                json.put("callback", "true");
//                json.put("success", "false");
//
//                System.out.println(json + "\n\n\n");
//
//                request.body(json.toJSONString());
//
//                Response responsePost = request.post(properties.getProperty("payment.gateway.payment.url"));
//                int code = responsePost.getStatusCode();
//                responsePost.getBody().print();
//                Assert.assertEquals(code, 200);
//
//
//                try {
//                    TimeUnit.SECONDS.sleep(10);
//                } catch (Exception e) {
//                    System.out.println(e);
//                }
//
//
//                try {
//
//
//                    String queryOrderstate = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//                    String OrderState = utils.getValueFromDB(conOrder, queryOrderstate);
//                    System.out.println("Order : " + OrderState);
//
//                    assertEquals("REFUND_FAILED", OrderState);
//
//
//                    String querryCartid = "SELECT cart_uid from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//                    String cartid = utils.getValueFromDB(conOrder, querryCartid);
//                    System.out.println(cartid);
//
//                    String querryCartstate = "SELECT state from nuclei_cart.cart_info where cart_uid = '" + cartUid + "'";
//                    String cartstate = utils.getValueFromDB(conCart, querryCartstate);
//                    System.out.println("Cart : " + cartstate);
//
//
//                    assertEquals("CART_FULL_REFUND_FAILED", cartstate);
//
//
//                } catch (Exception e) {
//                    System.out.println(e);
//                }
//            }
//
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e);
//            return;
//        }
//    }
//
//
//    @Test(description = "Check order state after order created")
//    public void DacreateOrderAndCheckResponseAssertTC4() {
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
//            String querryOrderstate = "SELECT order_state from nuclei_orders.orders where order_uid = '" + orderUid + "'";
//            String order_State = utils.getValueFromDB(conOrder, querryOrderstate);
//            System.out.println("Order state : " + order_State);
//
//            if (createOrderRedirectResponse.getResponseMap().get("transaction_id") == null) {
//                assertEquals(order_State, "PAYMENT_INITIATION_FAILED");
//
//                /* Again creating order using same cartId*/
//            } else {
//                assertEquals(order_State, "PAYMENT_INITIATED");
//            }
//
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//
//    }
//
//
//    /**
//     * Order will only initiate payment
//     */
//    @Test(description = "Verify order state when payment is failed")
//    public void DbinitiatePaymentRequestTC4() {
//
//        String OrderUid;
//        try {
//
//
//            String queryOrderUid = "SELECT order_uid from nuclei_orders.orders where order_state= \"PAYMENT_INITIATED\" order by id desc limit 1";
//
//
//            OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//            System.out.println(OrderUid);
//
//            String queryTransactionId = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//            transactionId = utils.getValueFromDB(conPayment, queryTransactionId);
//            System.out.println(transactionId);
//
//
//
//            RequestSpecification request1 = given();
//            request1.header("Content-Type", "application/json");
//            request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//
//            JsonHeader dto = new JsonHeader();
//            Map<String, String> payments_info = new HashMap<>();
//            payments_info.put("transaction_id", transactionId);
//            payments_info.put("order_uid", OrderUid);
//
//            dto.setRequestMap(payments_info);
//
//            Gson gson = new Gson();
//            String json1 = gson.toJson(dto);
//
//            System.out.println(json1);
//
//            request1.body(json1);
//
//            Response response1 = request1.post(properties.getProperty("payment.gateway.payload.url"));
//            int code1 = response1.getStatusCode();
//            response1.getBody().print();
//            Assert.assertEquals(code1, 200);
//
//            System.out.println("\n\n\n");
//
//
//
//
//            RequestSpecification request = given();
//            request.header("Content-Type", "application/json");
//            request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//            JSONObject json = new JSONObject();
//            json.put("transaction_id", transactionId);
//            json.put("callback", "true");
//            json.put("success", "false");
//
//            System.out.println(json + "\n\n\n");
//
//            request.body(json.toJSONString());
//
//            Response responsePost = request.post(properties.getProperty("payment.gateway.payment.url"));
//            int code = responsePost.getStatusCode();
//            responsePost.getBody().print();
//            Assert.assertEquals(code, 200);
//
//
//            try {
//                TimeUnit.SECONDS.sleep(10);
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//
//            try {
//
//                String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//                transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//                System.out.println(transactionId);
//
//                String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//                String status = utils.getValueFromDB(conOrder, query2);
//                System.out.println(status);
//
//                assertEquals("PAYMENT_FAILED", status);
//
//                if ("PAYMENT_FAILED".equals(status)) {
//                    System.out.println("Payment failed ");
//
//                } else {
//
//                    System.out.println("Failure : create order.");
//                }
//
//
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e);
//            return;
//        }
//
//    }
//
//}