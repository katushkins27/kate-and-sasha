package server.util;

import common.data.*;
import to_use.CreateID;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Scanner;

/**
 * Класс для парсинга и сохранения данных о билетах в CSV формате.
 * <p>
 * Предоставляет функциональность для:
 * <ul>
 *   <li>Чтения файла CSV и преобразования строк в объекты {@link Ticket}</li>
 *   <li>Записи коллекции билетов обратно в CSV файл</li>
 *   <li>Валидации данных при чтении</li>
 * </ul>
 * </p>
 *
 * @see Ticket
 * @see Venue
 * @see Address
 * @see Location
 */
public class Parser {
    /** Форматтер для преобразования даты и времени в ISO формат */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    /**
     * Выполняет парсинг CSV файла и преобразует его содержимое в коллекцию билетов.
     * <p>
     * Метод читает файл построчно, пропуская первую строку (заголовок).
     * Каждая непустая строка парсится в объект {@link Ticket} и добавляется в коллекцию.
     * При возникновении ошибок в процессе парсинга конкретной строки, эта строка пропускается,
     * а сообщение об ошибке выводится в стандартный поток ошибок.
     * </p>
     *
     * @param filePath путь к CSV файлу, который необходимо распарсить
     * @return {@link ArrayDeque} содержащий все успешно распарсенные объекты {@link Ticket}
     * @throws IOException если возникает ошибка при чтении файла (файл не найден, недостаточно прав доступа и т.д.)
     */
    public static ArrayDeque<Ticket> parseFile(String filePath) throws IOException {
        ArrayDeque<Ticket> tickets = new ArrayDeque<>();
        File file = new File(filePath);
        String encoding = "UTF-8";
        if (!file.exists()){
            throw new IOException("Такого файла не существует "+ filePath);
        }
        if (!file.canRead()){
            throw new SecurityException("Отсутствует права на чтение файла "+ filePath);
        }
        Scanner scanner = null;
        try {
            scanner = new Scanner(file, encoding);
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            int lineNumber = 1;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                try {
                    Ticket ticket = parseOneLine(line);
                    tickets.add(ticket);
                } catch (Exception e) {
                    System.err.println("Ошибка в строке " + lineNumber + ": " + e.getMessage());
                }
            }
        } finally{
            if (scanner!=null){
                scanner.close();
            }
        }
        return tickets;
    }
    /**
     * Парсит одну строку CSV и создает объект Ticket.
     *
     * @param line строка CSV для парсинга
     * @return объект {@link Ticket} созданный из строки
     * @throws IllegalArgumentException если обязательные поля отсутствуют или имеют неверный формат
     */
    private static Ticket parseOneLine(String line) {
        String[] fields = line.split(";", -1); //список с полями строки
        Ticket ticket = new Ticket();
        ticket.setID(Integer.parseInt(fields[0].trim()));
        CreateID.addTicketID(ticket.getId());
        ticket.setName(fields[1].trim());
        Coordinates coordinates = new Coordinates();
        if (!fields[2].trim().isEmpty()) {
            coordinates.setX(Integer.parseInt(fields[2].trim()));
        }
        coordinates.setY(Long.parseLong(fields[3].trim()));
        ticket.setCoordinates(coordinates);
        ticket.setCreationDate(LocalDateTime.parse(fields[4].trim(),DATE_FORMATTER));
        if (!fields[5].trim().isEmpty()){
            ticket.setPrice(Long.parseLong(fields[5].trim()));
        }
        ticket.setType(TicketType.valueOf(fields[6].trim()));
        if (fields.length > 7 && !fields[7].trim().isEmpty()){
            Venue venue = new Venue();
            venue.setID(Long.parseLong(fields[7].trim()));
            venue.setName(fields[8].trim());
            venue.setCapacity(Integer.parseInt(fields[9].trim()));
            Address address = new Address();
            address.setStreet(fields[10].trim());
            if (fields.length > 11 && !fields[11].trim().isEmpty()){
                address.setZipCode(fields[11].trim());
                if (fields.length > 12 && !fields[12].trim().isEmpty()) {
                    Location location = new Location();
                    location.setX(Double.parseDouble(fields[12].trim()));
                    location.setY(Long.parseLong(fields[13].trim()));
                    location.setZ(Float.parseFloat(fields[14].trim()));
                    if (fields.length > 15 && !fields[15].trim().isEmpty()) {
                        location.setName(fields[15].trim());
                    }
                    address.setTown(location);
                }
            }
            venue.setAddress(address);
            ticket.setVenue(venue);
            CreateID.addVenueID(ticket.getVenue().getID());
        }
        return ticket;
    }
    /**
     * Преобразует один билет в строку CSV формата.
     *
     * @param ticket объект {@link Ticket} для преобразования
     * @return {@link StringBuilder} содержащий CSV строку билета
     */
    public static StringBuilder saveTicketToCSV(Ticket ticket){
        StringBuilder s = new StringBuilder();
        s.append(ticket.getId()).append(";");
        s.append(ticket.getName()).append(";");
        s.append(ticket.getCoordinates().getX() != 0 ? ticket.getCoordinates().getX() : "").append(";");
        s.append(ticket.getCoordinates().getY()).append(";");
        s.append(ticket.getCreationDate().format(DATE_FORMATTER)).append(";");
        s.append(ticket.getPrice() != null ? ticket.getPrice() : "").append(";");
        s.append(ticket.getType()).append(";");
        if (ticket.getVenue() != null) {
            Venue venue = ticket.getVenue();
            s.append(venue.getID()).append(";");
            s.append(venue.getName()).append(";");
            s.append(venue.getCapacity()).append(";");
            Address address = venue.getAddress();
            s.append(address.getStreet()).append(";");
            s.append(address.getZipCode() != null ? address.getZipCode() : "").append(";");
            Location location = address.getTown();
            if (location!= null) {
                s.append(!Double.isNaN(location.getX()) ? location.getX() : "").append(";");
                s.append(location.getY()).append(";");
                s.append(location.getZ()).append(";");
                s.append(location.getName() != null ? location.getName() : "").append("");
            }
        } else {
            s.append(";;;;;;;;");
        }
        return s;
    }
    /**
     * Сохраняет всю коллекцию билетов в CSV файл.
     *
     * @param tickets коллекция {@link ArrayDeque} с билетами для сохранения
     * @param filename имя файла для сохранения
     * @throws IOException если возникают проблемы при записи в файл
     */
    public static void saveFileToCSV(ArrayDeque<Ticket> tickets, String filename) throws IOException{
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("id;name;coordinates.x;coordinates.y;creationDate;price;type;venue.id;venue.name;venue.capacity;address.street;address.zipCode;location.x;location.y;location.z;location.name\n");
            bw.flush();
            int count = 0;
            for (Ticket oneticket : tickets) {
                bw.write(saveTicketToCSV(oneticket) + "\n");
                count++;
                if (count%50==0){
                    bw.flush();
                }
            }
            bw.flush();
            System.out.println("Сохранено " + count + " билетов");

        }
    }
}




