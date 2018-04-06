package servidor;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.*;

public class ServidorClima {
	
	private final int PUERTO = 2000;
    
    public static void main(String args[]) throws IOException {
        HashMap<Date, String> cacheClima = new HashMap<>();;
        ServerSocket ss;
        System.out.print("Inicializando servidor del horoscopo... ");
        try {
            ss = new ServerSocket(PUERTO);
            System.out.println("\t[OK]");
            int idSession = 0;
            while (true) {
                Socket socket;
                socket = ss.accept();
                System.out.println("Nueva conexión entrante: "+socket);
                //Creo un hilo y le envio un Runnable Servidor Menu
				new Thread(new HiloClima(socket,idSession,cacheClima)).start();
                idSession++;
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
