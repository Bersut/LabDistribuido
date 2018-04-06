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

    private Socket socket;
    private int idSession;


    public ServidorHiloMenu(Socket socket, int id) {
        this.socket = socket;
        this.idSession = id;
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
        int cantLineas;
        outputLine = "Elegir un horoscopo seguido de una fecha separado por coma\npor ejemplo: rata,01/02/2017 donde la fecha tiene el formato DIA/MES/AÑO" ;
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
	
	private String connexionHoroscopo(String horoscopo){
		Socket serverSocket = null;
		DataOutputStream outHoroscopo = null;
        DataInputStream inHoroscopo = null;
		String respuesta = "error"  ;
		try{
			serverSocket = new Socket("localhost", 1000);
            outHoroscopo = new DataOutputStream(serverSocket.getOutputStream());
            inHoroscopo = new DataInputStream(serverSocket.getInputStream());
			//Le envio el horoscopo
			outHoroscopo.writeUTF(horoscopo);
			//espero la respuesta
			respuesta = inHoroscopo.readUTF();
			 
		} catch (UnknownHostException e) {
            System.err.println("Host desconocido");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("No se puede conectar a localhost");
            System.exit(1);
        }
		return respuesta;
	}

    @Override
    public void run() {
        String lineaEntrada, lineaSalida;
        try {
            enviarMenuDeOpciones();
            while ((lineaEntrada = in.readUTF()) != null) {
                //leo la respuesta
                if (lineaEntrada.contains(',')){
					out.writeUTF("valido");
					
					String horoscopo,pronostico;
					int posComa = lineaEntrada.indexOf(',');
					
					horoscopo = lineaEntrada.substring(0,posComa - 1);
					pronostico = lineaEntrada.substring(posComa + 1);
					
					//Recorto los espacio en blanco
					horoscopo = horoscopo.trim();
					pronostico = pronostico.trim();
					
					//Generar conexion servidor horoscopo
					connexionHoroscopo(horoscopo);
					
				}else{
					lineaSalida = "Porfavor ingrese un horoscopo y un pronostico seguido por coma";
				}

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
