package com.econest.model;

import com.econest.model.util.Validator;

/** Base class of the client hierarchy. */
public abstract class Client {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;

    protected Client(int id, String name, String email, String phone, String address) {
        this.id = id;
        setName(name);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = Validator.requireText("Client name", name);
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        this.email = Validator.requireEmail(email);
    }

    public String getPhone() {
        return phone;
    }

    public final void setPhone(String phone) {
        this.phone = Validator.requirePhone(phone);
    }

    public String getAddress() {
        return address;
    }

    public final void setAddress(String address) {
        this.address = Validator.requireText("Address", address);
    }

    /** "Household" or "Office". */
    public abstract String getClientType();

    public abstract String getDetails();

    /** Polymorphic discount rate (0.10 = 10%). Useful for the order module's total calculation. */
    public double getDiscountRate() {
        return 0.0;
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + getClientType() + ")";
    }
}
