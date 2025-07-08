package com.medhir.rest.sales.model;

public enum FormType {
    CONVERTED("CONVERTED", "ConvertLeadModal", "Opens ConvertLeadModal - Collects conversion data (final quotation, signup amount, payment details, etc.)"),
    JUNK("JUNK", "JunkReasonModal", "Opens JunkReasonModal - Collects reason for marking lead as junk"),
    LOST("LOST", "LostLeadModal", "Opens LostLeadModal - Collects reason for marking lead as lost"),
    ONBOARDING("ONBOARDING", "OnboardingForm", "For onboarding forms - Generic onboarding form type"),
    APPROVAL("APPROVAL", "ApprovalForm", "For approval forms - Generic approval form type"),
    CUSTOM("CUSTOM", "CustomForm", "For custom forms - Generic custom form type");

    private final String value;
    private final String modalName;
    private final String description;

    FormType(String value, String modalName, String description) {
        this.value = value;
        this.modalName = modalName;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getModalName() {
        return modalName;
    }

    public String getDescription() {
        return description;
    }

    public static FormType fromValue(String value) {
        for (FormType formType : FormType.values()) {
            if (formType.value.equals(value)) {
                return formType;
            }
        }
        throw new IllegalArgumentException("Unknown FormType: " + value);
    }
} 