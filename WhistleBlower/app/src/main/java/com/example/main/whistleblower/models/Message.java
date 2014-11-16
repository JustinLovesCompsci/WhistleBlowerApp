package com.example.main.whistleblower.models;

/**
 * Created by Main on 11/15/14.
 */
public class Message {

    private String type;
    private String subType;
    private String message;
    private String category;
    private MessageAddress messageAddress;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubType() {
        return subType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public class MessageAddress {
        private String address;
        private String city;
        private String state;

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

    }
}
