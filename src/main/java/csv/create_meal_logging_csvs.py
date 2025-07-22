#!/usr/bin/env python3
"""
Meal Logging CSV Creator
Extracts 20 selected foods and their related data for meal logging system
"""

import csv
import os

# Our 20 selected Food IDs for meal logging
SELECTED_FOOD_IDS = [5, 7, 8, 10, 61, 129, 415, 1415, 2873, 2880, 2885, 5573, 5574, 5575, 5580, 5582, 5585, 5586, 5587, 5589]

def extract_foods():
    """Extract 20 selected foods from FOOD NAME _UTF8.csv"""
    input_file = "FOOD NAME _UTF8.csv"
    output_file = "MEAL_LOGGING_FOODS.csv"
    
    with open(input_file, 'r', encoding='utf-8') as infile:
        reader = csv.DictReader(infile)
        
        with open(output_file, 'w', encoding='utf-8', newline='') as outfile:
            writer = csv.DictWriter(outfile, fieldnames=reader.fieldnames)
            writer.writeheader()
            
            found_count = 0
            for row in reader:
                if int(row['FoodID']) in SELECTED_FOOD_IDS:
                    writer.writerow(row)
                    found_count += 1
                    print(f"✅ Found: {row['FoodID']} - {row['FoodDescription']}")
            
            print(f"📊 Extracted {found_count} foods to {output_file}")

def extract_food_groups():
    """Extract food groups for our selected foods"""
    # First, get the food group IDs from our selected foods
    food_group_ids = set()
    
    with open("MEAL_LOGGING_FOODS.csv", 'r', encoding='utf-8') as file:
        reader = csv.DictReader(file)
        for row in reader:
            food_group_ids.add(int(row['FoodGroupID']))
    
    print(f"🏷️  Need food groups: {sorted(food_group_ids)}")
    
    # Extract those food groups
    input_file = "FOOD GROUP _UTF8.csv"
    output_file = "MEAL_LOGGING_FOOD_GROUPS.csv"
    
    with open(input_file, 'r', encoding='utf-8') as infile:
        reader = csv.DictReader(infile)
        
        with open(output_file, 'w', encoding='utf-8', newline='') as outfile:
            writer = csv.DictWriter(outfile, fieldnames=reader.fieldnames)
            writer.writeheader()
            
            found_count = 0
            for row in reader:
                if int(row['FoodGroupID']) in food_group_ids:
                    writer.writerow(row)
                    found_count += 1
                    print(f"✅ Food Group: {row['FoodGroupID']} - {row['FoodGroupName']}")
            
            print(f"📊 Extracted {found_count} food groups to {output_file}")

def extract_nutrient_amounts():
    """Extract nutrient amounts for our selected foods"""
    input_file = "NUTRIENT AMOUNT _UTF8.csv"
    output_file = "MEAL_LOGGING_NUTRIENT_AMOUNTS.csv"
    
    with open(input_file, 'r', encoding='utf-8') as infile:
        reader = csv.DictReader(infile)
        
        with open(output_file, 'w', encoding='utf-8', newline='') as outfile:
            writer = csv.DictWriter(outfile, fieldnames=reader.fieldnames)
            writer.writeheader()
            
            found_count = 0
            for row in reader:
                if int(row['FoodID']) in SELECTED_FOOD_IDS:
                    writer.writerow(row)
                    found_count += 1
            
            print(f"📊 Extracted {found_count} nutrient amounts to {output_file}")

def extract_nutrients():
    """Extract all nutrients (we'll need most of them for nutrition facts)"""
    input_file = "NUTRIENT NAME _UTF8.csv"
    output_file = "MEAL_LOGGING_NUTRIENTS.csv"
    
    # Copy all nutrients since we want complete nutrition profiles
    with open(input_file, 'r', encoding='utf-8') as infile:
        content = infile.read()
    
    with open(output_file, 'w', encoding='utf-8') as outfile:
        outfile.write(content)
    
    # Count lines for reporting
    with open(output_file, 'r', encoding='utf-8') as file:
        line_count = sum(1 for line in file) - 1  # Subtract header
    
    print(f"📊 Copied all {line_count} nutrients to {output_file}")

def main():
    """Create meal logging CSV files"""
    print("🍽️  Creating Meal Logging CSV Files")
    print("=" * 50)
    
    print(f"\n🎯 Selected Food IDs: {SELECTED_FOOD_IDS}")
    print(f"📊 Total Foods: {len(SELECTED_FOOD_IDS)}")
    
    print("\n1️⃣  Extracting Foods...")
    extract_foods()
    
    print("\n2️⃣  Extracting Food Groups...")
    extract_food_groups()
    
    print("\n3️⃣  Extracting Nutrient Amounts...")
    extract_nutrient_amounts()
    
    print("\n4️⃣  Extracting Nutrients...")
    extract_nutrients()
    
    print("\n🎉 Meal logging CSV files created!")
    print("\n📁 Files created:")
    print("  - MEAL_LOGGING_FOODS.csv")
    print("  - MEAL_LOGGING_FOOD_GROUPS.csv") 
    print("  - MEAL_LOGGING_NUTRIENT_AMOUNTS.csv")
    print("  - MEAL_LOGGING_NUTRIENTS.csv")

if __name__ == "__main__":
    main() 