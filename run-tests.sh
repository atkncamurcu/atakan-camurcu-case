#!/bin/bash

# Insider Automation Framework Test Execution Script
# This script provides various options for running the Insider automation tests

echo "==================================="
echo "Insider Automation Framework"
echo "==================================="

# Function to display usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help          Show this help message"
    echo "  -a, --all           Run all Insider tests"
    echo "  -m, --main          Run main Insider automation scenario only"
    echo "  -s, --smoke         Run Insider smoke tests (quick validation)"
    echo "  -b, --browser       Specify browser (chrome|firefox)"
    echo "  -d, --headless      Run in headless mode"
    echo "  -c, --clean         Clean before running tests"
    echo ""
    echo "Examples:"
    echo "  $0 --all                     # Run all Insider tests with default settings"
    echo "  $0 --main --browser firefox  # Run main Insider scenario with Firefox"
    echo "  $0 --smoke --headless        # Run Insider smoke tests in headless mode"
    echo ""
}

# Default values
BROWSER="chrome"
HEADLESS="false"
CLEAN="false"
TEST_TYPE="all"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_usage
            exit 0
            ;;
        -a|--all)
            TEST_TYPE="all"
            shift
            ;;
        -m|--main)
            TEST_TYPE="main"
            shift
            ;;
        -s|--smoke)
            TEST_TYPE="smoke"
            shift
            ;;
        -b|--browser)
            BROWSER="$2"
            shift 2
            ;;
        -d|--headless)
            HEADLESS="true"
            shift
            ;;
        -c|--clean)
            CLEAN="true"
            shift
            ;;
        *)
            echo "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

echo "Configuration:"
echo "  Browser: $BROWSER"
echo "  Headless: $HEADLESS"
echo "  Clean: $CLEAN"
echo "  Test Type: $TEST_TYPE"
echo ""

# Update config.properties with runtime settings
sed -i.bak "s/browser=.*/browser=$BROWSER/" src/main/resources/config.properties
sed -i.bak "s/headless=.*/headless=$HEADLESS/" src/main/resources/config.properties

# Clean if requested
if [[ "$CLEAN" == "true" ]]; then
    echo "Cleaning project..."
    mvn clean
fi

# Run tests based on type
echo "Starting test execution..."
case $TEST_TYPE in
    "all")
        mvn test
        ;;
    "main")
        mvn test -Dtest=InsiderAutomationTest#testInsiderAutomationScenario
        ;;
    "smoke")
        mvn test -Dtest=InsiderAutomationTest#testHomePageLoad,InsiderAutomationTest#testCareersPageNavigation
        ;;
esac

# Restore original config.properties
mv src/main/resources/config.properties.bak src/main/resources/config.properties

echo ""
echo "Test execution completed!"
echo "Check reports at: target/surefire-reports/"
echo "Check screenshots at: test-output/screenshots/"
echo "Check logs at: test-output/logs/"
