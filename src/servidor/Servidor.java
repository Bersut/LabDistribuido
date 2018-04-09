package servidor;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.*;

public class Servidor {
    
    public static void main(String args[]) throws IOException {
        HashMap<Date, String> cachePronostico = new HashMap<>();
        HashMap<Integer, String> cacheHoroscopo = new HashMap<>();
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
                new Thread(new ServidorHiloMenu(socket,idSession)).start();
                idSession++;
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
