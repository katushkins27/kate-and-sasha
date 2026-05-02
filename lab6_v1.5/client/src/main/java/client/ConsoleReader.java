package client;

import common.data.*;

import java.util.Scanner;
import java.time.LocalDateTime;

public class ConsoleReader {
    /**
     * Сканер для интерактивного ввода с консоли
     */
    private static Scanner scanner = new Scanner(System.in);
    /**
     * Флаг, указывающий на режим выполнения скрипта
     */
    private static boolean scriptRegime = false;
    /**
     * Сканер для чтения из файла скрипта
     */
    private static Scanner scriptScanner = null;
    /**
     * Текущий номер строки в скрипте (для сообщений об ошибках)
     */
    private static int scriptLineNumber = 0;
    /**
     * Флаг, указывающий, был ли уже считан следующий символ методом peek
     */
    private static boolean hasPeeked = false;
    /**
     * Следующая строка, сохранённая при peek
     */
    private static String nextLine = null;

    /**
     * Переключает reader в режим выполнения скрипта.
     *
     * @param fileScanner сканер для чтения из файла скрипта
     */
    public static void setScriptRegime(Scanner fileScanner) {
        scriptRegime = true;
        scriptScanner = fileScanner;
        scriptLineNumber = 0;
        nextLine = null;
        hasPeeked = false;
    }

    /**
     * Переключает reader в интерактивный режим (ввод с консоли).
     */
    public static void setInteractiveRegime() {
        scriptRegime = false;
        scriptScanner = null;
        nextLine = null;
        scriptLineNumber = 0;
        hasPeeked = false;
    }

    /**
     * Проверяет, находится ли reader в режиме выполнения скрипта.
     *
     * @return true, если выполняется скрипт, иначе false
     */
    public static boolean isScriptRegime() {
        return scriptRegime;
    }

    /**
     * Проверяет, является ли строка командой (для определения границ данных в скрипте).
     *
     * @param line проверяемая строка
     * @return true, если строка содержит команду, иначе false
     */
    private static boolean commandFlag(String line) {
        if (line == null) return false;
        String str = line.trim().split("\\s+", 2)[0].toLowerCase();
        return str.equals("add") || str.equals("show") ||
                str.equals("update") || str.equals("remove_by_id") ||
                str.equals("remove_all_by_price") || str.equals("remove_any_by_type") ||
                str.equals("remove_greater") || str.equals("remove_head") ||
                str.equals("head") || str.equals("min_by_venue") ||
                str.equals("info") || str.equals("help") ||
                str.equals("clear") || str.equals("save") ||
                str.equals("execute_script") || str.equals("exit");
    }

    /**
     * Читает следующую строку из источника ввода с учётом peek-а.
     *
     * @return следующая строка или null, если достигнут конец ввода
     */
    private static String readLine() {
        if (hasPeeked) {
            hasPeeked = false;
            return nextLine;
        }
        if (scriptRegime && scriptScanner != null) {
            if (scriptScanner.hasNextLine()) {
                scriptLineNumber++;
                return scriptScanner.nextLine();
            }
            return null;
        }
        return scanner.nextLine();
    }

    /**
     * Просматривает следующую строку без её извлечения из потока.
     *
     * @return следующая строка или null, если строк больше нет
     */
    public static String peekNextLine() {
        if (!hasPeeked) {
            nextLine = readLine();
            hasPeeked = true;
        }
        return nextLine;
    }

    /**
     * Читает следующую команду из скрипта, пропуская пустые строки и комментарии.
     * <p>
     * Используется для построчного чтения команд при выполнении скрипта.
     * Комментарии и пустые строки игнорируются.
     * </p>
     *
     * @return строка с командой или null, если достигнут конец файла
     */
    public static String readNextCommand() {
        if (!scriptRegime || scriptScanner == null) return null;

        if (hasPeeked && nextLine != null) {
            String line = nextLine;
            hasPeeked = false;
            nextLine = null;
            return line;
        }

        String line;
        do {
            if (scriptScanner.hasNextLine()) {
                scriptLineNumber++;
                line = scriptScanner.nextLine().trim();
            } else {
                return null;
            }
        } while (line.isEmpty() || line.startsWith("#"));

        return line;
    }

    /**
     * Читает строку с приглашением.
     * <p>
     * В интерактивном режиме выводит приглашение и читает с консоли.
     * В режиме скрипта читает следующую строку из файла.
     * </p>
     *
     * @param invitation текст приглашения (используется только в интерактивном режиме)
     * @return прочитанная строка
     */
    public static String readStr(String invitation) {
        if (scriptRegime) {
            if (hasPeeked) {
                hasPeeked = false;
                return nextLine != null ? nextLine : "";
            }
            if (scriptScanner.hasNextLine()) {
                scriptLineNumber++;
                return scriptScanner.nextLine();
            }
            return "";
        } else {
            System.out.println(invitation);
            return scanner.nextLine();
        }
    }

