package servidor;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;

public class ServidorClima {
	
	private static final int PUERTO = 20000;
    
    public static void main(String args[]) throws IOException {
        ConcurrentHashMap<Date, String> cacheClima = new ConcurrentHashMap<>();
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
