import chat.*;
import java.io.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.CosNaming.*;
import java.util.regex.Pattern;

public class Client implements Runnable {
	public void run() {
		try {
			// getting reference to POA
			org.omg.CORBA.Object obj =
			orb.resolve_initial_references("RootPOA");
			POA rootpoa = POAHelper.narrow(obj);
			// getting reference to POA manager
			POAManager manager = rootpoa.the_POAManager();
			// activating manager 
			manager.activate();
			orb.run();
		} catch (Exception e) {}
	}

	protected static ORB orb;

	public static void main(String args[]) {
		try {
			// Creacion e inicializacion del ORB (Object Request Broker)
			orb = ORB.init(args,null);
			// Sacamos el service name y lo asignamos a la variable chatserver (que tendra nuestro servant)
			org.omg.CORBA.Object obj = 
			orb.resolve_initial_references("NameService");
			NamingContextExt ncRef =
			org.omg.CosNaming.NamingContextExtHelper.narrow(obj);
			obj = ncRef.resolve_str("chatserver_yzioaw");
			ChatServer chatserver = ChatServerHelper.narrow(obj);

			// iniciamos el servant
			ChatClientImpl cc = new ChatClientImpl();

			// conexion del esclavo al ORB 
			ChatClient chatclient = cc._this(orb);
			Thread t = new Thread(new Client());
			String id = chatserver.subscribe("test", chatclient);
			try {
				System.out.println("Connected with ID " + id);
				System.out.println("Type /quit to exit");
				System.out.println("Type /show to show users in chat");
				System.out.println("Type /change to change nick");
				t.start();
				BufferedReader br =
					new BufferedReader(new InputStreamReader(System.in));
				
				//bucle principal del cliente
				while (true) {
					String s = br.readLine(); //casos de uso
					if (s.equals("/quit")) break;

					if(s.equals("/show") ){
						String returnValue = chatserver.showUsers(id);
						String[] returnValueParts = returnValue.split(Pattern.quote(" "));

                        System.out.println("Usuarios:\n");
                        for (int i = 0; i < returnValueParts.length; i++) {
                            System.out.println((i + 1) + ". " + returnValueParts[i] + "\n");
                        }
                        System.out.println("*******************\n");

						continue;
					}

					if (s.equals("/change")) {
						 System.out.print("New nick: ");
						String newNick = br.readLine(); //casos de uso
						String result = chatserver.changeNick(id, newNick);
						System.out.println(result);
						continue;
					}

					chatserver.comment(id, s);
				}

			} finally { //fin de uso
				System.out.print("Unsubscribing...");
				chatserver.unsubscribe(id);
				System.out.println(" done");
				orb.destroy();
				t.join();
			}
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
}



