////import com.google.gson.Gson;
////
////import java.util.HashMap;
////import java.util.Map;
////import java.util.concurrent.TimeUnit;
////
////public class trash {
////
////
////    else {
////
////
////        try {
////            TimeUnit.SECONDS.sleep(10);
////        } catch (Exception e) {
////            System.out.println(e);
////        }
////
////
////        String url1 = "http://payment-gateway.172.16.1.0.nip.io/api/payment-gateway/v1/mock/gateway/mobile/payload";
////
////        RequestSpecification request1 = given();
////        request1.header("Content-Type", "application/json");
////        request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
////
////
////        JsonHeader dto = new JsonHeader();
////        Map<String, String> payments_info = new HashMap<>();
////        payments_info.put("TRANSACTION_ID", transactionId);
////        payments_info.put("ORDER_UUID", OrderUid);
////
////        dto.setRequestMap(payments_info);
////
////        Gson gson = new Gson();
////        String json1 = gson.toJson(dto);
////
////        System.out.println(json1);
////
////        request1.body(json1);
////
////        Response response1 = request1.post(url1);
////        int code1 = response1.getStatusCode();
////        response1.getBody().print();
////        Assert.assertEquals(code1, 200);
////
////        System.out.println("\n\n\n");
////
////
////        String url = "http://payment-gateway.172.16.1.0.nip.io/api/payment-gateway/v1/mock/gateway/payment";
////
////
////        RequestSpecification request = given();
////        request.header("Content-Type", "application/json");
////        request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
////
////        JSONObject json = new JSONObject();
////        json.put("transaction_id", transactionId);
////        json.put("callback", "true");
////        json.put("success", "true");
////
////        System.out.println(json + "\n\n\n");
////
////        request.body(json.toJSONString());
////
////        Response responsePost = request.post(url);
////        int code = responsePost.getStatusCode();
////        responsePost.getBody().print();
////        Assert.assertEquals(code, 200);
////
////
////        try {
////            TimeUnit.SECONDS.sleep(10);
////        } catch (Exception e) {
////            System.out.println(e);
////        }
////
////
//////            BizdirectOrderMessages.FulfillmentSuccessRequest request = BizdirectOrderMessages.FulfillmentSuccessRequest.newBuilder()
//////                    .setOrderUid(OrderUid)
//////                    .setProviderId(1)
//////                    .build();
//////
//////
//////            BizdirectOrderMessages.FulfillmentUpdateRequest fulfillmentUpdateRequest = BizdirectOrderMessages.FulfillmentUpdateRequest
//////                    .newBuilder()
//////                    .setFulfillmentSuccessRequest(request)
//////                    .build();
//////
//////            BizdirectOrderMessages.FulfillmentUpdateResponse responseStatus;
//////
//////            blockingStubOrder = BizdirectOrderServiceGrpc.newBlockingStub(orderChannel);
//////            responseStatus = blockingStubOrder.fulfillmentUpdate(fulfillmentUpdateRequest);
//////
//////            System.out.println(responseStatus);
//////            //Assert.assertEquals(responseStatus.getEmpty().getResponse().getResponseCode(),"200");
//////            System.out.println(responseStatus.getEmpty().getResponse().getResponseCode());
////
////        try {
////
////
////            String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
////
////            String status = utils.getValueFromDB(conOrder, query2);
////            System.out.println(status);
////
////            //assertEquals(status , responseStatus.getEmpty().getResponse().getMessage());      // getting response message asa SUCCESS
////
////            if ("REFUND_SUCCESS".equals(emptyResponse.getResponse().getMessage())) {
////                System.out.println("Refund success");
////            }
////        } catch (Exception e) {
////            System.out.println(e);
////        }
////    }
////}
//
//
//@Test(description = "Payment callback=true success=true")
//public void initiatePaymentRequestTC3() {
//
//        String OrderUid;
//        try {
//
//
//        String queryOrderUid = "SELECT order_uid from nuclei_orders.orders  order by id desc limit 1";
//
//        OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//        System.out.println(OrderUid);
//
//        String queryTransactionId = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//        transactionId = utils.getValueFromDB(conPayment, queryTransactionId);
//        System.out.println(transactionId);
//
//
//        String url1 = "http://payment-gateway.172.16.1.0.nip.io/api/payment-gateway/v1/mock/gateway/mobile/payload";
//
//        RequestSpecification request1 = given();
//        request1.header("Content-Type", "application/json");
//        request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//
//        //System.out.println(request1);
//
//
//        JsonHeader dto = new JsonHeader();
//        Map<String, String> payments_info = new HashMap<>();
//        payments_info.put("TRANSACTION_ID", transactionId);
//        payments_info.put("ORDER_UUID", OrderUid);
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
//        json.put("transaction_id", transactionId);
//        json.put("callback", "true");
//        json.put("success", "true");
//
//        System.out.println(json + "\n\n\n");
//
//        request.body(json.toJSONString());
//
//        Response responsePost = request.post(url);
//        int code = responsePost.getStatusCode();
//        responsePost.getBody().print();
//        Assert.assertEquals(code, 200);
//
//
//        try {
//        TimeUnit.SECONDS.sleep(10);
//        } catch (Exception e) {
//        System.out.println(e);
//        }
//
//
//        try {
//
//        String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//
//        transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//        System.out.println(transactionId);
//
//        String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//        String status = utils.getValueFromDB(conOrder, query2);
//        System.out.println(status);
//
//        assertEquals("PAYMENT_SUCCESS", status);
//
//        if ("PAYMENT_SUCCESS".equals(status)) {
//        System.out.println("Payment success ");
//
//        }
//        } catch (Exception e) {
//        System.out.println(e);
//        }
//
//        } catch (Exception e) {
//        logger.log(Level.WARNING, "RPC failed: {0}", e);
//        return;
//        }
//
//        }
//
//
///**
// * 200 status code status
// * otherwise call refund
// */
//
//
//@Test(description = "Post fulfillment fails Refund callback=true success=false.... refund fails")
//public void fulfillmentUpdateTC3() {
//
//        String OrderUid;
//
//        try {
//
//        String queryOrderUid = "SELECT order_uid from nuclei_orders.orders order by id desc limit 1";
//
//        OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//        System.out.println(OrderUid);
//
//        String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//
//        transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//        System.out.println(transactionId);
//
//
//        BizdirectCartMessages.PostFulfillmentRequest postFulfillmentRequest = BizdirectCartMessages.PostFulfillmentRequest
//        .newBuilder()
//        .setOrderUid(OrderUid)
//        .setProviderId(1)
//        .setDescription("Something")
//        .setFulfillmentStatusValue(0)
//        .build();
//
//
//        BizdirectCartMessages.Empty emptyResponse;
//        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
//        emptyResponse = blockingStubCart.postFulfillmentCall(postFulfillmentRequest);
//        System.out.println(emptyResponse);
//
//
//        if ("FULFILLMENT_SUCCESS".equals(emptyResponse.getResponse().getMessage())) {
//        String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//        String status = utils.getValueFromDB(conOrder, query2);
//        System.out.println(status);
//        assertEquals("ORDER_CREATED", status);
//
//        } else {
//
//
//        try {
//        TimeUnit.SECONDS.sleep(20);
//        } catch (Exception e) {
//        System.out.println(e);
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
//        payments_info.put("TRANSACTION_ID", transactionId);
//        payments_info.put("ORDER_UUID", OrderUid);
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
//        json.put("transaction_id", transactionId);
//        json.put("callback", "true");
//        json.put("success", "false");
//
//        System.out.println(json + "\n\n\n");
//
//        request.body(json.toJSONString());
//
//        Response responsePost = request.post(url);
//        int code = responsePost.getStatusCode();
//        responsePost.getBody().print();
//        Assert.assertEquals(code, 200);
//
//
//        try {
//        TimeUnit.SECONDS.sleep(10);
//        } catch (Exception e) {
//        System.out.println(e);
//        }
//
//
//
//        try {
//
//
//        String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//        String status = utils.getValueFromDB(conOrder, query2);
//        System.out.println(status);
//
//        //assertEquals(status , responseStatus.getEmpty().getResponse().getMessage());      // getting response message asa SUCCESS
//
//        if ("REFUND_FAILED".equals(emptyResponse.getResponse().getMessage())) {
//        System.out.println("Refund fails");
//        }
//        } catch (Exception e) {
//        System.out.println(e);
//        }
//        }
//
//        } catch (Exception e) {
//        logger.log(Level.WARNING, "RPC failed: {0}", e);
//        return;
//        }
//        }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//@Test(description = "Check order created or not depends on the transaction id")
//public void createOrderAndCheckResponseAssertTC4() {
//
//
//        BizdirectCartMessages.UserIdentifier userIdentifier = BizdirectCartMessages.UserIdentifier
//        .newBuilder()
//        .setDeviceId("de123")
//        .setPartnerId(12)
//        .setUserUid("U" + utils.randomAlphaNumeric(5))
//        .build();
//
//        BizdirectCartMessages.CreateCartRequest createCartRequest = BizdirectCartMessages.CreateCartRequest
//        .newBuilder()
//        .setUserIdentifier(userIdentifier)
//        .setLatitude(1.2323)
//        .setLongitude(1.57767)
//        .setProviderId(1)
//        .setCategoryId(1)
//        .build();
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
//        .newBuilder()
//        .setPrice(1000)
//        .setDiscount(5)
//        .setTax(2)
//        .setCurrencyCode("INR")
//        .build();
//
//        BizdirectCartMessages.AddItemRequest cartItem = BizdirectCartMessages.AddItemRequest
//        .newBuilder()
//        .setCartUid(createCartResponse.getCartUid())
//        .setDeviceId("1")
//        .setItemPriceDetails(priceDetails)
//        .build();
//
//        BizdirectCartMessages.AddItemResponse itemResponse;
//
//        itemResponse = blockingStubCart.addItem(cartItem);
//        System.out.println(itemResponse);
//
//
////
////        BizdirectCartMessages.CreateOrderRedirectRequest redirectRequest = BizdirectCartMessages.CreateOrderRedirectRequest
////                .newBuilder()
////                .setCartUid(createCartResponse.getCartUid())
////                .build();
////
////
////        BizdirectCartMessages.CreateOrderRedirectResponse createOrderRedirectResponse;
////
////
////        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
////        createOrderRedirectResponse = blockingStubCart.createOrderRequest(redirectRequest);
////        System.out.println(createOrderRedirectResponse.getResponseMap().get("TRANSACTION_ID"));
////        System.out.println(createOrderRedirectResponse);
//
//
//        BizdirectOrderMessages.ItemPrice itemPrice = BizdirectOrderMessages.ItemPrice
//        .newBuilder()
//        .setMPrice(10.0)
//        .setMDiscount(5.0)
//        .setMTax(2.0)
//        .setCurrencyType("INR")
//        .build();
//
//        BizdirectOrderMessages.CreateOrUpdateOrderItem createOrUpdateOrderItem = BizdirectOrderMessages.CreateOrUpdateOrderItem
//        .newBuilder()
//        .setItemId("Item" + utils.randomAlphaNumeric(2))
//        .addItemValue(itemPrice)
//        .build();
//
//
//        List<BizdirectOrderMessages.CreateOrUpdateOrderItem> CreateOrUpdateOrderItemList = new ArrayList<>();
//        CreateOrUpdateOrderItemList.add(createOrUpdateOrderItem);
//
//        BizdirectOrderMessages.CreateOrUpdateOrderRequest requestcreateOrUpdateOrder = BizdirectOrderMessages.CreateOrUpdateOrderRequest.newBuilder()
//        .setDeviceId("D" + utils.randomAlphaNumeric(2))
//        .setPartnerId(1)
//        .setLatitude(1.2323)
//        .setLongitude(1.57767)
//        .setCartUid(createCartResponse.getCartUid())
//        .setProviderId(1)
//        .setCategoryId(1)
//        .setCustomerUid(utils.randomAlphaNumeric(3))
//        .setMTotalAmount(12.0)
//        .setMTotalDiscount(2.0)
//        .setMTotalTax(1.5)
//        .setCurrencyType("INR")
//        .setIsPaymentRequired(true)
//        .addAllItems(CreateOrUpdateOrderItemList)
//        .build();/* For list of items*/
//
//
//        BizdirectOrderMessages.CreateOrUpdateOrderResponse responseOrder;
//
//        blockingStubOrder = BizdirectOrderServiceGrpc.newBlockingStub(orderChannel);
//        responseOrder = blockingStubOrder.createOrUpdateOrder(requestcreateOrUpdateOrder);
//        // System.out.println(response.getResponseStatus());
//        orderUid = responseOrder.getOrderUid();
//        transactionId = responseOrder.getResponseMap().get("TRANSACTION_ID");
//        System.out.println(responseOrder);
//        System.out.println(transactionId);
//
//        try {
//
////                String query1 = "SELECT cart_uid from nuclei_orders.orders where order_uid ='"+response.getOrderUid()+"'";
////                String cart_uid= utils.getValueFromDB(con,query1);
////                System.out.println( "Cart uid : "+cart_uid);
//
//
//        String querrOrderstate = "SELECT order_state from nuclei_orders.orders where order_uid = '" + orderUid + "'";
//        String order_State = utils.getValueFromDB(conOrder, querrOrderstate);
//        System.out.println("Order state : " + order_State);
//
//        if (responseOrder.getResponseMap().get("TRANSACTION_ID") == null) {
//        assertEquals(order_State, "PAYMENT_INITIATION_FAILED");
//
//        /* Again creating order using same cartId*/
//        createOrderAndCheckResponseAssertTC4();
//        } else {
//        //assertEquals(order_State, "PAYMENT_INITIATED");
//                    /*
//                        call next test case
//                    */
//        }
//
//
//        } catch (Exception e) {
//        System.out.println(e);
//        }
//
//
//        }
//
//
//@Test
//public void initiatePaymentRequestTC4() {
//
//        String OrderUid;
//        try {
//
//
//        String queryOrderUid = "SELECT order_uid from nuclei_orders.orders  order by id desc limit 1";
//
//        OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//        System.out.println(OrderUid);
//
//        String queryTransactionId = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//        transactionId = utils.getValueFromDB(conPayment, queryTransactionId);
//        System.out.println(transactionId);
//
//
//        String url1 = "http://payment-gateway.172.16.1.0.nip.io/api/payment-gateway/v1/mock/gateway/mobile/payload";
//
//        RequestSpecification request1 = given();
//        request1.header("Content-Type", "application/json");
//        request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
//
//
//        //System.out.println(request1);
//
//
//        JsonHeader dto = new JsonHeader();
//        Map<String, String> payments_info = new HashMap<>();
//        payments_info.put("TRANSACTION_ID", transactionId);
//        payments_info.put("ORDER_UUID", OrderUid);
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
//        json.put("transaction_id", transactionId);
//        json.put("callback", "true");
//        json.put("success", "true");
//
//        System.out.println(json + "\n\n\n");
//
//        request.body(json.toJSONString());
//
//        Response responsePost = request.post(url);
//        int code = responsePost.getStatusCode();
//        responsePost.getBody().print();
//        Assert.assertEquals(code, 200);
//
//
//        try {
//        TimeUnit.SECONDS.sleep(10);
//        } catch (Exception e) {
//        System.out.println(e);
//        }
//
//
//        try {
//
//        String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//
//        transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//        System.out.println(transactionId);
//
//        String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//        String status = utils.getValueFromDB(conOrder, query2);
//        System.out.println(status);
//
//        assertEquals("PAYMENT_FAILED", status);
//
//        if ("PAYMENT_SUCCESS".equals(status)) {
//        System.out.println("Payment success ");
//
//        }
//        } catch (Exception e) {
//        System.out.println(e);
//        }
//
//        } catch (Exception e) {
//        logger.log(Level.WARNING, "RPC failed: {0}", e);
//        return;
//        }
//
//        }
//
//
///**
// * 200 status code status
// * otherwise call refund
// */
//
//
//@Test(description = "Fulfillment success, order created")
//public void fulfillmentUpdateTC4() {
//
//        String OrderUid;
//
//        try {
//
//        String queryOrderUid = "SELECT order_uid from nuclei_orders.orders order by id desc limit 1";
//
//        OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
//        System.out.println(OrderUid);
//
//        String queryTransaction = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";
//
//
//        transactionId = utils.getValueFromDB(conPayment, queryTransaction);
//        System.out.println(transactionId);
//
//
//        BizdirectCartMessages.PostFulfillmentRequest postFulfillmentRequest = BizdirectCartMessages.PostFulfillmentRequest
//        .newBuilder()
//        .setOrderUid(OrderUid)
//        .setProviderId(1)
//        .setDescription("Something")
//        .setFulfillmentStatusValue(1)
//        .build();
//
//
//        BizdirectCartMessages.Empty emptyResponse;
//        blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
//        emptyResponse = blockingStubCart.postFulfillmentCall(postFulfillmentRequest);
//        System.out.println(emptyResponse);
//
//
//        if ("FULFILLMENT_SUCCESS".equals(emptyResponse.getResponse().getMessage())) {
//        String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
//
//        String status = utils.getValueFromDB(conOrder, query2);
//        System.out.println(status);
//        assertEquals("ORDER_CREATED", status);
//
//        }
////            else {
////
////
////                try {
////                    TimeUnit.SECONDS.sleep(20);
////                } catch (Exception e) {
////                    System.out.println(e);
////                }
////
////
////                String url1 = "http://payment-gateway.172.16.1.0.nip.io/api/payment-gateway/v1/mock/gateway/mobile/payload";
////
////                RequestSpecification request1 = given();
////                request1.header("Content-Type", "application/json");
////                request1.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
////
////
////                JsonHeader dto = new JsonHeader();
////                Map<String, String> payments_info = new HashMap<>();
////                payments_info.put("TRANSACTION_ID", transactionId);
////                payments_info.put("ORDER_UUID", OrderUid);
////
////                dto.setRequestMap(payments_info);
////
////                Gson gson = new Gson();
////                String json1 = gson.toJson(dto);
////
////                System.out.println(json1);
////
////                request1.body(json1);
////
////                Response response1 = request1.post(url1);
////                int code1 = response1.getStatusCode();
////                response1.getBody().print();
////                Assert.assertEquals(code1, 200);
////
////                System.out.println("\n\n\n");
////
////
////                String url = "http://payment-gateway.172.16.1.0.nip.io/api/payment-gateway/v1/mock/gateway/payment";
////
////
////                RequestSpecification request = given();
////                request.header("Content-Type", "application/json");
////                request.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoic3RyaW5nIiwic3ViIjoiVVNFUiIsImV4cCI6MTU1NDUzNjE1OH0.yvUCXBYQSxZzrWKYVrr5PYLjf6do7y2GvNBGD8FJayWAxvPW5Ki_zVzSvPNEn2Toyy6BjzjG1DGFIu8Ua84hkQ");
////
////                JSONObject json = new JSONObject();
////                json.put("transaction_id", transactionId);
////                json.put("callback", "true");
////                json.put("success", "false");
////
////                System.out.println(json + "\n\n\n");
////
////                request.body(json.toJSONString());
////
////                Response responsePost = request.post(url);
////                int code = responsePost.getStatusCode();
////                responsePost.getBody().print();
////                Assert.assertEquals(code, 200);
////
////
////                try {
////                    TimeUnit.SECONDS.sleep(10);
////                } catch (Exception e) {
////                    System.out.println(e);
////                }
////
////
////
////                try {
////
////
////                    String query2 = "SELECT order_state from nuclei_orders.orders where order_uid = '" + OrderUid + "'";
////
////                    String status = utils.getValueFromDB(conOrder, query2);
////                    System.out.println(status);
////
////                    //assertEquals(status , responseStatus.getEmpty().getResponse().getMessage());      // getting response message asa SUCCESS
////
////                    if ("REFUND_FAILED".equals(emptyResponse.getResponse().getMessage())) {
////                        System.out.println("Refund failed");
////                    }
////                } catch (Exception e) {
////                    System.out.println(e);
////                }
////            }
//
//        } catch (Exception e) {
//        logger.log(Level.WARNING, "RPC failed: {0}", e);
//        return;
//        }
//        }
//
//
//










