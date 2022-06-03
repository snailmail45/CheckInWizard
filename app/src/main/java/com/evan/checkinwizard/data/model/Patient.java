package com.evan.checkinwizard.data.model;

public class Patient {

    private String name;
    private String dateOfBirth;
    private String height;
    private String weight;
    private String ethnicity;
    private String phoneNumber;
    private String email;
    private String emergencyContact;

    public static class Builder {

        private String name;
        private String dateOfBirth;
        private String height;
        private String weight;
        private String ethnicity;
        private String phoneNumber;
        private String email;
        private String emergencyContact;

        public Builder() {
        }

        Builder(String name, String dateOfBirth, String height, String weight, String ethnicity,
                String phoneNumber, String email, String emergencyContact) {
            this.name = name;
            this.dateOfBirth = dateOfBirth;
            this.height = height;
            this.weight = weight;
            this.ethnicity = ethnicity;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.emergencyContact = emergencyContact;
        }

        public Builder name(String name) {
            this.name = name;
            return Builder.this;
        }

        public Builder dateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return Builder.this;
        }

        public Builder height(String height) {
            this.height = height;
            return Builder.this;
        }

        public Builder weight(String weight) {
            this.weight = weight;
            return Builder.this;
        }

        public Builder ethnicity(String ethnicity) {
            this.ethnicity = ethnicity;
            return Builder.this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return Builder.this;
        }

        public Builder email(String email) {
            this.email = email;
            return Builder.this;
        }

        public Builder emergencyContact(String emergencyContact) {
            this.emergencyContact = emergencyContact;
            return Builder.this;
        }

        public Patient build() {
            return new Patient(this);
        }
    }

    private Patient(Builder builder) {
        this.name = builder.name;
        this.dateOfBirth = builder.dateOfBirth;
        this.height = builder.height;
        this.weight = builder.weight;
        this.ethnicity = builder.ethnicity;
        this.phoneNumber = builder.phoneNumber;
        this.email = builder.email;
        this.emergencyContact = builder.emergencyContact;
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }
}
