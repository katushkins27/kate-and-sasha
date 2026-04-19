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

    public Ticket head() {
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
            String loc1="";
            if (t1.getVenue()!=null && t1.getVenue().getAddress() !=null &&
            t1.getVenue().getAddress().getTown() != null &&
            t1.getVenue().getAddress().getTown().getName() != null){
                loc1= t1.getVenue().getAddress().getTown().getName();
            }
            String loc2="";
            if (t2.getVenue()!=null && t2.getVenue().getAddress() !=null &&
                    t2.getVenue().getAddress().getTown() != null &&
                    t2.getVenue().getAddress().getTown().getName() != null){
                loc2= t2.getVenue().getAddress().getTown().getName();
            }
            return loc1.compareTo(loc2);
        }).map(Ticket::toString).collect(Collectors.joining("\n"));
    }

    public ArrayDeque<Ticket> getCollection() {return collection;}

    public void removeHead(){
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
            if (price.equals(ticket.getPrice())){
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
