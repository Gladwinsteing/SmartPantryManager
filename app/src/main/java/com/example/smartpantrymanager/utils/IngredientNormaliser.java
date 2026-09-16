package com.example.smartpantrymanager.utils;

public final class IngredientNormaliser {

    private IngredientNormaliser() { /* no instances */ }

    /**
     * Turns any user-typed ingredient name into a canonical lowercase,
     * trimmed, singularised form.
     *   "Tomatoes"  -> "tomato"
     *   "  ONIONS " -> "onion"
     *   "Carrots"   -> "carrot"
     *   "Berries"   -> "berry"
     */
    public static String normalise(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toLowerCase();

        // Remove trailing punctuation and extra whitespace inside
        s = s.replaceAll("[^a-z0-9 ]", "");
        s = s.replaceAll("\\s+", " ");

        if (s.isEmpty()) return s;

        // Handle a few irregulars first
        if (s.equals("potatoes")) return "potato";
        if (s.equals("tomatoes")) return "tomato";
        if (s.equals("loaves"))   return "loaf";

        // -ies  -> -y   (berries -> berry)
        if (s.endsWith("ies") && s.length() > 3) {
            return s.substring(0, s.length() - 3) + "y";
        }
        // -ses / -xes / -zes / -ches / -shes  -> drop "es"
        if (s.endsWith("ses") || s.endsWith("xes") || s.endsWith("zes")
                || s.endsWith("ches") || s.endsWith("shes")) {
            return s.substring(0, s.length() - 2);
        }
        // plain plural  -s  -> (onion -> onion)
        if (s.endsWith("s") && s.length() > 2 && !s.endsWith("ss")) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }
}