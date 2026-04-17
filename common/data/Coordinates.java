package common.data;
import java.io.Serializable;

public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x;

    private Long y;

    public Coordinates(int x, Long y) {
        setX(x);
        setY(y);
    }

    public Coordinates() {

    }
    public void setX(int x) {
        this.x = x;
    }

    public void setY(Long y) {
        if (y == null) {
            throw new IllegalArgumentException("Аргумент Y не может быть null");
        }
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public Long getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}