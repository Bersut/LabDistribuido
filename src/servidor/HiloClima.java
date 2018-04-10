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
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Joan
 */
public class HiloClima implements Runnable {

    private ConcurrentHashMap<Date, String> cacheClima;

    private DataOutputStream out;
    private DataInputStream in;

    private Socket socket;
    private int idSession;

    private String[] clima = {"nublado", "soleado", "lluvia", "granizo", "supernova", "parcialmente nublado"};

    public HiloClima(Socket socket, int idSession, ConcurrentHashMap<Date, String> cacheClima) {

        this.socket = socket;
        this.idSession = idSession;
        this.cacheClima = cacheClima;

        try {
            //inicializo buffers de lectura y escritura
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            desconnectar();
        }

    }

    private void desconnectar() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("ioexecption");
            //Logger.getLogger(ServidorHiloMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String retornarClima(Date dFecha) {
        String respuesta = cacheClima.get(dFecha);
        if (respuesta == null) {
            Random aleatorio = new Random();
            int numero = aleatorio.nextInt(clima.length);
            respuesta = clima[numero];
            //Se cargan los valores de hash siempre en mayuscula
            cacheClima.put(dFecha, respuesta);
        }

        return respuesta;
    }

    private Date transformarFecha(String fecha) {
        Date dFecha;
        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            dFecha = dt1.parse(fecha);
        } catch (ParseException ex) {
            return null;
        }
        return dFecha;
    }

    @Override
    public void run() {
        String fechaClima;
        String resultado;
        try {
            Date fecha;
            fechaClima = in.readUTF();
            fecha = transformarFecha(fechaClima);
            if (fecha != null) {
                resultado = retornarClima(fecha);
            } else {
                resultado = "Formate de fecha no valido";
            }
            out.writeUTF(resultado);

        } catch (IOException ex) {
            Logger.getLogger(HiloClima.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    /* 



    private boolean verificarExistencia(String horoscopo) {
        /* int longitud;
            longitud = horoscopo.length;
            int i = 0;
		boolean encontrado = false;
		while (i < longitud & !encontrado){
			encontrado = horoscopos[i].equalsIgnoreCase(horoscopo);
			i++;
		}
		return encontrado;
        return true;
    }

  
    


     */
}
