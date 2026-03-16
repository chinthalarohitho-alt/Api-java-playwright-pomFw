#!/bin/bash

# Find the local Allure binary
ALLURE_BIN=$(find .allure -name allure | head -n 1)
OUTPUT_DIR="allure-report-shareable"

# Decide which results directory to use (prioritize target/allure-results for Maven runs)
if [ -d "target/allure-results" ]; then
    RESULTS_DIR="target/allure-results"
elif [ -d "allure-results" ]; then
    RESULTS_DIR="allure-results"
else
    # If no directory found, try to generate it via maven
    echo "No results found. Running mvn allure:report to ensure results are ready..."
    mvn allure:report
    RESULTS_DIR="target/allure-results"
fi

if [ ! -d "$RESULTS_DIR" ]; then
    echo "Error: Could not find or generate Allure results."
    exit 1
fi

# Ensure Allure CLI exists
if [ -z "$ALLURE_BIN" ] || [ ! -f "$ALLURE_BIN" ]; then
    echo "Allure CLI not found. Running 'mvn allure:install'..."
    mvn allure:install
    ALLURE_BIN=$(find .allure -name allure | head -n 1)
fi

if [ -z "$ALLURE_BIN" ] || [ ! -f "$ALLURE_BIN" ]; then
    echo "Error: Could not find or install Allure CLI."
    exit 1
fi

echo "Using results from: $RESULTS_DIR"
echo "Generating shareable Allure report..."
$ALLURE_BIN generate "$RESULTS_DIR" -o "$OUTPUT_DIR" --single-file --clean

if [ $? -eq 0 ]; then
    echo "-------------------------------------------------------"
    echo "Success! Report generated in: $OUTPUT_DIR/index.html"
    echo "You can now share this single HTML file with anyone."
    echo "-------------------------------------------------------"
else
    echo "Error: Failed to generate Allure report."
    exit 1
fi
