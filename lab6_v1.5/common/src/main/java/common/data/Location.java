package common.data;
import java.io.Serializable;

public class Location implements Serializable{
    private static final long serialVersionUID = 1L;

    private double x;

    private Long y;

    private Float z;

    private String name;

    public Location(String name, double x, Long y, Float z) {
        setName(name);
        setX(x);
        setY(y);
        setZ(z);
    }

    public Location() {

    }

    public double getX() {
        return x;
    }

    public Long getY() {
        return y;
    }

    public Float getZ() {
        return z;
    }

    public String getName() {
        return name;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(Long y) {
        if (y == null) {
            throw new IllegalArgumentException("Аргумент Y не может быть null");
        }
        this.y = y;
    }

    public void setZ(Float z) {
        if (z == null) {
            throw new IllegalArgumentException("Аргумент Z не может быть null");
        }
        this.z = z;
    }

    public void setName(String name) {
        if (name != null && name.length() > 777) {
            throw new IllegalArgumentException("Длина имени не может превышать 777");
        }
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s, %f, %d, %f", name, x, y, z);
    }
}