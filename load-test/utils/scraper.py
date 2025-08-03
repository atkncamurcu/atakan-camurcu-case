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
        
        scraper = cloudscraper.create_scraper(
            browser=browser,
            delay=5,
            captcha={"provider": "return_response"}
        )
        
        headers = get_request_headers()
        for key, value in headers.items():
            scraper.headers[key] = value
            
        return scraper
        
    def get(self, url, params=None, **kwargs):
        """
        Perform GET request with rate limiting and retry logic
        """
        jitter = random.uniform(1.0, 3.0)
        time.sleep(jitter)
        
        if not url.startswith('http'):
            url = f"https://www.n11.com{'' if url.startswith('/') else '/'}{url}"
        
        print(f"🌐 Requesting: {url} with CloudScraper")
        
        try:
            response = self.scraper.get(url, params=params, **kwargs)
            
            print(f"📡 Response status: {response.status_code}")
            
            return response
            
        except Exception as e:
            print(f"❌ CloudScraper request failed: {str(e)}")
            return None

scraper_session = CloudScraperSession()

def get_scraper():
    """Get the singleton CloudScraperSession instance"""
    return scraper_session
