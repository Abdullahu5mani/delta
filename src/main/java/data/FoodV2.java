package data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enhanced Food class that can load and manage its complete nutritional profile.
 * Demonstrates composition: Food "has many" NutrientAmounts.
 */
public class FoodV2 extends Food {
    
    // Nutritional composition - Food has many NutrientAmounts
    private List<NutrientAmount> nutritionalProfile;
    private List<Nutrient> availableNutrients;
    
    // DAOs for loading nutrition data
    private final NutrientAmountDAO nutrientAmountDAO;
    private final NutrientDAO nutrientDAO;
    
    /**
     * Constructor with DAOs for nutrition loading capability.
     */
    public FoodV2(Integer foodId, Integer foodCode, String foodDescription, 
                  NutrientAmountDAO nutrientAmountDAO, NutrientDAO nutrientDAO) {
        super(foodId, foodCode, foodDescription);
        this.nutrientAmountDAO = nutrientAmountDAO;
        this.nutrientDAO = nutrientDAO;
        this.nutritionalProfile = new ArrayList<>();
        this.availableNutrients = new ArrayList<>();
    }
    
    /**
     * Load complete nutritional profile for this food from database.
     */
    public void loadNutritionalProfile() throws DatabaseException {
        if (getFoodId() == null) return;
        
        // Load all nutrient amounts for this food
        List<NutrientAmount> allNutrientAmounts = nutrientAmountDAO.findAll();
        this.nutritionalProfile = allNutrientAmounts.stream()
            .filter(na -> na.getFoodId() != null && na.getFoodId().equals(getFoodId()))
            .collect(Collectors.toList());
        
        // Load corresponding nutrient definitions
        this.availableNutrients = new ArrayList<>();
        for (NutrientAmount na : nutritionalProfile) {
            Nutrient nutrient = nutrientDAO.findById(na.getNutrientId());
            if (nutrient != null) {
                availableNutrients.add(nutrient);
            }
        }
        
        System.out.println("✅ Loaded " + nutritionalProfile.size() + " nutrients for " + getFoodDescription());
    }
    
    /**
     * Get all macronutrients (protein, fat, carbohydrates).
     */
    public List<NutritionalFact> getMacronutrients() {
        List<NutritionalFact> macros = new ArrayList<>();
        int[] macroIds = {203, 204, 205}; // Protein, Fat, Carbohydrates
        
        for (int macroId : macroIds) {
            NutritionalFact fact = getNutritionalFact(macroId);
            if (fact != null) macros.add(fact);
        }
        return macros;
    }
    
    /**
     * Get all vitamins for this food.
     */
    public List<NutritionalFact> getVitamins() {
        return getNutritionalFactsWhere(nutrient -> nutrient.isVitamin());
    }
    
    /**
     * Get all minerals for this food.
     */
    public List<NutritionalFact> getMinerals() {
        return getNutritionalFactsWhere(nutrient -> nutrient.isMineral());
    }
    
    /**
     * Get energy/calories information.
     */
    public NutritionalFact getCalories() {
        return getNutritionalFact(208); // Energy in kcal
    }
    
    /**
     * Get a specific nutritional fact by nutrient ID.
     */
    public NutritionalFact getNutritionalFact(Integer nutrientId) {
        if (nutritionalProfile == null || availableNutrients == null) return null;
        
        for (int i = 0; i < nutritionalProfile.size(); i++) {
            NutrientAmount amount = nutritionalProfile.get(i);
            if (amount.getNutrientId().equals(nutrientId)) {
                Nutrient nutrient = availableNutrients.get(i);
                return new NutritionalFact(nutrient, amount);
            }
        }
        return null;
    }
    
    /**
     * Get nutritional facts matching a condition.
     */
    private List<NutritionalFact> getNutritionalFactsWhere(NutrientPredicate condition) {
        List<NutritionalFact> facts = new ArrayList<>();
        if (nutritionalProfile == null || availableNutrients == null) return facts;
        
        for (int i = 0; i < Math.min(nutritionalProfile.size(), availableNutrients.size()); i++) {
            Nutrient nutrient = availableNutrients.get(i);
            if (condition.test(nutrient)) {
                NutrientAmount amount = nutritionalProfile.get(i);
                facts.add(new NutritionalFact(nutrient, amount));
            }
        }
        return facts;
    }
    
    /**
     * Display complete nutrition facts label.
     */
    public void displayNutritionFacts() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🍽️  NUTRITION FACTS - " + getFoodDescription().toUpperCase());
        System.out.println("=".repeat(50));
        
        // Calories
        NutritionalFact calories = getCalories();
        if (calories != null) {
            System.out.printf("⚡ CALORIES: %.0f %s\n\n", 
                calories.getValue(), calories.getUnit());
        }
        
        // Macronutrients
        System.out.println("📊 MACRONUTRIENTS:");
        for (NutritionalFact macro : getMacronutrients()) {
            System.out.printf("  • %-20s: %6.2f %s\n", 
                macro.getName(), macro.getValue(), macro.getUnit());
        }
        
        // Vitamins
        List<NutritionalFact> vitamins = getVitamins();
        if (!vitamins.isEmpty()) {
            System.out.println("\n💊 VITAMINS:");
            for (NutritionalFact vitamin : vitamins) {
                System.out.printf("  • %-20s: %6.2f %s\n", 
                    vitamin.getName(), vitamin.getValue(), vitamin.getUnit());
            }
        }
        
        // Minerals  
        List<NutritionalFact> minerals = getMinerals();
        if (!minerals.isEmpty()) {
            System.out.println("\n⛏️  MINERALS:");
            for (NutritionalFact mineral : minerals.subList(0, Math.min(5, minerals.size()))) {
                System.out.printf("  • %-20s: %6.2f %s\n", 
                    mineral.getName(), mineral.getValue(), mineral.getUnit());
            }
            if (minerals.size() > 5) {
                System.out.println("  ... and " + (minerals.size() - 5) + " more minerals");
            }
        }
        
        System.out.println("=".repeat(50));
    }
    
    /**
     * Get summary of nutritional completeness.
     */
    public String getNutritionalSummary() {
        if (nutritionalProfile == null) return "Nutritional profile not loaded";
        
        long macroCount = getMacronutrients().size();
        long vitaminCount = getVitamins().size();
        long mineralCount = getMinerals().size();
        
        return String.format("Nutrition Profile: %d total nutrients (%d macros, %d vitamins, %d minerals)", 
            nutritionalProfile.size(), macroCount, vitaminCount, mineralCount);
    }
    
    // Helper classes
    public static class NutritionalFact {
        private final Nutrient nutrient;
        private final NutrientAmount amount;
        
        public NutritionalFact(Nutrient nutrient, NutrientAmount amount) {
            this.nutrient = nutrient;
            this.amount = amount;
        }
        
        public String getName() { return nutrient.getNutrientName(); }
        public Double getValue() { return amount.getNutrientValue(); }
        public String getUnit() { return nutrient.getNutrientUnit(); }
        public String getSymbol() { return nutrient.getNutrientSymbol(); }
        
        public String getFormattedValue() {
            return String.format("%.2f %s", getValue(), getUnit());
        }
        
        @Override
        public String toString() {
            return getName() + ": " + getFormattedValue();
        }
    }
    
    @FunctionalInterface
    interface NutrientPredicate {
        boolean test(Nutrient nutrient);
    }
} 