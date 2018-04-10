package servidor;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;

public class Servidor {
    
    public static void main(String args[]) throws IOException {
        ConcurrentHashMap<String, String> cachePronostico = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> cacheHoroscopo = new ConcurrentHashMap<>();
        ServerSocket ss;
        System.out.print("Inicializando servidor... ");
        try {
            ss = new ServerSocket(20000);
            System.out.println("\t[OK]");
            int idSession = 0;
            while (true) {
                Socket socket;
                socket = ss.accept();
                System.out.println("Nueva conexión entrante: "+socket);
                //Creo un hilo y le envio un Runnable Servidor Menu
                new Thread(new HiloMenu(socket,idSession,cachePronostico,cacheHoroscopo)).start();
                idSession++;
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
