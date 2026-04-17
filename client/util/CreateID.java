package client.util;

import java.util.HashSet;
import java.util.Set;

public class CreateID {
    private static Set<Integer> ticketID = new HashSet<>();
    private static Set<Long> venueID = new HashSet<>();

    public static int createTicketID(){
        int id = 1;
        while (ticketID.contains(id)) id++;
        ticketID.add(id);
        return id;
    }

    public static long createVenueID(){
        long id = 1;
        while (venueID.contains(id)) id++;
        venueID.add(id);
        return id;
    }

    public static void removeTicketID(int id){
        ticketID.remove(id);
    }

    public static void removeVenueID(long id){
        venueID.remove(id);
    }

    public static void addTicketID(int id){
        if (id > 0) ticketID.add(id);
    }
    public static void addVenueID(long id){if (id > 0) venueID.add(id);}
}