//
//        BizdirectOrderMessages.ItemPrice itemPrice = BizdirectOrderMessages.ItemPrice
//                .newBuilder()
//                .setMPrice(10.0)
//                .setMDiscount(5.0)
//                .setMTax(2.0)
//                .setCurrencyType("INR")
//                .build();
//
//        BizdirectOrderMessages.CreateOrUpdateOrderItem createOrUpdateOrderItem = BizdirectOrderMessages.CreateOrUpdateOrderItem
//                .newBuilder()
//                .setItemId("Item" + utils.randomAlphaNumeric(2))
//                .addItemValue(itemPrice)
//                .build();


//        List<BizdirectOrderMessages.CreateOrUpdateOrderItem> CreateOrUpdateOrderItemList = new ArrayList<>();
//        CreateOrUpdateOrderItemList.add(createOrUpdateOrderItem);
//
//        BizdirectOrderMessages.CreateOrUpdateOrderRequest requestcreateOrUpdateOrder = BizdirectOrderMessages.CreateOrUpdateOrderRequest.newBuilder()
//                .setDeviceId("D" + utils.randomAlphaNumeric(2))
//                .setPartnerId(1)
//                .setLatitude(1.2323)
//                .setLongitude(1.57767)
//                .setCartUid(createCartResponse.getCartUid())
//                .setProviderId(1)
//                .setCategoryId(1)
//                .setCustomerUid(utils.randomAlphaNumeric(3))
//                .setMTotalAmount(12.0)
//                .setMTotalDiscount(2.0)
//                .setMTotalTax(1.5)
//                .setCurrencyType("INR")
//                .setIsPaymentRequired(true)
//                .addAllItems(CreateOrUpdateOrderItemList)
//                .build();/* For list of items*/
//

//        BizdirectOrderMessages.CreateOrUpdateOrderResponse responseOrder;
//
//        blockingStubOrder = BizdirectOrderServiceGrpc.newBlockingStub(orderChannel);
//        responseOrder = blockingStubOrder.createOrUpdateOrder(requestcreateOrUpdateOrder);
//        orderUid = responseOrder.getOrderUid();
//        transactionId = responseOrder.getResponseMap().get("TRANSACTION_ID");
//        System.out.println(responseOrder);
//        System.out.println(transactionId);

