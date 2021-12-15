package com.example.adoption_pet.model;

public class User {
    private String fullName;
    private String email;
    private String userImgeId;
    private String id;
    private String dob;
    private String phoneNumber;
    private String userGender;
    private String city;
    private String district;
    private String userAddress;
    private double longitude;
    private double latitude;
    private String zipCode;

    public User() {
    }

    public User(String fullName, String email, String userImgeId, String id, String dob,
                String phoneNumber, String userGender, String city, String district,
                String userAddress, double longitude, double latitude, String zipCode) {
        this.fullName = fullName;
        this.email = email;
        this.userImgeId = userImgeId;
        this.id = id;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
        this.userGender = userGender;
        this.city = city;
        this.district = district;
        this.userAddress = userAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.zipCode = zipCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserImgeId() {
        return userImgeId;
    }

    public void setUserImgeId(String userImgeId) {
        this.userImgeId = userImgeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", userImgeId='" + userImgeId + '\'' +
                ", id='" + id + '\'' +
                ", dob='" + dob + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userGender='" + userGender + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
