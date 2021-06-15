package com.ezequiel.addresses.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static com.ezequiel.addresses.domain.AddressRepositoryTestFeature.newMockedAddress;

@DataJpaTest
@ActiveProfiles("test")
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository subject;

    @AfterEach
    public void tearDown() {
        subject.deleteAll();
    }

    @Test
    public void shouldReturnAddressByStreetNameIgnoreCase() {
        List<Address> addresses = new ArrayList<>();
        addresses.add(newMockedAddress());
        addresses.add(newMockedAddress());
        subject.saveAll(addresses);

        List<Address> addressByStreetNameIgnoreCase = subject.findByStreetNameIgnoreCase(addresses.get(0).getStreetName());

        Assertions.assertNotNull(addressByStreetNameIgnoreCase);
        Assertions.assertFalse(addressByStreetNameIgnoreCase.isEmpty());
        Assertions.assertTrue(addressByStreetNameIgnoreCase
                .stream()
                .anyMatch(address -> address.getStreetName().equals(addresses.get(0).getStreetName()))
        );

    }

}
