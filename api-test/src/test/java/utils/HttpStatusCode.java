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
}
