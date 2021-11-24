package com.exmaple.apiaas;

import com.example.apiaas.graphql.PoliciesFeedPayload;
import com.example.apiaas.graphql.PoliciesFeedPayloadResponseProjection;
import com.example.apiaas.graphql.PoliciesFeedQueryRequest;
import com.example.apiaas.graphql.PolicyResponseProjection;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.joda.time.DateTime;

import java.util.Map;


public class App {

    public static void main(String[] args) {

        String header = "Bearer eyJraWQiOiIxNGZRamFOSHhzb3FiYTYxUGhEQk0yTUhEYzI4VDN4TU1oRTc0VDYxZ3M4IiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULlJ4WEVYeDZSa0lmSERPVXQzTmpyY0EzTHp2eTlKZVNsT0o0UXhldnZQZW8iLCJpc3MiOiJodHRwczovL2F1dGguZGV2LmlwdGlxLmNvbS9vYXV0aDIvYXVzcWdrb2Z6ZmxWSWxKam8weDYiLCJhdWQiOiJhcGk6Ly9zaXQiLCJpYXQiOjE2Mzc2NjM5MzEsImV4cCI6MTYzNzY3MTEzMSwiY2lkIjoiMG9hc2F1NWk5MXMxRjZ2RlQweDYiLCJzY3AiOlsidGFuZ3JhbS51bmRlcndyaXRpbmcucmVhZCIsInRhbmdyYW0ucGFydGllcy5tYW5hZ2UiLCJ0YW5ncmFtLnVuZGVyd3JpdGluZy5tYW5hZ2UiLCJ0YW5ncmFtLnBheW1lbnRzLm1hbmFnZSIsInRhbmdyYW0uY2FsY3VsYXRpb25zLm1hbmFnZSIsInRhbmdyYW0uY2FsY3VsYXRpb25zLnJlYWQiLCJ0YW5ncmFtLmRvY3VtZW50cy5tYW5hZ2UiLCJ0YW5ncmFtLnBvbGljeS5yZWFkIiwidGFuZ3JhbS5wcm9kdWN0cy5yZWFkIiwidGFuZ3JhbS5kb2N1bWVudHMucmVhZCIsInRhbmdyYW0ucGVyc29ucy5yZWFkIiwidGFuZ3JhbS5wb2xpY3kubWFuYWdlIiwidGFuZ3JhbS5wYXltZW50cy5yZWFkIl0sInN1YiI6IjBvYXNhdTVpOTFzMUY2dkZUMHg2IiwicGFydG5lcnMiOlsiZ2xkIl0sImp1cmlzZGljdGlvbnMiOlsiZ2JyIl19.ezgUuKNy0Y8Vq6j1sjxZeE5RgXJRT57d6UgP9QxXDqLcLqYMHCAGC5SHEh2Kb6N0KCh2jAhAuCVsdpmBN_8MaHvKHRBBlp_3dmXKZgNcNWsRiD1jet_et-tBDIIFmvmzDkmswTGoOCkguCE1DYNxAAe4gURBH5PLilksOQy0EFmIFXbP3lDjRwDTiAm3tdq_hS_dX-Xw6kyBg2LMpfJ4d9gfjtIoAYewDj8Y8weSHANPG32UqrUbxj5GEtcQIfbGjrkEIL8o2PYIg18pJicyF-Aa8C1RBiePCVAcP8bfTI-Mw49c0WskqtOMOa4wOnrB26-dfHGhUi45S-YDFijnpQ";
        RequestSpecBuilder specBuilder = new RequestSpecBuilder();
        specBuilder.setBaseUri("https://bff.sit.apiaap.np.lh.emea.iptiq.com/")
            .setContentType(ContentType.JSON)
            .addHeader("Authorization", header)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .setAccept(ContentType.JSON);

        PoliciesFeedQueryRequest policiesFeedQueryRequest = new PoliciesFeedQueryRequest.Builder()
            .setLimit(10)
            .setPage(0)
            .setModifiedSince(new DateTime().minusDays(20))
            .build();

        PolicyResponseProjection policyResponseProjection =
            new PolicyResponseProjection()
                .id()
                .displayStatus()
                .externalRefId()
                .lastModifiedDate();
        PoliciesFeedPayloadResponseProjection policies =
            new PoliciesFeedPayloadResponseProjection()
                .totalCount()
                .totalPages()
                .policies(policyResponseProjection);

        GraphQLRequest graphQLRequest = new GraphQLRequest(policiesFeedQueryRequest, policies);
        System.out.println("================== SIMPLE TEXT ==================");
        ValidatableResponse simpleString = RestAssured
            .given()
            .spec(specBuilder.build())
            .body("{\"operationName\":\"GetLatestPolicies\",\"variables\":{\"page\":0,\"limit\":15,\"modifiedSince\":\"2021-10-25T00:00:00.000Z\"},\"query\":\"query GetLatestPolicies($page: Int, $limit: Int, $modifiedSince: DateTime) {\\n  policiesFeed(page: $page, limit: $limit, modifiedSince: $modifiedSince) {\\n    totalCount\\n    totalPages\\n    policies {\\n      id\\n      displayStatus\\n      externalRefId\\n      lastModifiedDate\\n    }\\n  }\\n}\\n\"}")
            .post()
            .then();

        System.out.println(simpleString.extract().jsonPath().toString());

        System.out.println("================== GENERATED ==================");
        ValidatableResponse generated = RestAssured
            .given()
            .spec(specBuilder.build())
            .body(graphQLRequest.toHttpJsonBody())
            .post()
            .then();

        System.out.println(generated.extract().jsonPath().toString());


        GraphQLRequest request2 = new GraphQLRequest("herevere", policiesFeedQueryRequest, policyResponseProjection);
        System.out.println("================== MODEL ==================");
        Model queryModel = new Model.ModelBuilder().variables(null).operationName("whut").query(Map.of("query", graphQLRequest.toQueryString())).build();
        ValidatableResponse modelResponse = RestAssured
            .given()
            .spec(specBuilder.build())
            .body(queryModel)
            .post()
            .then();

        System.out.println(modelResponse.extract().jsonPath().toString());



    }
}
