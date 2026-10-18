package com.econest.model;

import com.econest.model.exception.ValidationException;

public class HouseholdClient extends Client {

    private int householdSize;

    public HouseholdClient(int id, String name, String email, String phone, String address, int householdSize) {
        super(id, name, email, phone, address);
        setHouseholdSize(householdSize);
    }

    public int getHouseholdSize() {
        return householdSize;
    }

    public final void setHouseholdSize(int householdSize) {
        if (householdSize < 1 || householdSize > 30) {
            throw new ValidationException("Household size must be between 1 and 30.");
        }
        this.householdSize = householdSize;
    }

    @Override
    public String getClientType() {
        return "Household";
    }

    @Override
    public String getDetails() {
        return "Household size: " + householdSize;
    }
}
