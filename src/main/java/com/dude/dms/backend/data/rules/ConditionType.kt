package com.dude.dms.backend.data.rules

enum class ConditionType(val translationKey: String) {
    AND("and"),
    OR("or"),
    EQUALS("equals"),
    CONTAINS("contains"),
    PREVIOUS_EQUALS("equals.prev"),
    PREVIOUS_CONTAINS("contains.prev"),
    NEXT_EQUALS("equals.next"),
    NEXT_CONTAINS("contains.next"),
    LINE_CONTAINS_WORD("line.contains.word"),
    LINE_CONTAINS_TEXT("line.contains.text")
}