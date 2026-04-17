package client;

import common.data.*;
import java.util.Scanner;
import java.time.LocalDateTime;

public class ConsoleReader {
    private static Scanner scanner = new Scanner(System.in);

    public static String readStr(String invitation) {
        System.out.println(invitation);
        return scanner.nextLine();
    }

    public static int readInt(String invitation) {
        while (true) {
            try {
                String input = readStr(invitation).trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка, необходимо ввести ЦЕЛОЕ число");
            }
        }
    }

    public static double readDouble(String invitation) {
        while (true) {
            try {
                String input = readStr(invitation).trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка, необходимо ввести число");
            }
        }
    }

    public static String readNotEmptyStr(String invitation) {
        while (true) {
            String input = readStr(invitation).trim();
            if (input.isEmpty()) {
                System.out.println("Это поле не может быть пустым");
            } else {
                return input;
            }
        }
    }

    public static Long readLongNotNull(String invitation) {
        while (true) {
            try {
                String input = readStr(invitation).trim();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Необходимо ввести число");
            }
        }
    }

    public static Float readFloatNotNull(String invitation) {
        while (true) {
            try {
                String input = readStr(invitation).trim();
                return Float.parseFloat(input);
            } catch (NumberFormatException e) {
                System.out.println("Необходимо ввести число");
            }
        }
    }

    public static String readStreet(String invitation) {
        while (true) {
            String input = readNotEmptyStr(invitation);
            if (input.length() > 61) {
                System.out.println("Длина не может быть больше 61 символа");
            } else {
                return input;
            }
        }
    }

    public static String readLocationName(String invitation) {
        while (true) {
            String input = readStr(invitation);
            if (input.isEmpty()) {
                return null;
            }
            if (input.length() > 777) {
                System.out.println("Длина не должна превышать 777 символов");
            } else {
                return input;
            }
        }
    }

    public static Long readPrice(String invitation) {
        while (true) {
            String input = readStr(invitation).trim();
            if (input.trim().isEmpty()) {
                return null;
            }
            try {
                long price = Long.parseLong(input.trim());
                if (price <= 0) {
                    System.out.println("Стоимость не может быть меньше 0");
                } else {
                    return price;
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число");
            }
        }
    }

    public static TicketType readTicketType() {
        while (true) {
            String input = readStr("Введите тип билета: ").trim().toUpperCase();
            try {
                return TicketType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка, неправильный тип билета");
            }
        }
    }

    public static Coordinates readCoordinates() {
        System.out.println("Введите координты мест");
        int x = readInt("Введите Х: ");
        Long y = readLongNotNull("Введите Y: ");
        return new Coordinates(x, y);
    }

    public static Location readLocation() {
        System.out.println("Введите местоположение:");
        double x = readDouble("Введите Х: ");
        Long y = readLongNotNull("Введите Y: ");
        Float z = readFloatNotNull("Введите Z: ");
        String name = readLocationName("Введите название города: ");
        return new Location(name, x, y, z);
    }

    private static boolean isNumber(String line) {
        if (line == null || line.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(line.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean readYN(String invitation) {
        while (true) {
            String input = readStr(invitation).trim().toLowerCase();
            if (input.equals("y") || input.equals("yes") || input.equals("да") || input.equals("д")) {
                return true;
            } else if (input.equals("n") || input.equals("no") || input.equals("нет") || input.equals("н")) {
                return false;
            } else {
                System.out.println("Ошибка: введите y или n");
            }
        }
    }

    public static Address readAddress() {
        System.out.println("Введите адресс");
        String street = readStreet("Введите улицу: ");
        String zipCode = readStr("Введите индекс: ").trim();
        if (zipCode.isEmpty()) zipCode = null;

        Location town = null;
        if (readYN("Добавить координаты и город? (y/n)")) {
                town = readLocation();
            }
        }
        return new Address(street, zipCode, town);
    }

    public static Venue readVenue() {
        if (!readYN("Добавить местоположение? (y/n)")) {
                return null;
            }
            System.out.println("Введите данные места проведения:");
            long id = client.util.CreateID.createVenueID();
            String name = readNotEmptyStr("Введите название: ");
            int capacity = readInt("Введите вместимость: ");
            while (capacity <= 0) {
                System.out.println("Вместимрсть должна быть больше 0");
                capacity = readInt("Введите вместимость: ");
            }
            Address address = readAddress();
            return new Venue(id, name, capacity, address);
        }
    }

    public static Ticket readTicket(String ticketName) {
        return readTicket();
        int id = client.util.CreateID.createTicketID();
        Coordinates coordinates = readCoordinates();
        LocalDateTime createDate = LocalDateTime.now();
        Long price = readPrice("Введите стоимость");
        TicketType type = readTicketType();
        Venue venue = readVenue();

        return new Ticket(id, ticketName, coordinates, createDate, price, type, venue);
    }

    public static Ticket readTicket() {
        System.out.println("Введите данные билета");
        int id = client.util.CreateID.createTicketID();
        String name = readNotEmptyStr("Введите название: ");
        Coordinates coordinates = readCoordinates();
        LocalDateTime createDate = LocalDateTime.now();
        Long price = readPrice("Введите стоимость");
        TicketType type = readTicketType();
        Venue venue = readVenue();

        return new Ticket(id, name, coordinates, createDate, price, type, venue);
    }
}