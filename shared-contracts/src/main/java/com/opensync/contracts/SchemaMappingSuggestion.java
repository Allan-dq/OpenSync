package com.opensync.contracts;

import java.util.List;

public record SchemaMappingSuggestion(List<FieldMapping> mappings, double confidence, List<String> warnings) {
    public record FieldMapping(String sourcePath, String targetField, TransformationType transformation) { }
}
