"""
Cloudscraper utility for bypassing Cloudflare protection
Provides session management and better browser simulation
"""

import cloudscraper
import random
import time
from utils.headers import get_request_headers

class CloudScraperSession:
    """
    Manages cloudscraper session for better Cloudflare bypass
    """
    
    def __init__(self):
        """Initialize a cloudscraper session with browser-like attributes"""
        self.scraper = self._create_scraper()
        
    def _create_scraper(self):
        """
        Create a cloudscraper instance with browser fingerprinting
        """
        browser_list = [
            {'browser': 'chrome', 'platform': 'darwin', 'desktop': True},
            {'browser': 'firefox', 'platform': 'darwin', 'desktop': True},
            {'browser': 'chrome', 'platform': 'windows', 'desktop': True},
            {'browser': 'firefox', 'platform': 'windows', 'desktop': True}
        ]
        
        browser = random.choice(browser_list)
        
        # Create scraper with browser fingerprinting
        scraper = cloudscraper.create_scraper(
            browser=browser,
            delay=5,  # Add delay between requests
            captcha={"provider": "return_response"}
        )
        
        # Add headers from our headers.py
        headers = get_request_headers()
        for key, value in headers.items():
            scraper.headers[key] = value
            
        return scraper
        
    def get(self, url, params=None, **kwargs):
        """
        Perform GET request with rate limiting and retry logic
        """
        # Add jitter for more natural behavior
        jitter = random.uniform(1.0, 3.0)
        time.sleep(jitter)
        
        # Ensure URL is absolute
        if not url.startswith('http'):
            url = f"https://www.n11.com{'' if url.startswith('/') else '/'}{url}"
        
        # Log request attempt
        print(f"🌐 Requesting: {url} with CloudScraper")
        
        try:
            # Perform request with cloudscraper
            response = self.scraper.get(url, params=params, **kwargs)
            
            # Log status code
            print(f"📡 Response status: {response.status_code}")
            
            return response
            
        except Exception as e:
            print(f"❌ CloudScraper request failed: {str(e)}")
            return None

# Create a singleton instance for reuse
scraper_session = CloudScraperSession()

def get_scraper():
    """Get the singleton CloudScraperSession instance"""
    return scraper_session
