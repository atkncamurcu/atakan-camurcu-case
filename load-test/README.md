# N11.com Header Search Module Performance Test

This project is a professional load testing solution created using Locust to test the performance of the header search module on n11.com.

## 🎯 Project Purpose

To examine the behavior of n11.com's header search module under different scenarios with a single user, covering positive, negative, and edge case scenarios.

## 📂 Project Structure

```
load-test/
│
├── locustfile.py              # Main Locust entry file
├── tasks/
│   ├── __init__.py
│   └── search_tasks.py        # Search test scenarios
│
├── utils/
│   ├── __init__.py
│   ├── headers.py             # Minimal headers implementation
│   └── payloads.py            # Search keywords and test data
│
├── reports/                   # Folder for generated reports
├── requirements.txt           # locust==2.28.0
└── README.md                  # This file
```

## 🛠️ Requirements

- Python 3.9+
- pip (Python package manager)

## ⚙️ Installation

### 1. Create Virtual Environment (Recommended)

```bash
cd load-test
python3 -m venv venv
source venv/bin/activate  # macOS/Linux
# venv\Scripts\activate   # Windows
```

### 2. Install Dependencies

```bash
pip install -r requirements.txt
```

### 3. Verify Locust Version

```bash
locust --version
# Expected output: locust 2.28.0
```

## 🚀 Running Tests

### Web UI Mode (Recommended)

```bash
locust -f locustfile.py --host=https://www.n11.com
```

In your browser, go to `http://localhost:8089` and set:
- Number of users: `1`
- Spawn rate: `1`
- Run time: `60s` (optional)

### Headless Mode (Command Line)

```bash
# Short test (10 seconds)
locust -f locustfile.py --host=https://www.n11.com --users 1 --spawn-rate 1 --run-time 10s --headless

# Long test (60 seconds)
locust -f locustfile.py --host=https://www.n11.com --users 1 --spawn-rate 1 --run-time 60s --headless

# With CSV report
locust -f locustfile.py --host=https://www.n11.com --users 1 --spawn-rate 1 --run-time 60s --headless --csv=reports/n11_search_extended_test
```

## 📊 Test Scenarios

### 🔹 Positive Tests
- **Search with popular keywords**: `telefon`, `bilgisayar`, `kitap`
- **Medium-length keyword**: `kitap`
- **Redirect validation**: `/arama?q=<keyword>` endpoint check
- **Brand-based search**: `Samsung`, `Apple`, `Xiaomi`, `Philips`, `Bosch`
- **Word combination searches**: `akıllı telefon`, `kablosuz kulaklık`, etc.

### 🔹 Negative Tests
- **Empty query search**: `""` - 200 or appropriate error handling expected
- **Search with invalid characters**: `!@#$%^&*()`
- **Long query test (200+ characters)**

### 🔹 Edge Case Tests
- **Single character search**: `a`
- **3 consecutive rapid searches**: `telefon → bilgisayar → kitap` and measuring response times
- **Search with Turkish characters**: `ç`, `ş`, `ğ`, `ü`, `ö`, `ı`
- **Search with numbers only**: `123456789`
- **Minimal headers implementation**
- **Search in different languages**: English, French, Spanish, etc.
- **Search with emoji and special characters**: `telefon 📱`, `bilgisayar 💻`, etc.
- **Fast and repeated search operations**: Searching the same term 5 times in rapid succession

### 🔹 Customized Scenarios
- **Post-search filtering**: Applying a price filter after performing a search

## 🔧 Configuration

### Headers Management (`utils/headers.py`)

The following minimal headers are used to access n11.com without cookies:

```python
'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36...',
'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
'Accept-Language': 'en-US,en;q=0.5'
```

### Test Data (`utils/payloads.py`)

- Positive keywords: telefon, bilgisayar, kitap
- Brand-based keywords: Samsung, Apple, Xiaomi, Philips, Bosch
- Word combinations: akıllı telefon, kablosuz kulaklık, bluetooth hoparlör, etc.
- Terms in different languages: smartphone, ordinateur, computadora, etc.
- Emoji and special characters: telefon 📱, bilgisayar 💻, kitap 📚, etc.
- Negative test data: empty string, special characters, long string
- Edge case data: single character, Turkish characters, numbers

## 📈 Understanding Results

### Web UI Metrics
- **Request/s**: Number of requests per second
- **Response Time**: Response times (min, max, avg, 95%)
- **Failures**: Failed requests
- **Users**: Number of active users

### Console Output
```
✓ Successful search for 'telefon' - Response time: 0.85s
✓ Empty search handled properly - Status: 200
� Applying price filter to 'Samsung' search results...
✓ Filter applied successfully! Response time: 0.92s
�🚀 Starting rapid consecutive searches...
  ✓ Rapid search 1 'telefon': 0.92s
  ✓ Rapid search 2 'bilgisayar': 0.78s
  ✓ Rapid search 3 'kitap': 0.83s
�� Rapid search sequence complete: 3/3 successful
�� Starting 5 rapid repeated searches for 'telefon'...
  ✓ Repeated Search 1/5: telefon: 0.87s
  ✓ Repeated Search 2/5: telefon: 0.81s
  ✓ Repeated Search 3/5: telefon: 0.84s
  ✓ Repeated Search 4/5: telefon: 0.89s
  ✓ Repeated Search 5/5: telefon: 0.92s
📊 Repeated search complete: 5/5 successful
📈 Response times - Avg: 0.87ms, Min: 0.81ms, Max: 0.92ms
✓ Consistent performance: 5.49% change between first and second half
```