    /**
     * Читает целое число с приглашением и валидацией.
     *
     * @param invitation текст приглашения
     * @return прочитанное целое число
     * @throws RuntimeException в режиме скрипта при пустой строке или нечисловом формате
     */
    public static int readInt(String invitation) {
        while (true) {
            try {
                String input = readStr(invitation).trim();
                if (input.isEmpty()) {
                    if (scriptRegime) {
                        throw new RuntimeException("Не должно быть пустой строки в стоке" + scriptLineNumber);
                    }
                    System.out.println("Ошибка, введите число");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                if (scriptRegime) {
                    throw new RuntimeException("Ошибка в скрипте, надо число");
                }
                System.out.println("Ошибка, необходимо ввести ЦЕЛОЕ число");
            }
        }
    }

    /**
     * Читает число с плавающей точкой двойной точности с приглашением и валидацией.
     *
     * @param invitation текст приглашения
     * @return прочитанное число
     * @throws RuntimeException в режиме скрипта при пустой строке или нечисловом формате
     */
    public static double readDouble(String invitation) {
        while (true) {
            try {
                String input = readStr(invitation).trim();
                if (input.isEmpty()) {
                    if (scriptRegime) {
                        throw new RuntimeException("Не должно быть пустой строки в стоке" + scriptLineNumber);
                    }
                    System.out.println("Ошибка, введите число");
                    continue;
                }
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                if (scriptRegime) {
                    throw new RuntimeException("Ошибка в скрипте, надо число");
                }
                System.out.println("Ошибка, необходимо ввести число");
            }
        }
    }

    /**
     * Читает непустую строку с приглашением и валидацией.
     *
     * @param invitation текст приглашения
     * @return непустая строка
     * @throws RuntimeException в режиме скрипта при пустой строке
     */
    public static String readNotEmptyStr(String invitation) {
        while (true) {
            String input = readStr(invitation).trim();
            if (input.isEmpty()) {
                if (scriptRegime) {
                    throw new RuntimeException("Ошибка в скрипте, поле не может быть пустым в строке" + scriptLineNumber);
                }
                System.out.println("Это поле не может быть пустым");
            } else {
                return input;
            }
        }
    }

    /**
     * Читает целое число типа Long (не null) с приглашением и валидацией.
     *
     * @param invitation текст приглашения
     * @return прочитанное число Long
     * @throws RuntimeException в режиме скрипта при пустой строке или нечисловом формате
     */
    public static Long readLongNotNull(String invitation) {
        while (true) {
            try {
                String input = readStr(invitation).trim();
                if (input.isEmpty()) {
                    if (scriptRegime) {
                        throw new RuntimeException("Пустой строки не может быть, строка " + scriptLineNumber);
                    }
                    System.out.println("Ошибка, введите число");
                    continue;
                }
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                if (scriptRegime) {
                    throw new RuntimeException("Ошибка в скрипте, надо целое число в строке" + scriptLineNumber);
                }
                System.out.println("Необходимо ввести число");
            }
        }
    }

    /**
     * Читает число с плавающей точкой типа Float (не null) с приглашением и валидацией.
     *
     * @param invitation текст приглашения
     * @return прочитанное число Float
     * @throws RuntimeException в режиме скрипта при пустой строке или нечисловом формате
     */
    public static Float readFloatNotNull(String invitation) {
        while (true) {
            try {
                String input = readStr(invitation).trim();
                if (input.isEmpty()) {
                    if (scriptRegime) {
                        throw new RuntimeException("Не должно быть пустой строки в стоке" + scriptLineNumber);
                    }
                    System.out.println("Ошибка, введите число");
                    continue;
                }
                return Float.parseFloat(input);
            } catch (NumberFormatException e) {
                if (scriptRegime) {
                    throw new RuntimeException("Ошибка в скрипте, надо число в строке" + scriptLineNumber);
                }
                System.out.println("Необходимо ввести число");
            }
        }
    }

    /**
     * Читает название улицы с ограничением длины (не более 61 символа).
     *
     * @param invitation текст приглашения
     * @return название улицы
     * @throws RuntimeException в режиме скрипта при превышении длины
     */
    public static String readStreet(String invitation) {
        while (true) {
            String input = readNotEmptyStr(invitation);
            if (input.length() > 61) {
                if (scriptRegime) {
                    throw new RuntimeException("Длина не может быть больше 61 символа в строке " + scriptLineNumber);
                }
                System.out.println("Длина не может быть больше 61 символа");
            } else {
                return input;
            }
        }
    }

    /**
     * Читает название локации с ограничением длины (не более 777 символов).
     * Пустая строка интерпретируется как null.
     *
     * @param invitation текст приглашения
     * @return название локации или null, если строка пуста
     * @throws RuntimeException в режиме скрипта при превышении длины
     */
    public static String readLocationName(String invitation) {
        while (true) {
            String input = readStr(invitation);
            if (input.isEmpty()) {
                return null;
            }
            if (input.length() > 777) {
                if (scriptRegime) {
                    throw new RuntimeException("Длина не может быть больше 777 символа в строке" + scriptLineNumber);
                }
                System.out.println("Длина не должна превышать 777 символов");
            } else {
                return input;
            }
        }
    }

    /**
     * Читает цену билета (должна быть положительной).
     * Пустая строка интерпретируется как null.
     *
     * @param invitation текст приглашения
     * @return цена или null, если строка пуста
     * @throws RuntimeException в режиме скрипта при некорректном значении
     */
    public static Long readPrice(String invitation) {
        while (true) {
            String input = readStr(invitation).trim();
            if (input.trim().isEmpty()) {
                return null;
            }
            try {
                long price = Long.parseLong(input.trim());
                if (price <= 0) {
                    if (scriptRegime) {
                        throw new RuntimeException("Стоимость не может быть меньше 0 в строке " + scriptLineNumber);
                    }
                    System.out.println("Стоимость не может быть меньше 0");
                } else {
                    return price;
                }
            } catch (NumberFormatException e) {
                if (scriptRegime) {
                    throw new RuntimeException("Введено нецелое число в строке " + scriptLineNumber);
                }
                System.out.println("Введите целое число");
            }
        }
    }


    public static TicketType readTicketType() {
        while (true) {
            if (!scriptRegime) {
                System.out.println("Доступные типы билетов:");
                System.out.println(TicketType.AllDescriptions());
            }
            String input = readStr("Введите тип билета: ").trim().toUpperCase();
            try {
                return TicketType.valueOf(input);
            } catch (IllegalArgumentException e) {
                if (scriptRegime) {
                    throw new RuntimeException("Ошибка, неправильный тип билета в строке " + scriptLineNumber);
                }
                System.out.println("Ошибка, неправильный тип билета");
            }
        }
    }

    public static Coordinates readCoordinates() {
        if (!scriptRegime) {
            System.out.println("Введите координты мест");
        }
        int x = readInt("Введите Х: ");
        Long y = readLongNotNull("Введите Y: ");

        return new Coordinates(x, y);
    }

    public static Location readLocation() {
        if (!scriptRegime) {
            System.out.println("Введите местоположение:");
        }
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
        if (!scriptRegime) {
            System.out.println("Введите адресс");
        }
        String street = readStreet("Введите улицу: ");
        String zipCode = readStr("Введите индекс: ").trim();
        if (zipCode.isEmpty()) zipCode = null;

        Location town = null;
        if (scriptRegime) {
            String next = peekNextLine();
            if (next != null && !next.isEmpty() && !commandFlag(next) && isNumber(next)) {
                town = readLocation();
            }
        } else {
            if (readYN("Добавить координаты и город? (y/n)")) {
                town = readLocation();
            }
        }
        return new Address(street, zipCode, town);
    }

    public static Venue readVenue() {
        if (scriptRegime) {
            String possibleName = peekNextLine();
            if (possibleName == null || possibleName.isEmpty() ||
                    commandFlag(possibleName) || isNumber(possibleName)) {
                return null;
            }

            String name = readNotEmptyStr("");

            String possibleCapacity = peekNextLine();
            if (possibleCapacity == null || !isNumber(possibleCapacity)) {
                throw new RuntimeException("Ожидалась вместимость (число) в строке " + scriptLineNumber);
            }
            int capacity = readInt("");
            if (capacity <= 0) {
                throw new RuntimeException("Вместимость должна быть больше 0 в строке" + scriptLineNumber);
            }
            Address address = readAddress();
            long id = 0;
            return new Venue(id, name, capacity, address);

        } else {
            if (!readYN("Добавить местоположение? (y/n)")) {
                return null;
            }
            System.out.println("Введите данные места проведения:");
            long id = 0;
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
        if (!isScriptRegime()) {
            return readTicket();
        }
        int id = 0;

        Coordinates coordinates = readCoordinates();
        LocalDateTime createDate = LocalDateTime.now();
        Long price = readPrice(" ");
        TicketType type = readTicketType();
        Venue venue = readVenue();

        return new Ticket(id, ticketName, coordinates, createDate, price, type, venue);
    }

    public static Ticket readTicket() {
        System.out.println("Введите данные билета");
        int id = 0;
        String name = readNotEmptyStr("Введите название: ");
        Coordinates coordinates = readCoordinates();
        Long price = readPrice("Введите стоимость");
        LocalDateTime createDate = LocalDateTime.now();
        TicketType type = readTicketType();
        Venue venue = readVenue();

        return new Ticket(id, name, coordinates, createDate, price, type, venue);
    }
}