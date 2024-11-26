package backend.academy.analyzer.enums;

import lombok.Getter;

@Getter
public enum Param {
    PATH("--path"),
    FROM("--from"),
    TO("--to"),
    FORMAT("--format"),
    FILTER_FIELD("--filter-field"),
    FILTER_VALUE("--filter-value");
    private final String symbol;

    Param(String symbol) {
        this.symbol = symbol;
    }
}
