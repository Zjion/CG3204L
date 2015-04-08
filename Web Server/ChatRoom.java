import java.util.*;

class ChatRoom {
  
  List<Client> clientList;
  String name;
  
  public ChatRoom(Client founder, String nameGiven)
  {
    clientList = new ArrayList<Client>();
    clientList.add(founder);
    name = nameGiven;
  }
  
  public void addClient(Client newClient) {clientList.add(newClient);}
  public void removeClient(Client remClient) {clientList.remove(remClient);}
}