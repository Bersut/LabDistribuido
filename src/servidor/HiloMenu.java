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
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joan
 */
public class HiloMenu implements Runnable {

    // private final static int PORT = 20000;
    private DataOutputStream out;
    private DataInputStream in;

    private ConcurrentHashMap<String, String> cachePronostico;
    private ConcurrentHashMap<String, String> cacheHoroscopo;

    private Socket socket;
    private int idSession;

    public HiloMenu(Socket socket, int id,
            ConcurrentHashMap<String, String> cachePronostico, ConcurrentHashMap<String, String> cacheHoroscopo) {
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
            Logger.getLogger(HiloMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void mensajesDeError(int i, DataOutputStream out) throws IOException {
        String[] Errores = {"\033[31mOpcion incorrecta"};
        out.writeUTF(Errores[i]);
    }

    private void enviarMenuDeOpciones() throws IOException {
        String outputLine;
        outputLine = "--------------------------------------------------------------------------------------------------------------------------"
                + "\nEscriba un horoscopo y una fecha separados por un coma donde la fecha tiene el formato DIA/MES/AÑO o exit para salir"
                + "\npor ejemplo: rata,01/02/2017"
                + "\n--------------------------------------------------------------------------------------------------------------------------";
        out.writeUTF(outputLine);
    }

    private DataInputStream connexionHoroscopo(String horoscopo) {
        Socket serverSocket = null;
        DataOutputStream outHoroscopo = null;
        DataInputStream inHoroscopo = null;
        try {
            serverSocket = new Socket("localhost", 10000);
            outHoroscopo = new DataOutputStream(serverSocket.getOutputStream());
            inHoroscopo = new DataInputStream(serverSocket.getInputStream());
            //Le envio el horoscopo
            outHoroscopo.writeUTF(horoscopo);

        } catch (UnknownHostException e) {
            System.err.println("Host desconocido");
            //System.exit(1);
        } catch (IOException e) {
            System.err.println("No se puede conectar a localhost");
            //System.exit(1);
        }
        return inHoroscopo;
    }

    private DataInputStream conexionClima(String fechaClima) {
        Socket serverSocket = null;
        DataOutputStream outClima = null;
        DataInputStream inClima = null;
        try {
            serverSocket = new Socket("localhost", 20000);
            outClima = new DataOutputStream(serverSocket.getOutputStream());
            inClima = new DataInputStream(serverSocket.getInputStream());
            //Le envio el horoscopo
            outClima.writeUTF(fechaClima);

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
        String lineaEntrada, lineaSalida, respuestaHoroscopo, respuestaClima;
        boolean listo = false;
        DataInputStream inHoroscopo;
        DataInputStream inClima;
        try {
            enviarMenuDeOpciones();
            while (!listo && (lineaEntrada = in.readUTF()) != null) {
                //leo la respuesta

                if (lineaEntrada.contains(",")) {

                    out.writeUTF("valido");

                    String horoscopo, pronostico;
                    int posComa = lineaEntrada.indexOf(',');

                    horoscopo = lineaEntrada.substring(0, posComa);
                    pronostico = lineaEntrada.substring(posComa + 1);

                    //Recorto los espacio en blanco y paso a minuscula
                    horoscopo = horoscopo.trim();
                    pronostico = pronostico.trim();

                    horoscopo = horoscopo.toLowerCase();
                    pronostico = pronostico.toLowerCase();

                    //verifica si las consultan se encuentran cacheadas
                    respuestaHoroscopo = this.cacheHoroscopo.get(horoscopo);
                    respuestaClima = this.cachePronostico.get(pronostico);

                    if (respuestaHoroscopo == null) {
                        //consulta no cacheada

                        //Generar conexion servidor horoscopo
                        inHoroscopo = connexionHoroscopo(horoscopo);
                        //inClima = conexionClima(pronostico);
                        if (inHoroscopo != null) {
                            //le envio la respuesta del servidor al cliente
                            respuestaHoroscopo = inHoroscopo.readUTF();
                            out.writeUTF(respuestaHoroscopo);
                            cacheHoroscopo.put(horoscopo, respuestaHoroscopo);
                        } else {
                            out.writeUTF("Fallo conexion con el servidor horoscopo");
                        }
                    } else {
                        //consulta cacheada
                        out.writeUTF(respuestaHoroscopo);
                    }
                    if (respuestaClima == null) {
                        inClima = conexionClima(pronostico);
                        if (inClima != null) {
                            String resPronostico;
                            //le envio la respuesta del servidor al cliente
                            resPronostico = inClima.readUTF();
                            out.writeUTF(resPronostico);
                            cachePronostico.put(pronostico, resPronostico);
                        } else {
                            out.writeUTF("Fallo conexion con el servidor clima");
                        }
                    } else {
                        out.writeUTF(respuestaClima);
                    }
                    // listo = true;
                } else if (lineaEntrada.equalsIgnoreCase("exit")) {
                    listo = true;
                    out.writeUTF("Cerrando conexion.... ");
                    desconnectar();

                } else {
                    out.writeUTF("Formato de consulta erroneo");
                }

            }
        } catch (IOException e) {
            //System.out.println("cerro conexion");
            e.printStackTrace();
            //System.exit(1);
        }

    }

}
