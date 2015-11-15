package channel;

import java.util.ArrayList;

public class Clients {

    private static ArrayList<Client> clientList = new ArrayList<>();

    public static Client getClient(final long chunk_index) {
        return clientList.get((int) (chunk_index % clientList.size()));
    }

    public static void addClient(final Client client) {
        clientList.add(client);
    }
}
