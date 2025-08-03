"""
Search test scenarios for n11.com header search module
Implements positive, negative, and edge case testing scenarios
"""

import time
import random
from locust import task, TaskSet
from urllib.parse import urlencode
from utils.headers import get_request_headers, get_ajax_headers
from utils.scraper import get_scraper
from utils.payloads import (
    POSITIVE_KEYWORDS, NEGATIVE_KEYWORDS, EDGE_CASE_KEYWORDS, 
    RAPID_SEARCH_SEQUENCE, BRAND_KEYWORDS, COMBINED_KEYWORDS,
    FOREIGN_LANGUAGE_KEYWORDS, EMOJI_SPECIAL_KEYWORDS,
    REPEATED_SEARCH_TERM, REPEATED_SEARCH_COUNT,
    get_search_payload, get_random_brand_keyword,
    get_random_combined_keyword, get_random_foreign_language_keyword,
    get_random_emoji_special_keyword
)


class SearchTasks(TaskSet):
    """
    Task set for n11.com search functionality testing
    Contains various search scenarios with different user behaviors
    """
    
    def on_start(self):
        """Called when a user starts - simulate visiting homepage first"""
        headers = get_request_headers()
        
        scraper = get_scraper()
        
        try:
            time.sleep(random.uniform(2.0, 4.0))
            
            print("🔍 Visiting n11.com homepage with CloudScraper...")
            base_url = self.user.host if hasattr(self.user, 'host') else "https://www.n11.com"
            response = scraper.get(f"{base_url}/")
            
            if response and response.status_code == 200:
                print("✅ Successfully accessed homepage!")
                
                try:
                    self.user.environment.events.request.fire(
                        request_type="GET",
                        name="/",
                        response_time=response.elapsed.total_seconds() * 1000,
                        response_length=len(response.content),
                        response=response,
                        context={},
                        exception=None,
                    )
                except AttributeError:
                    print("ℹ️ Locust stats tracking is disabled in this mode")
            else:
                status_code = response.status_code if response else "No response"
                print(f"❌ Failed to access homepage: {status_code}")
                
                try:
                    self.user.environment.events.request.fire(
                        request_type="GET",
                        name="/",
                        response_time=0,
                        response_length=0,
                        exception=Exception(f"Failed with status: {status_code}"),
                        context={},
                    )
                except AttributeError:
                    print("ℹ️ Locust stats tracking is disabled in this mode")
        except Exception as e:
            print(f"❌ Exception during homepage access: {str(e)}")
            
            try:
                self.user.environment.events.request.fire(
                    request_type="GET",
                    name="/",
                    response_time=0,
                    response_length=0,
                    exception=e,
                    context={},
                )
            except AttributeError:
                print("ℹ️ Locust stats tracking is disabled in this mode")
    
    def _perform_search(self, keyword, task_name):
        """
        Helper method to perform search with consistent behavior
        """
        scraper = get_scraper()
        
        time.sleep(random.uniform(1.0, 2.5))
        
        base_url = self.user.host if hasattr(self.user, 'host') else "https://www.n11.com"
        
        search_url = f"{base_url}/arama?q={keyword}"
        
        print(f"🔍 Searching for '{keyword}'...")
        
        try:
            start_time = time.time()
            response = scraper.get(search_url)
            response_time = (time.time() - start_time) * 1000  # ms
            
            if response and response.status_code == 200:
                print(f"✓ Search for '{keyword}' successful! Response time: {response_time:.2f}ms")
                
                try:
                    self.user.environment.events.request.fire(
                        request_type="GET",
                        name=task_name,
                        response_time=response_time,
                        response_length=len(response.content),
                        response=response,
                        context={},
                        exception=None,
                    )
                except AttributeError:
                    print("ℹ️ Locust stats tracking is disabled in this mode")
                
                return True
            else:
                status_code = response.status_code if response else "No response"
                print(f"❌ Search for '{keyword}' failed: {status_code}")
                
                try:
                    self.user.environment.events.request.fire(
                        request_type="GET",
                        name=task_name,
                        response_time=response_time,
                        response_length=0,
                        exception=Exception(f"Failed with status: {status_code}"),
                        context={},
                    )
                except AttributeError:
                    print("ℹ️ Locust stats tracking is disabled in this mode")
                
                return False
                
        except Exception as e:
            print(f"❌ Exception during search for '{keyword}': {str(e)}")
            
            try:
                self.user.environment.events.request.fire(
                    request_type="GET",
                    name=task_name,
                    response_time=0,
                    response_length=0,
                    exception=e,
                    context={},
                )
            except AttributeError:
                print("ℹ️ Locust stats tracking is disabled in this mode")
            
            return False
    
    @task(3)
    def search_popular_keywords(self):
        """
        Positive Test: Search with popular keywords
        Weight: 3 (most common user behavior)
        """
        keyword = random.choice(POSITIVE_KEYWORDS)
        self._perform_search(keyword, "Search: Popular Keywords")
    
    @task(2)
    def search_medium_length_keyword(self):
        """
        Positive Test: Search with medium-length keyword
        Weight: 2
        """
        self._perform_search("kitap", "Search: Medium Length")
    
    @task(1)
    def search_empty_query(self):
        """
        Negative Test: Empty query search
        Weight: 1 (less common but important edge case)
        """
        self._perform_search("", "Search: Empty Query")
    
    @task(1)
    def search_invalid_characters(self):
        """
        Negative Test: Search with invalid/special characters
        Weight: 1
        """
        self._perform_search("!@#$%^&*()", "Search: Invalid Characters")
    
    @task(1)
    def search_long_query(self):
        """
        Negative Test: Search with 200+ character query
        Weight: 1
        """
        long_query = "a" * 201  # 201 characters
        self._perform_search(long_query, "Search: Long Query")
    
    @task(1)
    def search_single_character(self):
        """
        Edge Case: Single character search
        Weight: 1
        """
        self._perform_search("a", "Search: Single Character")
    
    @task(1)
    def rapid_consecutive_searches(self):
        """
        Edge Case: Perform 3 rapid consecutive searches
        Measures response times under rapid fire conditions
        Weight: 1
        """
        print("🚀 Starting rapid consecutive searches...")
        
        time.sleep(random.uniform(3.0, 5.0))
        
        results = []
        for i, keyword in enumerate(RAPID_SEARCH_SEQUENCE):
            result = self._perform_search(keyword, f"Rapid Search {i+1}: {keyword}")
            results.append(result)
            
            time.sleep(random.uniform(1.0, 1.5))
        
        success_count = sum(1 for r in results if r)
        print(f"📊 Rapid search sequence complete: {success_count}/3 successful")
    
    @task(1)
    def search_with_turkish_characters(self):
        """
        Edge Case: Search with Turkish characters
        Weight: 1
        """
        turkish_keywords = ["çanta", "şampuan", "ğitar", "öğrenci", "üzüm"]
        keyword = random.choice(turkish_keywords)
        self._perform_search(keyword, "Search: Turkish Characters")
    
    @task(1)
    def search_numbers_only(self):
        """
        Edge Case: Search with numbers only
        Weight: 1
        """
        self._perform_search("123456789", "Search: Numbers Only")
        
    @task(2)
    def search_brand_keywords(self):
        """
        Test: Brand-based search
        Searches using popular brand names to test brand-specific results
        Weight: 2
        """
        keyword = get_random_brand_keyword()
        success = self._perform_search(keyword, "Search: Brand Keyword")
        
        if success:
            time.sleep(random.uniform(2.0, 3.0))
    
    @task(2)
    def search_with_filtering(self):
        """
        Test: Search with post-search filtering
        Performs search then applies filters to results
        Weight: 2
        """
        keyword = get_random_brand_keyword()
        success = self._perform_search(keyword, "Search: Pre-Filter")
        
        if success:
            try:
                # Add delay to simulate user looking at results
                time.sleep(random.uniform(3.0, 5.0))

                base_url = self.user.host if hasattr(self.user, 'host') else "https://www.n11.com"
                
                filter_params = {
                    'q': keyword,
                    'minp': '1000',
                    'maxp': '2000'
                }
                
                filter_url = f"{base_url}/arama?" + urlencode(filter_params)
                
                scraper = get_scraper()
                print(f"🔍 Applying price filter to '{keyword}' search results...")
                
                start_time = time.time()
                response = scraper.get(filter_url)
                response_time = (time.time() - start_time) * 1000  # ms
                
                if response and response.status_code == 200:
                    print(f"✓ Filter applied successfully! Response time: {response_time:.2f}ms")
                    
                    try:
                        self.user.environment.events.request.fire(
                            request_type="GET",
                            name="Search: Apply Price Filter",
                            response_time=response_time,
                            response_length=len(response.content),
                            response=response,
                            context={},
                            exception=None,
                        )
                    except AttributeError:
                        print("ℹ️ Locust stats tracking is disabled in this mode")
                else:
                    status_code = response.status_code if response else "No response"
                    print(f"❌ Filter application failed: {status_code}")
                    
                    try:
                        self.user.environment.events.request.fire(
                            request_type="GET",
                            name="Search: Apply Price Filter",
                            response_time=response_time,
                            response_length=0,
                            exception=Exception(f"Failed with status: {status_code}"),
                            context={},
                        )
                    except AttributeError:
                        print("ℹ️ Locust stats tracking is disabled in this mode")
            
            except Exception as e:
                print(f"❌ Exception during filter application: {str(e)}")
                
                try:
                    self.user.environment.events.request.fire(
                        request_type="GET",
                        name="Search: Apply Price Filter",
                        response_time=0,
                        response_length=0,
                        exception=e,
                        context={},
                    )
                except AttributeError:
                    print("ℹ️ Locust stats tracking is disabled in this mode")
    
    @task(2)
    def search_word_combinations(self):
        """
        Test: Search with word combinations (multi-word queries)
        Tests how the search handles phrases and multiple keywords
        Weight: 2
        """
        keyword = get_random_combined_keyword()
        self._perform_search(keyword, "Search: Word Combination")
    
    @task(1)
    def search_foreign_language(self):
        """
        Test: Search with foreign language terms
        Tests how the search handles non-Turkish queries
        Weight: 1
        """
        keyword = get_random_foreign_language_keyword()
        self._perform_search(keyword, "Search: Foreign Language")
    
    @task(1)
    def search_emoji_special_chars(self):
        """
        Test: Search with emoji and special characters
        Tests how the search handles modern text with emojis and special chars
        Weight: 1
        """
        keyword = get_random_emoji_special_keyword()
        self._perform_search(keyword, "Search: Emoji & Special Chars")
    
    @task(1)
    def repeated_rapid_searches(self):
        """
        Test: Multiple rapid repeated searches with the same term
        Tests system response to repeated identical queries in rapid succession
        Weight: 1
        """
        print(f"🔄 Starting {REPEATED_SEARCH_COUNT} rapid repeated searches for '{REPEATED_SEARCH_TERM}'...")
        
        time.sleep(random.uniform(3.0, 5.0))
        
        success_count = 0
        response_times = []
        
        for i in range(REPEATED_SEARCH_COUNT):
            task_name = f"Repeated Search {i+1}/{REPEATED_SEARCH_COUNT}: {REPEATED_SEARCH_TERM}"
            
            start_time = time.time()
            result = self._perform_search(REPEATED_SEARCH_TERM, task_name)
            search_time = (time.time() - start_time) * 1000  # ms
            
            if result:
                success_count += 1
                response_times.append(search_time)
            
            time.sleep(random.uniform(0.5, 1.0))
        
        if response_times:
            avg_time = sum(response_times) / len(response_times)
            min_time = min(response_times)
            max_time = max(response_times)
            
            print(f"📊 Repeated search complete: {success_count}/{REPEATED_SEARCH_COUNT} successful")
            print(f"📈 Response times - Avg: {avg_time:.2f}ms, Min: {min_time:.2f}ms, Max: {max_time:.2f}ms")
            
            if len(response_times) >= 2:
                first_half = response_times[:len(response_times)//2]
                second_half = response_times[len(response_times)//2:]
                
                first_half_avg = sum(first_half) / len(first_half)
                second_half_avg = sum(second_half) / len(second_half)
                
                change = ((second_half_avg - first_half_avg) / first_half_avg) * 100
                
                if change > 10:
                    print(f"⚠️ Performance degradation detected: +{change:.2f}% slower in second half of requests")
                elif change < -10:
                    print(f"✅ Performance improvement detected: {change:.2f}% faster in second half of requests")
                else:
                    print(f"✓ Consistent performance: {change:.2f}% change between first and second half")
        else:
            print("❌ All repeated searches failed. Cannot calculate statistics.")
