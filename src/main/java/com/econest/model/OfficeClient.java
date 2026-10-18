package com.econest.model;

import com.econest.model.util.Validator;

public class OfficeClient extends Client {

    public static final double BULK_DISCOUNT_RATE = 0.10;

    private String companyName;
    private String contactPerson;

    public OfficeClient(int id, String name, String email, String phone, String address,
                        String companyName, String contactPerson) {
        super(id, name, email, phone, address);
        setCompanyName(companyName);
        setContactPerson(contactPerson);
    }

    public String getCompanyName() {
        return companyName;
    }

    public final void setCompanyName(String companyName) {
        this.companyName = Validator.requireText("Company name", companyName);
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public final void setContactPerson(String contactPerson) {
        this.contactPerson = Validator.requireText("Contact person", contactPerson);
    }

    @Override
    public double getDiscountRate() {
        return BULK_DISCOUNT_RATE;
    }

    @Override
    public String getClientType() {
        return "Office";
    }

    @Override
    public String getDetails() {
        return companyName + " | Contact: " + contactPerson;
    }
}
