import chat.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.CosNaming.*;

public class Server {
	public static void main(String args[]) {
		try{
		// Creacion e inicializacion del ORB (Object Request Broker)
			ORB orb = ORB.init(args,null);

		// cogemos la referencia a la raiz desde donde creamos los POAs y activamos el manager de POAs
		
			org.omg.CORBA.Object obj =
				orb.resolve_initial_references("RootPOA");
			POA rootpoa = POAHelper.narrow(obj);
			POAManager manager = rootpoa.the_POAManager();
			manager.activate();

			// sacamos el nombre del servicio al que referenciamos
			obj = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef =
				org.omg.CosNaming.NamingContextExtHelper.narrow(obj);

			// Creamos el esclavo y lo registramos con el ORB
			ChatServerImpl cs = new ChatServerImpl();

			ChatServer chatserver = cs._this(orb);
			// asociamos el esclavo al servicio
			ncRef.rebind(ncRef.to_name("chatserver_yzioaw"), chatserver);

			System.out.println("Object activated");
			// Runeamos el ORB
			orb.run();

		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
}



