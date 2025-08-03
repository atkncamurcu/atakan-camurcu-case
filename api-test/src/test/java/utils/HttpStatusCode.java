package utils;

/**
 * HTTP Status Code enum for API testing
 * Contains commonly used HTTP status codes with descriptive names
 */
public enum HttpStatusCode {
    
    // Success codes (2xx)
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    
    // Client Error codes (4xx)
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(409, "Conflict"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    
    // Server Error codes (5xx)
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout");
    
    private final int code;
    private final String description;
    
    HttpStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return code + " " + description;
    }
    
    /**
     * Get HttpStatusCode enum by status code number
     * @param code HTTP status code number
     * @return HttpStatusCode enum or null if not found
     */
    public static HttpStatusCode fromCode(int code) {
        for (HttpStatusCode status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * Check if status code indicates success (2xx)
     * @return true if status code is in 2xx range
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }
    
    /**
     * Check if status code indicates client error (4xx)
     * @return true if status code is in 4xx range
     */
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }
    
    /**
     * Check if status code indicates server error (5xx)
     * @return true if status code is in 5xx range
     */
    public boolean isServerError() {
        return code >= 500 && code < 600;
    }
}
