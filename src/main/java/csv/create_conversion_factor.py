#!/usr/bin/env python3

import csv

# Our 20 selected food IDs (from MEAL_LOGGING_FOODS.csv)
FOOD_IDS = [5, 7, 8, 10, 61, 129, 415, 1415, 2873, 2880, 2885, 5573, 5574, 5575, 5580, 5582, 5585, 5586, 5587, 5589]

# Common measurement IDs we want to include (cooking measurements)
MEASURE_IDS = {
    341: "100ml",
    383: "125ml (1/2 cup)", 
    385: "15ml (1 tablespoon)",
    415: "250ml (1 cup)",
    405: "2 tablespoons", 
    439: "5ml (1 teaspoon)",
    428: "30ml (2 tablespoons)",
    413: "200ml",
    414: "225ml"
}

def create_conversion_factors():
    print("🔧 Creating conversion factors for meal logging foods...")

    # Read the full conversion factor data
    with open('CONVERSION FACTOR _UTF8.csv', 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        conversion_data = list(reader)

    # Read measure names to get descriptions
    measure_names = {}
    with open('MEASURE NAME _UTF8.csv', 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            measure_id = int(row['MeasureID'])
            measure_names[measure_id] = row['MeasureDescription']

    # Filter for our foods and common measurements
    filtered_conversions = []

    for row in conversion_data:
        try:
            food_id = int(row['FoodID'])
            measure_id = int(row['MeasureID'])

            # Include if it's one of our foods AND one of our common measurements
            if food_id in FOOD_IDS and measure_id in MEASURE_IDS:
                filtered_conversions.append(row)
                print(f"✅ Food {food_id}, Measure {measure_id} ({MEASURE_IDS[measure_id]}): {row['ConversionFactorValue']}")

        except (ValueError, KeyError):
            continue

    # Write the filtered conversion factors
    with open('MEAL_LOGGING_CONVERSION_FACTORS.csv', 'w', newline='', encoding='utf-8') as f:
        if filtered_conversions:
            fieldnames = ['FoodID', 'MeasureID', 'ConversionFactorValue', 'ConvFactorDateOfEntry']
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(filtered_conversions)

    print(f"📁 Created: MEAL_LOGGING_CONVERSION_FACTORS.csv")
    print(f"📊 Total conversion factors: {len(filtered_conversions)}")

    # Also create measure names for our selected measures
    filtered_measures = []
    for measure_id in MEASURE_IDS:
        if measure_id in measure_names:
            filtered_measures.append({
                'MeasureID': measure_id,
                'MeasureDescription': measure_names[measure_id],
                'MeasureDescriptionF': '',  # We'll ignore French
                'CommonName': MEASURE_IDS[measure_id]
            })

    # Write the filtered measure names
    with open('MEAL_LOGGING_MEASURES.csv', 'w', newline='', encoding='utf-8') as f:
        fieldnames = ['MeasureID', 'MeasureDescription', 'MeasureDescriptionF', 'CommonName']
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(filtered_measures)

    print(f"📁 Created: MEAL_LOGGING_MEASURES.csv")
    print(f"📊 Total measures: {len(filtered_measures)}")

if __name__ == "__main__":
    create_conversion_factors() 
    