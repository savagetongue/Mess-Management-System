// Suggestion.java
package com.example.myapplication;

import java.io.Serializable;

public class Suggestion implements Serializable {

    private String suggestionText;
    private String date;

    public Suggestion(String suggestionText, String date) {
        this.suggestionText = suggestionText;
        this.date = date;
    }

    public String getSuggestionText() {
        return suggestionText;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Suggestion: " + suggestionText + "\nDate: " + date;
    }
}
