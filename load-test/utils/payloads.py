"""
Test data and payloads for n11.com search testing
Contains search keywords for different test scenarios
"""

# Positive test keywords - popular search terms on n11.com
POSITIVE_KEYWORDS = [
    "telefon",
    "bilgisayar", 
    "kitap"
]

# Medium length keyword
MEDIUM_KEYWORDS = [
    "kitap"
]

# Brand-based keywords for brand search tests
BRAND_KEYWORDS = [
    "Samsung",
    "Apple",
    "Xiaomi",
    "Philips",
    "Bosch"
]

# Keyword combinations for multiple word search tests
COMBINED_KEYWORDS = [
    "akıllı telefon",
    "kablosuz kulaklık",
    "bluetooth hoparlör",
    "oyuncu bilgisayarı",
    "elektrikli süpürge"
]

# Foreign language keywords
FOREIGN_LANGUAGE_KEYWORDS = [
    "smartphone",    # English
    "ordinateur",    # French
    "computadora",   # Spanish
    "computer",      # English/German
    "カメラ"          # Japanese (camera)
]

# Emoji and special character keywords
EMOJI_SPECIAL_KEYWORDS = [
    "telefon 📱",
    "bilgisayar 💻",
    "kitap 📚",
    "❤️ hediye",
    "€ ürünler"
]

# Negative test cases
NEGATIVE_KEYWORDS = [
    "",  # Empty search
    "!@#$%^&*()",  # Special characters
    "a" * 201,  # 201 character long string (over 200 chars)
]

# Edge case keywords
EDGE_CASE_KEYWORDS = [
    "a",  # Single character
    "1",  # Single number
    "ç",  # Turkish character
    "   ",  # Only spaces
    "123456789",  # Numbers only
]

# Rapid consecutive search terms
RAPID_SEARCH_SEQUENCE = [
    "telefon",
    "bilgisayar", 
    "kitap"
]

# Rapid repeated search (same term multiple times)
REPEATED_SEARCH_TERM = "telefon"
REPEATED_SEARCH_COUNT = 5

# All keywords combined for random selection
ALL_KEYWORDS = POSITIVE_KEYWORDS + NEGATIVE_KEYWORDS + EDGE_CASE_KEYWORDS

def get_search_payload(keyword):
    """
    Returns search payload for n11.com search
    Args:
        keyword (str): Search term
    Returns:
        dict: Query parameters for search request
    """
    return {
        'q': keyword
    }

def get_random_positive_keyword():
    """Returns a random positive keyword"""
    import random
    return random.choice(POSITIVE_KEYWORDS)

def get_random_negative_keyword():
    """Returns a random negative keyword"""
    import random
    return random.choice(NEGATIVE_KEYWORDS)

def get_random_edge_case_keyword():
    """Returns a random edge case keyword"""
    import random
    return random.choice(EDGE_CASE_KEYWORDS)

def get_random_brand_keyword():
    """Returns a random brand keyword"""
    import random
    return random.choice(BRAND_KEYWORDS)

def get_random_combined_keyword():
    """Returns a random combined keyword"""
    import random
    return random.choice(COMBINED_KEYWORDS)

def get_random_foreign_language_keyword():
    """Returns a random foreign language keyword"""
    import random
    return random.choice(FOREIGN_LANGUAGE_KEYWORDS)

def get_random_emoji_special_keyword():
    """Returns a random emoji/special character keyword"""
    import random
    return random.choice(EMOJI_SPECIAL_KEYWORDS)

def get_all_test_keywords():
    """Returns all test keywords for comprehensive testing"""
    return {
        'positive': POSITIVE_KEYWORDS,
        'negative': NEGATIVE_KEYWORDS,
        'edge_cases': EDGE_CASE_KEYWORDS,
        'rapid_sequence': RAPID_SEARCH_SEQUENCE,
        'brands': BRAND_KEYWORDS,
        'combined': COMBINED_KEYWORDS,
        'foreign_language': FOREIGN_LANGUAGE_KEYWORDS,
        'emoji_special': EMOJI_SPECIAL_KEYWORDS
    }
