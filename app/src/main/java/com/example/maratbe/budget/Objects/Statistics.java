package com.example.maratbe.budget.Objects;

/**
 * Statistics class - Holds all the statistics data of expenses
 */
public class Statistics {
    private int payDate;
    private double sum;
    private double mean;
    private String category;

    public Statistics()
    {}

    public Statistics(int payDate, double sum, String category)
    {
        setSum(sum);
        setPayDate(payDate);
        setCategory(category);
    }

    public int getPayDate() {
        return payDate;
    }

    public void setPayDate(int payDate) {
        this.payDate = payDate;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
