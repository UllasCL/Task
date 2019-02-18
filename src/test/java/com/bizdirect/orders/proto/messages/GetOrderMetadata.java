package com.bizdirect.orders.proto.messages;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class GetOrderMetadata extends BaseTest {


    @Test(description = "Test without orderId.")
    public void TestCase1() {

        RequestSpecification request = given();
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOV1U3WExndnl0dGNYeC91bytuSlBUUmIzVTh2ZXlGNjFuV0NJaVAxSU8vUWxacDQ0S2phdnN1RC9ZamM3NmIvUWtEdWErRi96MjNpczVmMzFpWFYyZUVieWtGamRiQVA3RXp3Ymt3a3lucEo3NEZEZVhuN2ZzSkc4V3RWYXJETDJld3IrWE9JcERVTHl1TTllVUZES2lFUnlndUQxMHhxaUtLakZJdFUxNS9tVmZrQnlXY2p3NGwyNzBWbkx1MDVRdi9ST0JCWEc0MjFZM21NSXNTQUlkdU1Ta0JQd1RuRjdRa09NYjBNMVpwN0Vkcng2Q0JFTElWdnZqdm80NTZxIiwiUm9sZSI6IlJPTEVfVVNFUiJ9.Tgr4Nrrl9BkcXLPCWUEKyRJieaiYSpHrCdkmv6QCd_wmk_bNwfAcOGjk6oA01e8bFL5q2602EY83EmGAEZvGOA");


        String urlGet = properties.getProperty("common.service.getOrder.metadata.url")+"?orderId="+""+"&categoryId="+"1";

        Response response = request.get(urlGet);
        System.out.println(response.prettyPrint());
        Assert.assertEquals(500, response.getStatusCode());
    }


    @Test(description = "Test without categoryId.")
    public void TestCase2() {


        RequestSpecification request = given();
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOV1U3WExndnl0dGNYeC91bytuSlBUUmIzVTh2ZXlGNjFuV0NJaVAxSU8vUWxacDQ0S2phdnN1RC9ZamM3NmIvUWtEdWErRi96MjNpczVmMzFpWFYyZUVieWtGamRiQVA3RXp3Ymt3a3lucEo3NEZEZVhuN2ZzSkc4V3RWYXJETDJld3IrWE9JcERVTHl1TTllVUZES2lFUnlndUQxMHhxaUtLakZJdFUxNS9tVmZrQnlXY2p3NGwyNzBWbkx1MDVRdi9ST0JCWEc0MjFZM21NSXNTQUlkdU1Ta0JQd1RuRjdRa09NYjBNMVpwN0Vkcng2Q0JFTElWdnZqdm80NTZxIiwiUm9sZSI6IlJPTEVfVVNFUiJ9.Tgr4Nrrl9BkcXLPCWUEKyRJieaiYSpHrCdkmv6QCd_wmk_bNwfAcOGjk6oA01e8bFL5q2602EY83EmGAEZvGOA");

        String urlGet = properties.getProperty("common.service.getOrder.metadata.url")+"?orderId="+"8deefb6b-6b16-4441-b180-6f5d6d6741ca"+"&categoryId="+"";


        Response response = request.get(urlGet);
        System.out.println(response.prettyPrint());
        Assert.assertEquals(500, response.getStatusCode());
    }


    @Test(description = "Test with invalid orderId.")
    public void TestCase3() {


        RequestSpecification request = given();
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOV1U3WExndnl0dGNYeC91bytuSlBUUmIzVTh2ZXlGNjFuV0NJaVAxSU8vUWxacDQ0S2phdnN1RC9ZamM3NmIvUWtEdWErRi96MjNpczVmMzFpWFYyZUVieWtGamRiQVA3RXp3Ymt3a3lucEo3NEZEZVhuN2ZzSkc4V3RWYXJETDJld3IrWE9JcERVTHl1TTllVUZES2lFUnlndUQxMHhxaUtLakZJdFUxNS9tVmZrQnlXY2p3NGwyNzBWbkx1MDVRdi9ST0JCWEc0MjFZM21NSXNTQUlkdU1Ta0JQd1RuRjdRa09NYjBNMVpwN0Vkcng2Q0JFTElWdnZqdm80NTZxIiwiUm9sZSI6IlJPTEVfVVNFUiJ9.Tgr4Nrrl9BkcXLPCWUEKyRJieaiYSpHrCdkmv6QCd_wmk_bNwfAcOGjk6oA01e8bFL5q2602EY83EmGAEZvGOA");


        String urlGet = properties.getProperty("common.service.getOrder.metadata.url")+"?orderId="+"INVALID"+"&categoryId="+"1";

        Response response = request.get(urlGet);
        System.out.println(response.prettyPrint());
        Assert.assertEquals(500, response.getStatusCode());
    }


    @Test(description = "Test with invalid categoryId.")
    public void TestCase4() {


        RequestSpecification request = given();
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOV1U3WExndnl0dGNYeC91bytuSlBUUmIzVTh2ZXlGNjFuV0NJaVAxSU8vUWxacDQ0S2phdnN1RC9ZamM3NmIvUWtEdWErRi96MjNpczVmMzFpWFYyZUVieWtGamRiQVA3RXp3Ymt3a3lucEo3NEZEZVhuN2ZzSkc4V3RWYXJETDJld3IrWE9JcERVTHl1TTllVUZES2lFUnlndUQxMHhxaUtLakZJdFUxNS9tVmZrQnlXY2p3NGwyNzBWbkx1MDVRdi9ST0JCWEc0MjFZM21NSXNTQUlkdU1Ta0JQd1RuRjdRa09NYjBNMVpwN0Vkcng2Q0JFTElWdnZqdm80NTZxIiwiUm9sZSI6IlJPTEVfVVNFUiJ9.Tgr4Nrrl9BkcXLPCWUEKyRJieaiYSpHrCdkmv6QCd_wmk_bNwfAcOGjk6oA01e8bFL5q2602EY83EmGAEZvGOA");


        String urlGet = properties.getProperty("common.service.getOrder.metadata.url")+"?orderId="+"8deefb6b-6b16-4441-b180-6f5d6d6741ca"+"&categoryId="+"111";

        Response response = request.get(urlGet);
        System.out.println(response.prettyPrint());
        Assert.assertEquals(500, response.getStatusCode());
    }

    @Test(description = "Test with valid Payload.")
    public void TestCase5() {

        RequestSpecification request = given();
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOV1U3WExndnl0dGNYeC91bytuSlBUUmIzVTh2ZXlGNjFuV0NJaVAxSU8vUWxacDQ0S2phdnN1RC9ZamM3NmIvUWtEdWErRi96MjNpczVmMzFpWFYyZUVieWtGamRiQVA3RXp3Ymt3a3lucEo3NEZEZVhuN2ZzSkc4V3RWYXJETDJld3IrWE9JcERVTHl1TTllVUZES2lFUnlndUQxMHhxaUtLakZJdFUxNS9tVmZrQnlXY2p3NGwyNzBWbkx1MDVRdi9ST0JCWEc0MjFZM21NSXNTQUlkdU1Ta0JQd1RuRjdRa09NYjBNMVpwN0Vkcng2Q0JFTElWdnZqdm80NTZxIiwiUm9sZSI6IlJPTEVfVVNFUiJ9.Tgr4Nrrl9BkcXLPCWUEKyRJieaiYSpHrCdkmv6QCd_wmk_bNwfAcOGjk6oA01e8bFL5q2602EY83EmGAEZvGOA");

        String urlGet = properties.getProperty("common.service.getOrder.metadata.url")+"?orderId="+"8deefb6b-6b16-4441-b180-6f5d6d6741ca"+"&categoryId="+"1";

        Response response = request.get(urlGet);
        System.out.println(response.prettyPrint());
        Assert.assertEquals(200, response.getStatusCode());
    }
}
