package channel;

import java.util.ArrayList;

public class Clients {

    private static ArrayList<Client> clientList = new ArrayList<>();

    public static Client getClient(final int chunk_index) {
        return clientList.get(chunk_index % clientList.size());
    }
}
