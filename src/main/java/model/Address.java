package model;


public class Address {
    private int addressId;
    private String streetName;
    private int streetNumber;
    private String city;
    private String state;

    /*Constructors*/
    public Address(int addressId, String streetName, int streetNumber, String city, String state) {
        this.setAddressId(addressId);
        this.setStreetName(streetName);
        this.setCity(city);
        this.setState(state);
        this.setStreetNumber(streetNumber);
        this.setCity(city);
    }

    public Address(int addressId) {
        this.setAddressId(addressId);
    }

    /*Methods*/
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return getStreetName() + " " +getStreetNumber();
    }

}
