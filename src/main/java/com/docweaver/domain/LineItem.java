// src/main/java/com/docweaver/domain/LineItem.java
package com.docweaver.domain;

public class LineItem {

    private final String description;
    private final int quantity;
    private final double unitPrice;
    private final double amount;
    private final String currency;

    public LineItem(String description, int quantity, double unitPrice, double amount, String currency) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
