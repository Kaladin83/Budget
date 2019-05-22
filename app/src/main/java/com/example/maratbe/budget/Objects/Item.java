package com.example.maratbe.budget.Objects;

/**
 * Item class - Holds all the data of added expense
 */
public class Item {
    private String category;
    private double amount;
    private String date;
    private int payDate;
    private String currency;
    private String description;

    public Item(String category, String date, int payDate, double amount, String description) {
        setAmount(amount);
        setCategory(category);
        setDate(date);
        setPayDate(payDate);
        setDescription(description);
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getPayDate() {
        return payDate;
    }

    public void setPayDate(int payDate) {
        this.payDate = payDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
