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
        Optional<Ticket> optionalTicket=collection.stream().filter(t-> t.getId()==id).
                findFirst();
        if (optionalTicket.isPresent()) {
            Ticket oldTicket = optionalTicket.get();
            collection.remove(oldTicket);
            CreateID.removeTicketID(id);
            if (oldTicket.getVenue() != null) {
                CreateID.removeVenueID(oldTicket.getVenue().getID());
            }

            collection.add(newTicket);
            sortCollection();
            return true;
        }
        return false;
    }

    public void clearCollection() {
        collection.stream().forEach(ticket -> {
            CreateID.removeTicketID(ticket.getId());
            Optional.ofNullable(ticket.getVenue())
                    .ifPresent(venue -> CreateID.removeVenueID(venue.getID()));
        });
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
        collection.stream().findFirst().ifPresent(ticket -> {
            CreateID.removeTicketID(ticket.getId());
            Optional.ofNullable(ticket.getVenue())
                    .ifPresent(venue -> CreateID.removeVenueID(venue.getID()));
            collection.pollFirst();
        });
    }

    public boolean removeById(int id){
        Optional<Ticket> ticketRemoved = collection.stream().filter(t->t.getId()==id).findFirst();
        ticketRemoved.ifPresent(ticket -> {
            CreateID.removeTicketID(id);
            Optional.ofNullable(ticket.getVenue())
                    .ifPresent(venue -> CreateID.removeVenueID(venue.getID()));
        });
        boolean removed = ticketRemoved.isPresent();
        if (removed){
            collection = collection.stream().filter(t->t.getId()!=id)
                    .collect(Collectors.toCollection(ArrayDeque::new));
            sortCollection();
        }
        return removed;
    }

    public int removeAllByPrice(Long price){
        long count = collection.stream().filter(t -> Objects.equals(price, t.getPrice()))
                .count();
        if (count==0) return 0;
        collection.stream().filter(t-> Objects.equals(price, t.getPrice()))
                .forEach(ticket-> {
                    CreateID.removeTicketID(ticket.getId());
                    Optional.ofNullable(ticket.getVenue())
                            .ifPresent(venue -> CreateID.removeVenueID(venue.getID()));
                });
        collection.removeIf(ticket-> Objects.equals(price, ticket.getPrice()));
        sortCollection();
        return (int) count;
    }

    public boolean removeByType(TicketType type){
        Optional<Ticket> ticketRemoved = collection.stream().filter(t->t.getType()==type).findFirst();
        ticketRemoved.ifPresent(ticket -> {
            collection.remove(ticket);
            CreateID.removeTicketID(ticket.getId());
            Optional.ofNullable(ticket.getVenue())
                    .ifPresent(venue -> CreateID.removeVenueID(venue.getID()));
            sortCollection();
        });
        return ticketRemoved.isPresent();

    }

    public int removeAllGreater(Ticket newTicket){
        long count = collection.stream().filter(t-> t.compareTo(newTicket)<0).count();
        if (count==0) return 0;
        collection.stream().filter(t-> t.compareTo(newTicket)<0)
                .forEach(ticket-> {
                    CreateID.removeTicketID(ticket.getId());
                    Optional.ofNullable(ticket.getVenue())
                            .ifPresent(venue -> CreateID.removeVenueID(venue.getID()));
                });
        collection.removeIf(ticket-> ticket.compareTo(newTicket)<0);
        sortCollection();
        return (int) count;
    }

    public Ticket getMinByVenue() {
        return collection.stream().min(Comparator.comparing(Ticket::getVenue,
                Comparator.nullsFirst(Comparator.naturalOrder()))).orElse(null);
    }
}