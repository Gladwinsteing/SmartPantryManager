package com.example.smartpantrymanager.utils;

public final class UnitConverter {

    private UnitConverter() { /* no instances */ }

    // Unit categories
    public static final int CATEGORY_WEIGHT = 1;
    public static final int CATEGORY_VOLUME = 2;
    public static final int CATEGORY_COUNT  = 3;
    public static final int CATEGORY_UNKNOWN = 0;

    /**
     * Returns which category a unit belongs to.
     * Units in different categories can never be compared.
     */
    public static int getCategory(String unit) {
        if (unit == null) return CATEGORY_UNKNOWN;
        switch (unit.trim().toLowerCase()) {
            case "g": case "gram": case "grams":
            case "kg": case "kilogram": case "kilograms":
                return CATEGORY_WEIGHT;

            case "ml": case "millilitre": case "millilitres":
            case "l": case "litre": case "litres":
                return CATEGORY_VOLUME;

            case "unit": case "units": case "piece": case "pieces":
            case "egg": case "eggs":
                return CATEGORY_COUNT;

            default:
                return CATEGORY_UNKNOWN;
        }
    }

    /**
     * Converts a quantity in the given unit to a base unit for its category:
     *   weight -> grams
     *   volume -> millilitres
     *   count  -> units (no change)
     * Unknown units are returned as-is (their raw number).
     */
    public static double toBase(double quantity, String unit) {
        if (unit == null) return quantity;
        switch (unit.trim().toLowerCase()) {
            // Weight
            case "g": case "gram": case "grams":                 return quantity;
            case "kg": case "kilogram": case "kilograms":        return quantity * 1000.0;

            // Volume
            case "ml": case "millilitre": case "millilitres":    return quantity;
            case "l": case "litre": case "litres":               return quantity * 1000.0;

            // Count
            case "unit": case "units": case "piece": case "pieces":
            case "egg": case "eggs":                             return quantity;

            default:
                return quantity; // unknown — leave as-is
        }
    }

    /**
     * True if two units can be meaningfully compared (same category).
     * Two unknown units are treated as compatible.
     */
    public static boolean sameCategory(String unitA, String unitB) {
        int a = getCategory(unitA);
        int b = getCategory(unitB);
        if (a == CATEGORY_UNKNOWN && b == CATEGORY_UNKNOWN) return true;
        if (a == CATEGORY_UNKNOWN || b == CATEGORY_UNKNOWN) return true; // lenient
        return a == b;
    }
}