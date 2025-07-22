#!/usr/bin/env python3
"""
CSV Encoding Converter
Converts CSV files ending with "2" from cp1252 to UTF-8 encoding
"""

import os
import glob

def convert_csv_to_utf8(input_file, output_file):
    """Convert a single CSV file from cp1252 to UTF-8"""
    try:
        # Read with cp1252 encoding
        with open(input_file, 'r', encoding='cp1252') as f:
            content = f.read()
        
        # Write with UTF-8 encoding
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print(f"✅ Converted: {input_file} → {output_file}")
        return True
    
    except Exception as e:
        print(f"❌ Error converting {input_file}: {e}")
        return False

def main():
    """Convert all CSV files ending with '2' to UTF-8"""
    # Find all CSV files ending with "2"
    pattern = "*2.csv"
    csv_files = glob.glob(pattern)
    
    if not csv_files:
        print("No CSV files ending with '2' found in current directory")
        return
    
    print(f"Found {len(csv_files)} CSV files ending with '2':")
    for file in csv_files:
        print(f"  - {file}")
    
    print("\nStarting conversion...")
    
    successful = 0
    failed = 0
    
    for input_file in csv_files:
        # Generate output filename: "FILENAME2.csv" → "FILENAME_UTF8.csv"
        base_name = input_file.replace("2.csv", "")
        output_file = f"{base_name}_UTF8.csv"
        
        if convert_csv_to_utf8(input_file, output_file):
            successful += 1
        else:
            failed += 1
    
    print(f"\n🎯 Conversion complete!")
    print(f"✅ Successful: {successful}")
    print(f"❌ Failed: {failed}")
    
    if successful > 0:
        print(f"\n📁 UTF-8 files created:")
        utf8_files = glob.glob("*_UTF8.csv")
        for file in sorted(utf8_files):
            print(f"  - {file}")

if __name__ == "__main__":
    main() 