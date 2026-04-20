package server;
import common.data.*;
import server.util.Parser;
import server.util.CreateID;
import java.util.*;
import java.time.LocalDateTime;
import java.io.IOException;
import java.util.stream.Collectors;

public class TicketCollection {

    private ArrayDeque<Ticket> collection;

    private LocalDateTime date;

    public TicketCollection() {
        this.collection = new ArrayDeque<>();
        this.date = LocalDateTime.now();
    }

    public TicketCollection(ArrayDeque<Ticket> firstCollection) {
        this.collection = firstCollection;
        this.date = LocalDateTime.now();
        sortCollection();
    }

    private void sortCollection() {
        collection = collection.stream().sorted().collect(Collectors.toCollection(ArrayDeque::new));
    }

    private void sortCollectionByLocation() {
        collection = collection.stream()
                .sorted((t1, t2) -> getTownName(t1).compareTo(getTownName(t2)))
                .collect(Collectors.toCollection(ArrayDeque::new));
    }
    private String getTownName(Ticket ticket){
        if (ticket.getVenue() != null &&
                ticket.getVenue().getAddress() != null &&
                ticket.getVenue().getAddress().getTown() != null &&
                ticket.getVenue().getAddress().getTown().getName() != null) {
            return ticket.getVenue().getAddress().getTown().getName();
        }
        return "";
    }

    public Ticket head() {
        sortCollectionByLocation();
        return collection.peekFirst();
    }

    public void addElement(Ticket ticket) {
        collection.add(ticket);
        sortCollection();
    }

    public boolean update(int id, Ticket newTicket){
        Iterator<Ticket> iterator = collection.iterator();
        while (iterator.hasNext()){
            Ticket ticket = iterator.next();
            if (id == ticket.getId()){
                iterator.remove();
                CreateID.removeTicketID(id);
                if (ticket.getVenue()!=null){
                    CreateID.removeVenueID(ticket.getVenue().getID());
                }
                collection.add(newTicket);
                sortCollection();
                return true;
            }
        }
        return false;
    }

    public void clearCollection() {
        for (Ticket ticket : collection) {
            CreateID.removeTicketID(ticket.getId());
            if (ticket.getVenue() != null) {
                CreateID.removeVenueID(ticket.getVenue().getID());
            }
        }
        collection.clear();
    }

    public String getInfo(){
        return String.format("Тип коллекции: %s\nДата инициализации: %s\nРазмер коллекции: %d",
                collection.getClass().getSimpleName(), date, collection.size());
    }

    public String showAll(){
        if (collection.isEmpty()) return "Коллекция пустая";
        return  collection.stream().sorted((t1,t2)->{
            String loc1=getTownName(t1);
            String loc2=getTownName(t2);
            return loc1.compareTo(loc2);
        }).map(Ticket::toString).collect(Collectors.joining("\n"));
    }

    public ArrayDeque<Ticket> getCollection() {return collection;}

    public void removeHead(){
        sortCollectionByLocation();
        Ticket ticket = head();
        if (ticket != null){
            CreateID.removeTicketID(ticket.getId());
            if (ticket.getVenue()!=null){
                CreateID.removeVenueID(ticket.getVenue().getID());
            }
            collection.pollFirst();
        }
    }

    public boolean removeById(int id){
        Iterator<Ticket> iterator = collection.iterator();
        while (iterator.hasNext()){
            Ticket ticket = iterator.next();
            if (id == ticket.getId()){
                iterator.remove();
                CreateID.removeTicketID(id);
                if (ticket.getVenue()!=null){
                    CreateID.removeVenueID(ticket.getVenue().getID());
                }
                sortCollection();
                return true;
            }
        }
        return false;
    }

    public int removeAllByPrice(Long price){
        Iterator<Ticket> iterator = collection.iterator();
        int count = 0;
        while (iterator.hasNext()){
            Ticket ticket = iterator.next();
            if (Objects.equals(price, ticket.getPrice())){
                count = count +1;
                iterator.remove();
                CreateID.removeTicketID(ticket.getId());
                if (ticket.getVenue()!=null){
                    CreateID.removeVenueID(ticket.getVenue().getID());
                }
            }
        }
        sortCollection();
        return count;
    }

    public boolean removeByType(TicketType type){
        Iterator<Ticket> iterator = collection.iterator();
        while (iterator.hasNext()){
            Ticket ticket = iterator.next();
            if (type == ticket.getType()){
                iterator.remove();
                CreateID.removeTicketID(ticket.getId());
                if (ticket.getVenue()!=null){
                    CreateID.removeVenueID(ticket.getVenue().getID());
                }
                sortCollection();
                return true;
            }
        }
        return false;
    }

    public int removeAllGreater(Ticket newTicket){
        Iterator<Ticket> iterator = collection.iterator();
        int count = 0;
        while (iterator.hasNext()){
            Ticket ticket = iterator.next();
            if (ticket.compareTo(newTicket)<0){
                count = count +1;
                iterator.remove();
                CreateID.removeTicketID(ticket.getId());
                if (ticket.getVenue()!=null){
                    CreateID.removeVenueID(ticket.getVenue().getID());
                }
            } else{
                break;
            }
        }
        sortCollection();
        return count;
    }

    public Ticket getMinByVenue() {
        return collection.stream().min(Comparator.comparing(Ticket::getVenue,
                Comparator.nullsFirst(Comparator.naturalOrder()))).orElse(null);
    }
}
