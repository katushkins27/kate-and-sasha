package common.data;
import java.io.Serializable;

public enum TicketType implements Serializable{

    VIP("VIP"),

    USUAL("USUAL"),

    BUDGETARY("BUDGETARY"),

    CHEAP("CHEAP");

    private final String description;
    TicketType(String description) {
        this.description = description;
    }

    public static String AllDescriptions() {
        StringBuilder str = new StringBuilder();
        for (TicketType type : TicketType.values()) {
            str.append(type.name()).append(", ");
        }
        // Удаляем последние два символа (запятую и пробел)
        return str.substring(0, str.length() - 2);
    }
}