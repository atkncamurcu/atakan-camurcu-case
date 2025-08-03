"""
Headers utility module for n11.com search testing
Simplified version without cookies and with minimal headers
"""

import random

USER_AGENTS = [
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36'
]

def get_request_headers():
    """
    Returns minimal headers for testing n11.com without cookies
    """
    user_agent = random.choice(USER_AGENTS)
    
    return {
        'User-Agent': user_agent,
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
        'Accept-Language': 'en-US,en;q=0.5'
    }

def get_ajax_headers():
    """
    Returns minimal headers for AJAX requests without cookies
    """
    headers = get_request_headers()
    
    headers.update({
        'Accept': 'application/json, text/javascript, */*; q=0.01',
        'X-Requested-With': 'XMLHttpRequest'
    })
    
    return headers
