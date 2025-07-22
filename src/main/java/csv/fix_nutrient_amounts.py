#!/usr/bin/env python3
"""
Fix MEAL_LOGGING_NUTRIENT_AMOUNTS.csv by removing StandardError and NumberofObservations columns
"""

import csv

def fix_nutrient_amounts():
    input_file = "MEAL_LOGGING_NUTRIENT_AMOUNTS.csv"
    output_file = "MEAL_LOGGING_NUTRIENT_AMOUNTS_FIXED.csv"
    
    # Columns we want to keep (matching database schema)
    keep_columns = ['FoodID', 'NutrientID', 'NutrientValue', 'NutrientSourceID', 'NutrientDateOfEntry']
    
    with open(input_file, 'r', encoding='utf-8') as infile:
        reader = csv.DictReader(infile)
        
        with open(output_file, 'w', encoding='utf-8', newline='') as outfile:
            writer = csv.DictWriter(outfile, fieldnames=keep_columns)
            writer.writeheader()
            
            count = 0
            for row in reader:
                # Only write the columns we want to keep
                filtered_row = {col: row[col] for col in keep_columns if col in row}
                writer.writerow(filtered_row)
                count += 1
    
    print(f"✅ Fixed CSV: {count} rows written")
    print(f"📁 Created: {output_file}")
    print(f"🗑️  Removed: StandardError, NumberofObservations columns")

if __name__ == "__main__":
    fix_nutrient_amounts() 