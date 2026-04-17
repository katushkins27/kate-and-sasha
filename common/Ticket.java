package common.data;
import java.io.Serializable;
import java.time.LocalDateTime;
import common.data.*;
import java.lang.Comparable;

public class Ticket implements Comparable<Ticket>, Serializable {
    private static final long serialVersionUID = 1L;

    private int id;

    private String name;

    private Coordinates coordinates;

    private java.time.LocalDateTime creationDate;

    private Long price;

    private TicketType type;

    private Venue venue;

    public Ticket(int id, String name, Coordinates coordinates, LocalDateTime creationDate,
                  Long price, TicketType type, Venue venue) {
        setID(id);
        setName(name);
        setCoordinates(coordinates);
        setCreationDate(creationDate);
        setPrice(price);
        setType(type);
        setVenue(venue);
    }

    public Ticket() {

    }

    public void setID(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть строго больше 0");
        }
        this.id = id;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Имя не может быть null");
        }
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты мест не могут быть null");
        }
        this.coordinates = coordinates;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        if (creationDate == null) {
            throw new IllegalArgumentException("Дата создания не может быть null");
        }
        this.creationDate = creationDate;
    }

    public void setPrice(Long price) {
        if (price != null && price <= 0) {
            throw new IllegalArgumentException("Стоимость должна быть выше 0");
        }
        this.price = price;
    }

    public void setType(TicketType type) {
        if (type == null) {
            throw new IllegalArgumentException("Тип не может быть null");
        }
        this.type = type;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Long getPrice() {
        return price;
    }

    public TicketType getType() {
        return type;
    }

    public Venue getVenue() {
        return venue;
    }

    @Override
    public int compareTo(Ticket other) {
        int nameCompare = this.name.compareTo(other.name);
        if (nameCompare != 0) return nameCompare;

        if (this.price == null && other.price == null) return 0;
        if (this.price == null) return -1;
        if (other.price == null) return 1;

        int priceCompare = Long.compare(this.price, other.price);
        if (priceCompare != 0) return priceCompare;
        return this.type.compareTo(other.type);
    }
    @Override
    public String toString() {
        return String.format("Ticket{id=%d, name='%s', coordinates=%s, creationDate=%s, " +
                        "price=%s, type=%s, venue=%s}", id, name, coordinates, creationDate,
                price, type, venue);
    }
}