package common.data;
import java.io.Serializable;

public class Address implements Serializable{
    private static final long serialVersionUID = 1L;

    private String street;

    private String zipCode;

    private Location town;

    public Address(String street, String zipCode, Location town) {
        setStreet(street);
        setZipCode(zipCode);
        setTown(town);
    }

    public Address() {
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Location getTown() {
        return town;
    }

    public void setStreet(String street) {
        if (street == null || street.length() > 61) {
            throw new IllegalArgumentException("Улица не может быть null или иметь длину больше 61");
        }
        this.street = street;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setTown(Location town) {
        this.town = town;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s", street, zipCode, town);
    }
}