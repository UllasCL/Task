package com.bizdirect.orders.proto.messages;

import com.bizdirect.proto.messages.BizdirectCartMessages;
import com.bizdirect.proto.messages.ResponseCode;
import com.bizdirect.proto.services.BizdirectCartServiceGrpc;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;


public class BackButtonClickEvent extends BaseTest {

    private static final Logger logger = Logger.getLogger(BackButtonClickEvent.class.getName());
    private Utils utils = new Utils();
    private BizdirectCartServiceGrpc.BizdirectCartServiceBlockingStub blockingStubCart;

    @Test(description = "verify order state in DB,when payment Initiated and back button pressed with valid data")
    public void backButtonPressTC1() {



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

        System.out.println("\ncartUid  :" + createCartResponse.getCartUid());


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
        System.out.println("Transaction Id : " + createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        //System.out.println(createOrderRedirectResponse.getResponseMap());
        System.out.println("Order uid : " + createOrderRedirectResponse.getOrderUid());


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {


            String queryP = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMap().get("transaction_id") + "'";

            String statusPR = utils.getValueFromDB(conPayment, queryP);
            System.out.println("Payment : " + statusPR);


            String queryO = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + createOrderRedirectResponse.getOrderUid() + "'";

            String statusOR = utils.getValueFromDB(conOrder, queryO);
            System.out.println("Order : " + statusOR);


            String queryC = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusCR = utils.getValueFromDB(conCart, queryC);
            System.out.println("Cart : " + statusCR);


            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            String OrderUid, TransactionId;

            String queryOrderUid = "SELECT order_uid from nuclei_orders.orders order by id desc limit 1";

            OrderUid = utils.getValueFromDB(conOrder, queryOrderUid);
            System.out.println("OrderId : " + OrderUid);


            String queryTransactionId = "SELECT transaction_id from nuclei_payments.payment_refund_state where order_uuid ='" + OrderUid + "'";

            TransactionId = utils.getValueFromDB(conPayment, queryTransactionId);
            System.out.println("TransactionId : " + TransactionId);


            BizdirectCartMessages.PaymentManuallyDeclinedRequest paymentManuallyDeclinedRequest = BizdirectCartMessages.PaymentManuallyDeclinedRequest
                    .newBuilder()
                    .putPayload("order_uid", createOrderRedirectResponse.getOrderUid())
                    .putPayload("transaction_id", createOrderRedirectResponse.getResponseMap().get("transaction_id"))
                    .build();

            BizdirectCartMessages.Empty emptyResponse;


            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
            emptyResponse = blockingStubCart.paymentManuallyDeclined(paymentManuallyDeclinedRequest);
            System.out.println(emptyResponse);

            //Assert.assertEquals(ResponseCode.SUCCESS,emptyResponse.getResponse().getResponseCode() );


            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            String queryPR = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String stateP = utils.getValueFromDB(conPayment, queryPR);
            System.out.println("Payment : " + stateP);


            assertEquals("FAILED", stateP);


            String queryOR = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + createOrderRedirectResponse.getOrderUid() + "'";

            String stateO = utils.getValueFromDB(conOrder, queryOR);
            System.out.println("Order : " + stateO);


            assertEquals("PAYMENT_FAILED", stateO);

            String queryCR = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String stateC = utils.getValueFromDB(conCart, queryCR);
            System.out.println("cart : " + stateC);


            assertEquals("PAYMENT_FAILED", stateC);


        } catch (Exception e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e);
            return;
        }


    }


