package com.example.adoption_pet.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Pet implements Parcelable {

    private String petName;
    private String petId;
    private String petType;
    private String breed;
    private String petImg;
    private String userId;
    private String dob;
    private String gender;
    private String steriStatus;
    private String description;
    private String provinces;
    private String district;
    private String address;
    private String zipCode;
    private double latitude;
    private double longitude;
    private String petSize;
    private String petAger;
    private long timestamp;
    private float energyLevel;
    private float activityLevel;
    private float playfulnessLevel;
    private float affectionLevel;
    private float trainingLevel;


    //Empty constructor
    public Pet() {
    }

    public Pet(String petName, String petId, String petType, String breed, String petImg, String userId, String dob, String gender, String steriStatus, String description, String provinces, String district, String address, String zipCode, double latitude, double longitude, String petSize, String petAger, long timestamp, float energyLevel, float activityLevel, float playfulnessLevel, float affectionLevel, float trainingLevel) {
        this.petName = petName;
        this.petId = petId;
        this.petType = petType;
        this.breed = breed;
        this.petImg = petImg;
        this.userId = userId;
        this.dob = dob;
        this.gender = gender;
        this.steriStatus = steriStatus;
        this.description = description;
        this.provinces = provinces;
        this.district = district;
        this.address = address;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.petSize = petSize;
        this.petAger = petAger;
        this.timestamp = timestamp;
        this.energyLevel = energyLevel;
        this.activityLevel = activityLevel;
        this.playfulnessLevel = playfulnessLevel;
        this.affectionLevel = affectionLevel;
        this.trainingLevel = trainingLevel;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getPetImg() {
        return petImg;
    }

    public void setPetImg(String petImg) {
        this.petImg = petImg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSteriStatus() {
        return steriStatus;
    }

    public void setSteriStatus(String steriStatus) {
        this.steriStatus = steriStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvinces() {
        return provinces;
    }

    public void setProvinces(String provinces) {
        this.provinces = provinces;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPetSize() {
        return petSize;
    }

    public void setPetSize(String petSize) {
        this.petSize = petSize;
    }

    public String getPetAger() {
        return petAger;
    }

    public void setPetAger(String petAger) {
        this.petAger = petAger;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(float energyLevel) {
        this.energyLevel = energyLevel;
    }

    public float getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(float activityLevel) {
        this.activityLevel = activityLevel;
    }

    public float getPlayfulnessLevel() {
        return playfulnessLevel;
    }

    public void setPlayfulnessLevel(float playfulnessLevel) {
        this.playfulnessLevel = playfulnessLevel;
    }

    public float getAffectionLevel() {
        return affectionLevel;
    }

    public void setAffectionLevel(float affectionLevel) {
        this.affectionLevel = affectionLevel;
    }

    public float getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(float trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    protected Pet(Parcel in) {
        super();
        readFromParcel(in);

    }

    public void readFromParcel(Parcel in){

        petName = in.readString();
        petId = in.readString();
        petType = in.readString();
        breed = in.readString();
        petImg = in.readString();
        userId = in.readString();
        dob = in.readString();
        gender = in.readString();
        steriStatus = in.readString();
        description = in.readString();
        provinces = in.readString();
        district = in.readString();
        address = in.readString();
        zipCode = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        petSize = in.readString();
        petAger = in.readString();
        timestamp = in.readLong();
        energyLevel = in.readFloat();
        activityLevel = in.readFloat();
        playfulnessLevel = in.readFloat();
        affectionLevel = in.readFloat();
        trainingLevel = in.readFloat();

    }

    public static final Creator<Pet> CREATOR = new Creator<Pet>() {
        @Override
        public Pet createFromParcel(Parcel in) {
            return new Pet(in);
        }

        @Override
        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(petName);
        dest.writeString(petId);
        dest.writeString(petType);
        dest.writeString(breed);
        dest.writeString(petImg);
        dest.writeString(userId);
        dest.writeString(dob);
        dest.writeString(gender);
        dest.writeString(steriStatus);
        dest.writeString(description);
        dest.writeString(provinces);
        dest.writeString(district);
        dest.writeString(address);
        dest.writeString(zipCode);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(petSize);
        dest.writeString(petAger);
        dest.writeLong(timestamp);
        dest.writeFloat(energyLevel);
        dest.writeFloat(activityLevel);
        dest.writeFloat(playfulnessLevel);
        dest.writeFloat(affectionLevel);
        dest.writeFloat(trainingLevel);
    }
}