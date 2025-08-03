"""
N11.com Header Search Module Performance Test
============================================

Locust performance testing project for n11.com search functionality
Tests various scenarios including positive, negative, and edge cases
with realistic browser simulation and Cloudflare bypass.
"""

from locust import HttpUser, between, events
from tasks.search_tasks import SearchTasks
import os
import random
import time
import logging


# Configure logging
logging.basicConfig(level=logging.INFO, 
                   format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger("n11-performance-test")


@events.init_command_line_parser.add_listener
def init_parser(parser):
    """Add command line options for this locust script"""
    parser.add_argument(
        '--debug-mode',
        dest='debug_mode',
        action='store_true',
        help="Enable debug mode for more verbose output"
    )


@events.init.add_listener
def on_locust_init(environment, **kwargs):
    """Initialize the locust environment"""
    if environment.parsed_options and hasattr(environment.parsed_options, "debug_mode") and environment.parsed_options.debug_mode:
        logger.setLevel(logging.DEBUG)
        logger.debug("Debug mode enabled")
    
    logger.info("Initializing n11.com search test...")


class N11SearchUser(HttpUser):
    """
    Simulates a single user testing n11.com search functionality
    Includes realistic wait times and browser behavior simulation
    using CloudScraper to bypass Cloudflare protections
    """

    tasks = [SearchTasks]

    wait_time = between(2, 5)
    
    weight = 1
    
    def on_start(self):
        """
        Called when user starts testing session
        Sets up initial state and logs user session start
        """
        self.session_id = f"user_{random.randint(1000, 9999)}"
        
        self.start_time = time.time()
        self.requests_made = 0
        
        time.sleep(random.uniform(0.1, 1.0))
        
        logger.info(f"🔍 N11 Search User {self.session_id} started - Beginning search behavior simulation")
        logger.info(f"🎯 Target: {self.host} header search module")
        logger.info(f"📊 Test scenarios: Positive, Negative, Edge cases")
        logger.info("-" * 60)
    
    def on_stop(self):
        """
        Called when user stops testing session
        Cleanup and final logging
        """
        session_duration = time.time() - self.start_time
        rps = self.requests_made / session_duration if session_duration > 0 else 0
        
        logger.info("-" * 60)
        logger.info(f"✅ N11 Search User {self.session_id} session completed")
        logger.info(f"📊 Session duration: {session_duration:.2f}s, Requests: {self.requests_made}, RPS: {rps:.2f}")
        logger.info(f"📈 Check Locust web UI for detailed performance metrics")


class N11PowerUser(HttpUser):
    """
    Power user simulation - more aggressive search patterns
    Currently not active, but can be enabled for mixed user testing
    """
    
    tasks = [SearchTasks]
    wait_time = between(0.5, 1.5)
    weight = 0
    
    def on_start(self):
        logger.info("⚡ N11 Power User started - Aggressive search patterns")


# Configuration for running the test
if __name__ == "__main__":
    """
    Entry point when running with: python locustfile.py
    Provides usage instructions
    """
    print("""
    🚀 N11.com Search Performance Test with Cloudflare Bypass
    =======================================================
    
    To run this test, use one of the following commands:
    
    1. Web UI Mode (Recommended):
       locust -f locustfile.py --host=https://www.n11.com
    
    2. Headless Mode (1 user, 30 seconds, with 3 second spawn rate):
       locust -f locustfile.py --host=https://www.n11.com --users 1 --spawn-rate 1 --run-time 30s --headless
    
    3. Debug Mode (with more verbose output):
       locust -f locustfile.py --host=https://www.n11.com --debug-mode
    
    4. Extended Test (1 user, 120 seconds):
       locust -f locustfile.py --host=https://www.n11.com --users 1 --spawn-rate 1 --run-time 120s --headless
    
    📊 Test includes:
    - CloudScraper integration for Cloudflare bypass
    - Minimal headers implementation (no cookies required)
    - Rate limiting and request throttling
    - Popular keyword searches (telefon, bilgisayar, kitap)
    - Empty query handling
    - Invalid character handling  
    - Long query testing (200+ chars)
    - Single character searches
    - Rapid consecutive searches
    - Turkish character support
    - Numbers-only searches
    
    🌐 Open http://localhost:8089 after starting to view real-time results
    """)