    @Test(description = "verify order state in DB,when payment Initiated and back button pressed with valid transactionId invalid orderId")
    public void backButtonPressTC2() {


        String orderUid;

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
        System.out.println(createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        System.out.println(createOrderRedirectResponse.getResponseMap());
        orderUid = createOrderRedirectResponse.getOrderUid();
        System.out.println("Order Uuid : " + orderUid);


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {


            String queryP = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMap().get("transaction_id") + "'";

            String statusPR = utils.getValueFromDB(conPayment, queryP);
            System.out.println("Payment : " + statusPR);


            String queryO = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + createOrderRedirectResponse.getOrderUid() + "'";

            String statusOR = utils.getValueFromDB(conOrder, queryO);
            System.out.println("Order : " + statusOR);


            String queryC = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusCR = utils.getValueFromDB(conCart, queryC);
            System.out.println("Cart : " + statusCR);

            System.out.println("\n\n\n");


            BizdirectCartMessages.PaymentManuallyDeclinedRequest paymentManuallyDeclinedRequest = BizdirectCartMessages.PaymentManuallyDeclinedRequest
                    .newBuilder()
                    .putPayload("order_uid", utils.randomAlphaNumeric(5))
                    .putPayload("transaction_id", createOrderRedirectResponse.getResponseMap().get("transaction_id"))
                    .build();

            BizdirectCartMessages.Empty emptyResponse;


            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
            emptyResponse = blockingStubCart.paymentManuallyDeclined(paymentManuallyDeclinedRequest);
            System.out.println(emptyResponse);

            Assert.assertEquals(ResponseCode.NOT_FOUND, emptyResponse.getResponse().getResponseCode());


            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            String queryPR = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String stateP = utils.getValueFromDB(conPayment, queryPR);
            System.out.println("Payment : " + stateP);


            assertEquals("IN_PROCESS", stateP);


            String queryOR = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";

            String stateO = utils.getValueFromDB(conOrder, queryOR);
            System.out.println("Order : " + stateO);


            assertEquals("PAYMENT_INITIATED", stateO);

            String queryCR = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String stateC = utils.getValueFromDB(conCart, queryCR);
            System.out.println("Cart : " + stateC);


            assertEquals("PAYMENT_INITIATED", stateC);


        } catch (Exception e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e);
            return;
        }


    }


    @Test(description = "verify order state in DB,when payment Initiated and back button pressed with valid transactionId empty orderId")
    public void backButtonPressTC3() {


        String orderUid;

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
        System.out.println(createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        System.out.println(createOrderRedirectResponse.getResponseMap());
        orderUid = createOrderRedirectResponse.getOrderUid();
        System.out.println("Order Uuid : " + orderUid);


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {


            String queryP = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMap().get("transaction_id") + "'";

            String statusPR = utils.getValueFromDB(conPayment, queryP);
            System.out.println("Payment : " + statusPR);


            String queryO = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + createOrderRedirectResponse.getOrderUid() + "'";

            String statusOR = utils.getValueFromDB(conOrder, queryO);
            System.out.println("Order : " + statusOR);


            String queryC = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusCR = utils.getValueFromDB(conCart, queryC);
            System.out.println("Cart : " + statusCR);

            System.out.println("\n\n\n");


            BizdirectCartMessages.PaymentManuallyDeclinedRequest paymentManuallyDeclinedRequest = BizdirectCartMessages.PaymentManuallyDeclinedRequest
                    .newBuilder()
                    .putPayload("order_uid", "")
                    .putPayload("transaction_id", createOrderRedirectResponse.getResponseMapMap().get("transaction_id"))
                    .build();

            BizdirectCartMessages.Empty emptyResponse;


            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
            emptyResponse = blockingStubCart.paymentManuallyDeclined(paymentManuallyDeclinedRequest);
            System.out.println(emptyResponse);

            Assert.assertEquals(ResponseCode.NOT_FOUND, emptyResponse.getResponse().getResponseCode());


            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            String queryPR = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String stateP = utils.getValueFromDB(conPayment, queryPR);
            System.out.println("Payment : " + stateP);


            assertEquals("IN_PROCESS", stateP);


            String queryOR = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";

            String stateO = utils.getValueFromDB(conOrder, queryOR);
            System.out.println("Order : " + stateO);


            assertEquals("PAYMENT_INITIATED", stateO);

            String queryCR = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String stateC = utils.getValueFromDB(conCart, queryCR);
            System.out.println("Cart : " + stateC);


            assertEquals("PAYMENT_INITIATED", stateC);


        } catch (Exception e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e);
            return;
        }

    }


