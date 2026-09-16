package com.example.smartpantrymanager.models;

public class PantryItem {
    private int id;                 // -1 means "not yet saved to DB"
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate;      // format "yyyy-MM-dd" or null

    // Constructor for a NEW item (no id yet)
    public PantryItem(String name, double quantity, String unit, String expiryDate) {
        this.id = -1;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    // Constructor for an item loaded FROM the DB
    public PantryItem(int id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    @Override
    public String toString() {
        return name + " — " + quantity + " " + unit;
    }
}