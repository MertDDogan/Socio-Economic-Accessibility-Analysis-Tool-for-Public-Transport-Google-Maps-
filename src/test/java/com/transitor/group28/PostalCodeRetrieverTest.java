package com.transitor.group28;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostalCodeRetrieverTest {

    @Test
    void testZipcode6211BA() {
        PostalCodeRetriever dataCSV = new PostalCodeRetriever();
        PostalCode postalCode = dataCSV.getDataZipCode("6211BA");
        assertNotNull(postalCode);
    }

    @Test
    void testZipcode6213CA() {
        PostalCodeRetriever dataCSV = new PostalCodeRetriever();
        PostalCode postalCode = dataCSV.getDataZipCode("6213CA");
        assertNotNull(postalCode);
    }

    @Test
    void testZipcode6214XK() {
        PostalCodeRetriever dataCSV = new PostalCodeRetriever();
        PostalCode postalCode = dataCSV.getDataZipCode("6214XK");
        assertNotNull(postalCode);
    }

    @Test
    void testZipcode6229EN() {
        PostalCodeRetriever dataCSV = new PostalCodeRetriever();
        PostalCode postalCode = dataCSV.getDataZipCode("6229EN");
        assertNotNull(postalCode);
    }

    @Test
    void testGettersOfPostalCodes(){
        // Create a PostalCode object
        PostalCode postalCode = new PostalCode(40.712776, -74.005974);

        // Verify getters return the expected values
        assertEquals(40.712776, postalCode.getLat());
        assertEquals(-74.005974, postalCode.getLon());

    }

}