    @Test(description = "verify order state in DB,when payment Initiated and back button pressed with valid transactionId and no order Id")
    public void backButtonPressTC4() {


        String orderUid;

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
        System.out.println(createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        System.out.println(createOrderRedirectResponse.getResponseMap());
        orderUid = createOrderRedirectResponse.getOrderUid();
        System.out.println("Order Uuid : " + orderUid);


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {


            String queryP = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMap().get("transaction_id") + "'";

            String statusPR = utils.getValueFromDB(conPayment, queryP);
            System.out.println("Payment : " + statusPR);


            String queryO = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + createOrderRedirectResponse.getOrderUid() + "'";

            String statusOR = utils.getValueFromDB(conOrder, queryO);
            System.out.println("Order : " + statusOR);


            String queryC = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusCR = utils.getValueFromDB(conCart, queryC);
            System.out.println("Cart : " + statusCR);

            System.out.println("\n\n\n");

            BizdirectCartMessages.PaymentManuallyDeclinedRequest paymentManuallyDeclinedRequest = BizdirectCartMessages.PaymentManuallyDeclinedRequest
                    .newBuilder()
                    .putPayload("transaction_id", createOrderRedirectResponse.getResponseMapMap().get("transaction_id"))
                    .build();

            BizdirectCartMessages.Empty emptyResponse;


            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
            emptyResponse = blockingStubCart.paymentManuallyDeclined(paymentManuallyDeclinedRequest);
            System.out.println(emptyResponse);

//            Assert.assertEquals(ResponseCode.NOT_FOUND, emptyResponse.getResponse().getResponseCode());


            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }

            String queryPR = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String stateP = utils.getValueFromDB(conPayment, queryPR);
            System.out.println("Payment : " + stateP);


            assertEquals("IN_PROCESS", stateP);


            String queryOR = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";

            String stateO = utils.getValueFromDB(conOrder, queryOR);
            System.out.println("Order : " + stateO);


            assertEquals("PAYMENT_INITIATED", stateO);

            String queryCR = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String stateC = utils.getValueFromDB(conCart, queryCR);
            System.out.println("Cart : " + stateC);


            assertEquals("PAYMENT_INITIATED", stateC);


        } catch (Exception e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e);
            return;
        }

    }


    @Test(description = "verify order state in DB,when payment Initiated and back button pressed with valid OrderId , invalid transactionId")
    public void backButtonPressTC5() {


        String orderUid;

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
        System.out.println(createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        System.out.println(createOrderRedirectResponse.getResponseMap());
        orderUid = createOrderRedirectResponse.getOrderUid();
        System.out.println("Order Uuid : " + orderUid);


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {


            String queryP = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMap().get("transaction_id") + "'";

            String statusPR = utils.getValueFromDB(conPayment, queryP);
            System.out.println("Payment : " + statusPR);


            String queryO = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + createOrderRedirectResponse.getOrderUid() + "'";

            String statusOR = utils.getValueFromDB(conOrder, queryO);
            System.out.println("Order : " + statusOR);


            String queryC = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusCR = utils.getValueFromDB(conCart, queryC);
            System.out.println("Cart : " + statusCR);

            System.out.println("\n\n\n");

            BizdirectCartMessages.PaymentManuallyDeclinedRequest paymentManuallyDeclinedRequest = BizdirectCartMessages.PaymentManuallyDeclinedRequest
                    .newBuilder()
                    .putPayload("order_uid", createOrderRedirectResponse.getOrderUid())
                    .putPayload("transaction_id", utils.randomAlphaNumeric(5))
                    .build();

            BizdirectCartMessages.Empty emptyResponse;


            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
            emptyResponse = blockingStubCart.paymentManuallyDeclined(paymentManuallyDeclinedRequest);
            System.out.println(emptyResponse);

//            Assert.assertEquals(ResponseCode.NOT_FOUND, emptyResponse.getResponse().getResponseCode());


            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            String queryPR = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String stateP = utils.getValueFromDB(conPayment, queryPR);
            System.out.println("Payment : " + stateP);


            assertEquals("IN_PROCESS", stateP);


            String queryOR = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";

            String stateO = utils.getValueFromDB(conOrder, queryOR);
            System.out.println("Order : " + stateO);


            assertEquals("PAYMENT_INITIATED", stateO);

            String queryCR = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String stateC = utils.getValueFromDB(conCart, queryCR);
            System.out.println("Cart : " + stateC);


            assertEquals("PAYMENT_INITIATED", stateC);


        } catch (Exception e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e);
            return;
        }

    }


    @Test(description = "verify order state in DB,when payment Initiated and back button pressed empty TransactionId, valid orderId")
    public void backButtonPressTC6() {


        String orderUid;

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
        System.out.println(createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        System.out.println(createOrderRedirectResponse.getResponseMap());
        orderUid = createOrderRedirectResponse.getOrderUid();
        System.out.println("Order Uuid : " + orderUid);


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {

            String queryP = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMap().get("transaction_id") + "'";

            String statusPR = utils.getValueFromDB(conPayment, queryP);
            System.out.println("Payment : " + statusPR);


            String queryO = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + createOrderRedirectResponse.getOrderUid() + "'";

            String statusOR = utils.getValueFromDB(conOrder, queryO);
            System.out.println("Order : " + statusOR);


            String queryC = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusCR = utils.getValueFromDB(conCart, queryC);
            System.out.println("Cart : " + statusCR);

            System.out.println("\n\n\n");

            BizdirectCartMessages.PaymentManuallyDeclinedRequest paymentManuallyDeclinedRequest = BizdirectCartMessages.PaymentManuallyDeclinedRequest
                    .newBuilder()
                    .putPayload("order_uid", createOrderRedirectResponse.getOrderUid())
                    .putPayload("transaction_id", "")
                    .build();

            BizdirectCartMessages.Empty emptyResponse;


            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
            emptyResponse = blockingStubCart.paymentManuallyDeclined(paymentManuallyDeclinedRequest);
            System.out.println(emptyResponse);

//            Assert.assertEquals(ResponseCode.NOT_FOUND, emptyResponse.getResponse().getResponseCode());

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            String queryPR = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String stateP = utils.getValueFromDB(conPayment, queryPR);
            System.out.println("Payment : " + stateP);


            assertEquals("IN_PROCESS", stateP);


            String queryOR = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";

            String stateO = utils.getValueFromDB(conOrder, queryOR);
            System.out.println("Order : " + stateO);


            assertEquals("PAYMENT_INITIATED", stateO);

            String queryCR = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String stateC = utils.getValueFromDB(conCart, queryCR);
            System.out.println("Cart : " + stateC);


            assertEquals("PAYMENT_INITIATED", stateC);


        } catch (Exception e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e);
            return;
        }

    }


    @Test(description = "verify order state in DB,when payment Initiated and back button pressed valid OrderId and no transactionId")
    public void backButtonPressTC7() {


        String orderUid;

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
        System.out.println(createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        System.out.println(createOrderRedirectResponse.getResponseMap());
        orderUid = createOrderRedirectResponse.getOrderUid();
        System.out.println("Order Uuid : " + orderUid);


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {


            String queryP = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMap().get("transaction_id") + "'";

            String statusPR = utils.getValueFromDB(conPayment, queryP);
            System.out.println("Payment : " + statusPR);


            String queryO = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + createOrderRedirectResponse.getOrderUid() + "'";

            String statusOR = utils.getValueFromDB(conOrder, queryO);
            System.out.println("Order : " + statusOR);


            String queryC = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusCR = utils.getValueFromDB(conCart, queryC);
            System.out.println("Cart : " + statusCR);

            System.out.println("\n\n\n");


            BizdirectCartMessages.PaymentManuallyDeclinedRequest paymentManuallyDeclinedRequest = BizdirectCartMessages.PaymentManuallyDeclinedRequest
                    .newBuilder()
                    .putPayload("order_uid", createOrderRedirectResponse.getOrderUid())
                    .build();

            BizdirectCartMessages.Empty emptyResponse;


            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
            emptyResponse = blockingStubCart.paymentManuallyDeclined(paymentManuallyDeclinedRequest);
            System.out.println(emptyResponse);

//            Assert.assertEquals(ResponseCode.NOT_FOUND, emptyResponse.getResponse().getResponseCode());

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            String queryPR = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String stateP = utils.getValueFromDB(conPayment, queryPR);
            System.out.println("Payment : " + stateP);


            assertEquals("IN_PROCESS", stateP);


            String queryOR = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";

            String stateO = utils.getValueFromDB(conOrder, queryOR);
            System.out.println("Order : " + stateO);


            assertEquals("PAYMENT_INITIATED", stateO);

            String queryCR = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String stateC = utils.getValueFromDB(conCart, queryCR);
            System.out.println("Cart : " + stateC);


            assertEquals("PAYMENT_INITIATED", stateC);


        } catch (Exception e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e);
            return;
        }

    }

    @Test(description = "verify order state in DB,when payment Initiated and back button pressed without OrderId and transactionId")
    public void backButtonPressTC8() {


        String orderUid;

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
        System.out.println(createOrderRedirectResponse.getResponseMap().get("transaction_id"));
        System.out.println(createOrderRedirectResponse.getResponseMap());
        orderUid = createOrderRedirectResponse.getOrderUid();
        System.out.println("Order Uuid : " + orderUid);


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.out.println(e);
        }


        try {


            String queryP = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMap().get("transaction_id") + "'";

            String statusPR = utils.getValueFromDB(conPayment, queryP);
            System.out.println("Payment : " + statusPR);


            String queryO = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + createOrderRedirectResponse.getOrderUid() + "'";

            String statusOR = utils.getValueFromDB(conOrder, queryO);
            System.out.println("Order : " + statusOR);


            String queryC = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String statusCR = utils.getValueFromDB(conCart, queryC);
            System.out.println("Cart : " + statusCR);

            System.out.println("\n\n\n");


            BizdirectCartMessages.PaymentManuallyDeclinedRequest paymentManuallyDeclinedRequest = BizdirectCartMessages.PaymentManuallyDeclinedRequest
                    .newBuilder()
                    .build();

            BizdirectCartMessages.Empty emptyResponse;


            blockingStubCart = BizdirectCartServiceGrpc.newBlockingStub(cartChannel);
            emptyResponse = blockingStubCart.paymentManuallyDeclined(paymentManuallyDeclinedRequest);
            System.out.println(emptyResponse);

//            Assert.assertEquals(ResponseCode.NOT_FOUND, emptyResponse.getResponse().getResponseCode());

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }


            String queryPR = "SELECT state from nuclei_payments.payment_refund_state where transaction_id ='" + createOrderRedirectResponse.getResponseMapMap().get("transaction_id") + "'";

            String stateP = utils.getValueFromDB(conPayment, queryPR);
            System.out.println("Payment : " + stateP);


            assertEquals("IN_PROCESS", stateP);


            String queryOR = "SELECT order_state from  nuclei_orders.orders where order_uid ='" + orderUid + "'";

            String stateO = utils.getValueFromDB(conOrder, queryOR);
            System.out.println("Order : " + stateO);


            assertEquals("PAYMENT_INITIATED", stateO);

            String queryCR = "SELECT state from  nuclei_cart.cart_info where cart_uid ='" + createCartResponse.getCartUid() + "'";

            String stateC = utils.getValueFromDB(conCart, queryCR);
            System.out.println("Cart : " + stateC);


            assertEquals("PAYMENT_INITIATED", stateC);


        } catch (Exception e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e);
            return;
        }

    }

}

