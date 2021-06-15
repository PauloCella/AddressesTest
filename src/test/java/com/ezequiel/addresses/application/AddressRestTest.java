package com.ezequiel.addresses.application;

import com.ezequiel.addresses.domain.Address;
import com.ezequiel.addresses.domain.AddressRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ezequiel.addresses.application.AddressRestTestFixture.newMockedAddress;
import static com.ezequiel.addresses.application.AddressRestTestFixture.newMockedAddressRequest;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class AddressRestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AddressRepository addressRepository;

    private String URL;

    @BeforeEach
    public void setUp(){
        URL = String.format("http://localhost:%s/addresses-api/v1/addresses", port);
    }

    @AfterEach
    public void  tearDown(){
        addressRepository.deleteAll();
    }

    @Test
    public void shouldCreateOneAddress() {
        given()
                .header("Content-type", "application/json")
                .and()
                .body(newMockedAddressRequest())
                .when()
                .post(URL)
                .then()
                .statusCode(is(HttpStatus.CREATED.value()));
    }

    @Test
    public void shouldCreateOneAddressWithoutLatAndLng(){
        AddressRequest addressRequest = newMockedAddressRequest();
        addressRequest.setLatitude(null);
        addressRequest.setLongitude(null);

        given()
                .header("Content-type", "application/json")
                .and()
                .body(newMockedAddressRequest())
                .when()
                .post(URL)
                .then()
                .statusCode(is(HttpStatus.CREATED.value()));
    }

    @Test
    public void shouldUpdateOneAddress(){
        UUID addressId = addressRepository.save(newMockedAddress()).getId();


        given()
                .header("Content-type", "application/json")
                .and()
                .body(newMockedAddressRequest())
                .when()
                .put(String.format(URL+"/%s",addressId))
                .then()
                .statusCode(is(HttpStatus.OK.value()));
    }

    @Test
    public void shouldDeleteOneAddress(){
        UUID addressId = addressRepository.save(newMockedAddress()).getId();
        when()
                .delete(String.format(URL+"/%s",addressId))
                .then()
                .statusCode(is(HttpStatus.OK.value()));

    }

    @Test
    public void shouldReturnOneAddress(){
        UUID addressId = addressRepository.save(newMockedAddress()).getId();
        when()
                .get(String.format(URL+"/%s",addressId))
                .then()
                .statusCode(is(HttpStatus.OK.value()))
                .body(notNullValue());
    }

    @Test
    public void shouldReturnAllAddress(){
        List<Address> addresses = new ArrayList<>();
        addresses.add(newMockedAddress());
        addresses.add(newMockedAddress());
        addressRepository.saveAll(addresses);
        when()
                .get(URL)
                .then()
                .statusCode(is(HttpStatus.OK.value()))
                .body(notNullValue());
    }


    @Test
    public void shouldReturnAllAddressByStreetName(){
        List<Address> addresses = new ArrayList<>();
        addresses.add(newMockedAddress());
        addresses.add(newMockedAddress());

        addressRepository.saveAll(addresses);

        when()
                .get(String.format(URL+"/search?streetName=%s", addresses.get(0).getStreetName()))
                .then()
                .statusCode(is(HttpStatus.OK.value()))
                .body(notNullValue());
    }




}
