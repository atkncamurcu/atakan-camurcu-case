"""
Headers utility module for n11.com search testing
Simplified version without cookies and with minimal headers
"""

import random

# List of minimal user agents
USER_AGENTS = [
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36'
]

def get_request_headers():
    """
    Returns minimal headers for testing n11.com without cookies
    """
    # Select a random user agent
    user_agent = random.choice(USER_AGENTS)
    
    # Minimal headers with no cookies
    return {
        'User-Agent': user_agent,
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
        'Accept-Language': 'en-US,en;q=0.5'
    }

def get_ajax_headers():
    """
    Returns minimal headers for AJAX requests without cookies
    """
    # Base headers from regular request
    headers = get_request_headers()
    
    # Modify for AJAX
    headers.update({
        'Accept': 'application/json, text/javascript, */*; q=0.01',
        'X-Requested-With': 'XMLHttpRequest'
    })
    
    return headers
