package common.data;
import java.lang.Comparable;
import java.io.Serializable;

public class Venue implements Comparable<Venue>, Serializable{
    private static final long serialVersionUID = 1L;

    private long id;

    private String name;

    private int capacity;

    private Address address;


    public Venue(long id, String name, int capacity, Address address){
        setID(id);
        setName(name);
        setCapacity(capacity);
        setAddress(address);
    }

    public Venue(){

    }


    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public Address getAddress() {
        return address;
    }

    public void setID(long id){
        this.id = id;
    }

    public void setIdWithValid(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть строго больше 0");
        }
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя не может быть null или пустым");
        }
        this.name = name;
    }

    public void setCapacity(int capacity){
        if (capacity<=0){
            throw new IllegalArgumentException("Вместимость не может быть меньше 0");
        }
        this.capacity = capacity;
    }

    public void setAddress(Address address){
        if (address == null){
            throw new IllegalArgumentException("Адрес не может быть null");
        }
        this.address = address;
    }

    @Override
    public int compareTo(Venue other) {
        int capacityCompare = Integer.compare(this.capacity, other.capacity);
        if (capacityCompare != 0) return capacityCompare;
        return this.name.compareTo(other.name);
    }

    @Override
    public String toString(){
        return String.format("%d, %s, %d, %s", id, name, capacity, address);
    }
}
