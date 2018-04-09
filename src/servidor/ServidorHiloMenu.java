/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joan
 */
public class ServidorHiloMenu implements Runnable {

    // private final static int PORT = 20000;
    private DataOutputStream out;
    private DataInputStream in;

    private HashMap<Date, String> cachePronostico ;
    private HashMap<Integer, String> cacheHoroscopo ;

    private Socket socket;
    private int idSession;

    public ServidorHiloMenu(Socket socket, int id,
            HashMap<Date, String> cachePronostico,HashMap<Integer, String> cacheHoroscopo ) {
        this.socket = socket;
        this.idSession = id;
        this.cachePronostico = cachePronostico;
        this.cacheHoroscopo = cacheHoroscopo;
        try {
            //inicializo buffers de lectura y escritura
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            desconnectar();
            e.printStackTrace();
        }
    }

    private void desconnectar() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorHiloMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void mensajesDeError(int i, DataOutputStream out) throws IOException {
        String[] Errores = {"\033[31mOpcion incorrecta"};
        out.writeUTF(Errores[i]);
    }

    private void enviarMenuDeOpciones() throws IOException {
        String outputLine;
        outputLine = "Elegir un horoscopo seguido de una fecha separado por coma donde la fecha tiene el formato DIA/MES/AÑO \npor ejemplo: rata,01/02/2017 ";
        out.writeUTF(outputLine);
    }

    public static int countLines(String str) {
        int count = 1;
        int total = str.length();
        for (int i = 0; i < total; ++i) {
            char letter = str.charAt(i);
            if (letter == '\n') {
                ++count;
            }
        }
        return count;
    }

    private DataInputStream connexionHoroscopo(String horoscopo) {
        Socket serverSocket = null;
        DataOutputStream outHoroscopo = null;
        DataInputStream inHoroscopo = null;
        try {
            serverSocket = new Socket("localhost", 1000);
            outHoroscopo = new DataOutputStream(serverSocket.getOutputStream());
            inHoroscopo = new DataInputStream(serverSocket.getInputStream());
            //Le envio el horoscopo
            outHoroscopo.writeUTF(horoscopo);
            //espero la respuesta
            //respuesta = inHoroscopo.readUTF();

        } catch (UnknownHostException e) {
            System.err.println("Host desconocido");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("No se puede conectar a localhost");
            System.exit(1);
        }
        return inHoroscopo;
    }

    private DataInputStream conexionClima(String fechaClima) {
        Socket serverSocket = null;
        DataOutputStream outClima = null;
        DataInputStream inClima = null;
        try {
            serverSocket = new Socket("localhost", 2000);
            outClima = new DataOutputStream(serverSocket.getOutputStream());
            inClima = new DataInputStream(serverSocket.getInputStream());
            //Le envio el horoscopo
            outClima.writeUTF(fechaClima);
            //espero la respuesta
            //respuesta = inHoroscopo.readUTF();

        } catch (UnknownHostException e) {
            System.err.println("Host desconocido");
            //System.exit(1);
        } catch (IOException e) {
            System.err.println("No se puede conectar a localhost");
            //System.exit(1);
        }
        return inClima;
    }

    @Override
    public void run() {
        String lineaEntrada, lineaSalida;
        boolean listo = false;
        DataInputStream inHoroscopo;
        DataInputStream inClima;
        try {
            enviarMenuDeOpciones();
            while ((lineaEntrada = in.readUTF()) != null && !listo) {
                //leo la respuesta
                if (lineaEntrada.contains(",")) {
                    out.writeUTF("valido");

                    String horoscopo, pronostico;
                    int posComa = lineaEntrada.indexOf(',');

                    horoscopo = lineaEntrada.substring(0, posComa - 1);
                    pronostico = lineaEntrada.substring(posComa + 1);

                    //Recorto los espacio en blanco
                    horoscopo = horoscopo.trim();
                    pronostico = pronostico.trim();

                    //Generar conexion servidor horoscopo
                    inHoroscopo = connexionHoroscopo(horoscopo);
                    inClima = conexionClima(pronostico);
                    if (inHoroscopo != null) {
                        out.writeUTF(inHoroscopo.readUTF());
                    } else {
                        out.writeUTF("Fallo conexion con el servidor horoscopo");
                    }

                    if (inClima != null) {
                        out.writeUTF(inClima.readUTF());
                    } else {
                        out.writeUTF("Fallo conexion con el servidor clima");
                    }

                    listo = true;

                } else {
                    lineaSalida = "Porfavor ingrese un horoscopo y un pronostico seguido por coma";
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
