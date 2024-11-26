package backend.academy.analyzer.enums;

import lombok.Getter;

@Getter
public enum ResponseCode {
    OK(200, "OK"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    NOT_MODIFIED(304, "Not Modified"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    PARTIAL_CONTENT(206, "Partial Content"),
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
    UNKNOWN(-1, "Unknown");
    private final int code;
    private final String description;

    ResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
