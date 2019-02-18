package com.bizdirect.orders.proto.messages;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.log4testng.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class BaseTest {


    private static Logger logger = Logger.getLogger(BaseTest.class);
    protected ManagedChannel cartChannel,orderChannel,paymentChannel,rechargeChannel;
    Properties properties = new Properties();
    Utils util = new Utils();
    protected  Connection conOrder,conCart,conPayment,conRecharge;



    @BeforeClass
    public void establishChannel() {
        try {
            properties = util.fetchFromPropertiesFile();
            String cartChannelEndPoint = properties.getProperty("cartEndPoint");
            String orderChannelEndPoint = properties.getProperty("orderEndPoint");
            String paymentChannelEndPonint= properties.getProperty("paymentEndPoint");
            String rechargeChannelEndpoint = properties.getProperty("rechargeEndPoint");


            this.cartChannel = ManagedChannelBuilder.forTarget(cartChannelEndPoint)
                    .usePlaintext().build();
            this.orderChannel = ManagedChannelBuilder.forTarget(orderChannelEndPoint)
                    .usePlaintext().build();
            this.paymentChannel = ManagedChannelBuilder.forTarget(paymentChannelEndPonint)
                    .usePlaintext().build();
            this.rechargeChannel = ManagedChannelBuilder.forTarget(rechargeChannelEndpoint)
                    .usePlaintext().build();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @AfterClass
    public void tearDown() throws InterruptedException {
        this.orderChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        this.cartChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        this.paymentChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        this.rechargeChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);


    }



    @BeforeTest
    public void setUp() {
        try {
            properties = util.fetchFromPropertiesFile();

            //Connecting Order DB
            conOrder=connectToDB("Order");

            //Connecting to Cart DB
            conCart=connectToDB("Cart");


            //Connecting to Payment DB
            conPayment=connectToDB("Payment");

            //Connecting to Recharge DB
            conRecharge=connectToDB("Recharge");



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @AfterTest
    public void closeDBConnection(){
        try {
            conOrder.close();
            conCart.close();
            conPayment.close();
            conRecharge.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public Connection connectToDB(String connectionName){
        Connection connection=null;

        try {
            properties = util.fetchFromPropertiesFile();

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String username="",password="",url="";

            if(connectionName.equalsIgnoreCase("Order")){
                logger.info("Connecting to Order  Database");
                url = properties.getProperty("db.url.order");
                username = properties.getProperty("db.order.username");
                password = properties.getProperty("db.order.password");
                logger.info("Connection to Order DB Established ");
            }else if(connectionName.equalsIgnoreCase("Cart")) {
                logger.info("Connecting to Cart Database");
                url = properties.getProperty("db.url.cart");
                username = properties.getProperty("db.cart.username");
                password = properties.getProperty("db.cart.password");
                logger.info("Connection to Cart DB Established ");
            }else if(connectionName.equalsIgnoreCase("Payment")) {
                logger.info("Connecting to Payment Database");
                url = properties.getProperty("db.url.payment");
                username = properties.getProperty("db.payment.username");
                password = properties.getProperty("db.payment.password");
                logger.info("Connection to Payment DB Established ");
            }else if(connectionName.equalsIgnoreCase("Recharge")) {
                logger.info("Connecting to Recharge Database");
                url = properties.getProperty("db.url.recharge");
                username = properties.getProperty("db.recharge.username");
                password = properties.getProperty("db.recharge.password");
                logger.info("Connection to recharge DB Established ");
            }

            connection = DriverManager.getConnection(url, username, password);


        }catch(Exception e){
            e.printStackTrace();
        }
        return connection;
    }


}