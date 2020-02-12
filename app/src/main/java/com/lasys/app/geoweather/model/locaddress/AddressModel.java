
package com.lasys.app.geoweather.model.locaddress;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressModel {

    @SerializedName("addressLine")
    @Expose
    private String addressLine;

    @SerializedName("feature")
    @Expose
    private String feature;

    @SerializedName("admin")
    @Expose
    private String admin;

    @SerializedName("locality")
    @Expose
    private String locality;

    @SerializedName("postalCode")
    @Expose
    private String postalCode;

    @SerializedName("countryName")
    @Expose
    private String countryName;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;



    @SerializedName("sid")
    @Expose
    private int sid;

    /**
     * No args constructor for use in serialization
     */
    public AddressModel()
    {

    }


    /**
     *
     * @param countryName
     * @param postalCode
     * @param addressLine  //complete address
     * @param admin        //State
     * @param locality     //Town
     * @param longitude
     * @param feature     //House Address
     * @param latitude
     */
    public AddressModel(String addressLine, String feature, String admin, String locality, String postalCode, String countryName, String latitude, String longitude) {
        super();
        this.addressLine = addressLine;
        this.feature = feature;
        this.admin = admin;
        this.locality = locality;
        this.postalCode = postalCode;
        this.countryName = countryName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }
    public String getAddressLine() {
        return addressLine;
    }

    public void setAddress(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
