import org.omg.CORBA.*;
import java.util.*;
import chat.*;

public class ChatServerImpl extends chat.ChatServerPOA {

	protected class Client {
		public chat.ChatClient chatclient;
		public String nick;
		public Client(String nick, chat.ChatClient chatclient) {
			this.chatclient = chatclient;
			this.nick = nick;
		}
					//update remoto del nick del cliente
			protected void updateNick(String nick){
				this.nick = nick;
			}
			protected String getNick(){
				return this.nick;
			}
	}

	protected Map<String, Client> clients = new HashMap<String, Client>();
	protected List<String> nicks = new Vector<String>();

	public String subscribe(String nick, chat.ChatClient c)
		throws chat.NameAlreadyUsed {
		if (nicks.contains(nick)) throw new chat.NameAlreadyUsed();
		nicks.add(nick);
		String id = UUID.randomUUID().toString();
		System.out.println("subscribe: " + nick + " -> " + id);
		clients.put(id, new Client(nick, c));
		return id;
	}

	public void unsubscribe(String id) throws chat.UnknownID {
		System.out.println("unsubscribe: " + id);
		Client c = clients.remove(id);
		if (c == null) throw new chat.UnknownID();
		nicks.remove(c.nick);
	}

	public void comment(String id, String text) throws chat.UnknownID {
		Client from = clients.get(id);
		if (from == null) throw new chat.UnknownID();
		System.out.println(
			"comment: " + text + " by " + id+ " [" + from.nick + "]");
		for (Client to : clients.values()) {
			to.chatclient.update(from.nick, text);
		}
	}

	
//mostrar usuarios - Nueva
	public String showUsers(String id) {
		
	//Para juntar todos los nombres en una string
	StringBuilder sb = new StringBuilder();

		for (String s : nicks) {
			sb.append(s);
			sb.append(" ");
		}

		return sb.toString();

	}


	public String changeNick(String nick, String id)
		throws chat.NameAlreadyUsed {
		if (nicks.contains(nick)) throw new chat.NameAlreadyUsed();

	//saco el elemento cliente asociado a este id
		Client clientInHash =clients.get(id) ;
		clientInHash.updateNick(nick);

	//borramos anterior nick
		String oldNick = clientInHash.getNick();
		nicks.remove(oldNick);
		nicks.add(nick);
		String ret ="registrado como "+ nick;
		return ret;
	}


}

