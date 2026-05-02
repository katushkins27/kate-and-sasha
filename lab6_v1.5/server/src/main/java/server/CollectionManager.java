package server;
import common.data.Ticket;
import common.data.TicketCollection;
import common.util.*;
import java.io.*;
import java.util.ArrayDeque;
import java.util.logging.Logger;
public class CollectionManager {
    private static final Logger logger = Logger.getLogger(CollectionManager.class.getName());
    private final TicketCollection collection;
    private final String filename;

    public CollectionManager(TicketCollection collection, String filename) {
        this.collection = collection;
        this.filename = filename;
    }

    public void load() {
        try {
            ArrayDeque<Ticket> loaded = Parser.parseFile(filename);
            for (Ticket ticket : loaded) {
                collection.addElement(ticket);
                CreateID.addTicketID(ticket.getId());
                if (ticket.getVenue() != null) CreateID.addVenueID(ticket.getVenue().getID());
            }
            logger.info("Коллекция успешно загружена из " + filename);
        } catch (IOException e) {
            logger.warning("Ошибка при загрузке: " + e.getMessage() + ". Работаем с пустой коллекцией.");
        }
    }

    public void save() {
        try {
            Parser.saveFileToCSV(collection.getCollection(), filename);
            logger.info("Коллекция сохранена в файл: " + filename);
        } catch (IOException e) {
            logger.severe("Критическая ошибка сохранения: " + e.getMessage());
        }
    }

    public TicketCollection getCollection() { return collection; }
}
