package com.econest.model;

import com.econest.model.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelValidationTest {

    @Test
    void validPlantIsCreated() {
        Plant p = new Plant(0, "Aloe Vera", 450.0, 5, "Aloe barbadensis", "Full sun", "Weekly");
        assertEquals("Plant", p.getCategory());
        assertEquals(450.0, p.calculatePrice());
    }

    @Test
    void negativePriceIsRejected() {
        assertThrows(ValidationException.class, () -> new Planter(0, "Pot", -5, 3, "Clay", "Medium"));
    }

    @Test
    void zeroPriceIsRejected() {
        assertThrows(ValidationException.class, () -> new Planter(0, "Pot", 0, 3, "Clay", "Medium"));
    }

    @Test
    void ecoRatingOutOfRangeIsRejected() {
        assertThrows(ValidationException.class, () -> new Planter(0, "Pot", 100, 6, "Clay", "Medium"));
        assertThrows(ValidationException.class, () -> new Planter(0, "Pot", 100, 0, "Clay", "Medium"));
    }

    @Test
    void emptyNameIsRejected() {
        assertThrows(ValidationException.class, () -> new Planter(0, "   ", 100, 3, "Clay", "Medium"));
    }

    @Test
    void kitAppliesBundleDiscountPolymorphically() {
        Product kit = new GardeningKit(0, "Herb Kit", 1000.0, 4, "Seeds, soil, pots", 10);
        assertEquals(900.0, kit.calculatePrice());
    }

    @Test
    void kitDiscountAboveLimitIsRejected() {
        assertThrows(ValidationException.class, () -> new GardeningKit(0, "Kit", 100, 3, "x", 80));
    }

    @Test
    void invalidEmailIsRejected() {
        assertThrows(ValidationException.class,
                () -> new HouseholdClient(0, "Nimal", "not-an-email", "0771234567", "Colombo", 3));
    }

    @Test
    void invalidPhoneIsRejected() {
        assertThrows(ValidationException.class,
                () -> new HouseholdClient(0, "Nimal", "nimal@example.com", "abc", "Colombo", 3));
    }

    @Test
    void officeClientHasBulkDiscount() {
        Client office = new OfficeClient(0, "Kamal", "kamal@corp.com", "0112345678", "Colombo 3",
                "Corp Ltd", "Kamal P.");
        Client home = new HouseholdClient(0, "Nimal", "nimal@example.com", "0771234567", "Kandy", 4);
        assertEquals(0.10, office.getDiscountRate());
        assertEquals(0.0, home.getDiscountRate());
    }
}
