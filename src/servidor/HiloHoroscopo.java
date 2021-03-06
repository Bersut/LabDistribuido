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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Joan
 */
public class HiloHoroscopo implements Runnable {

    private ConcurrentHashMap<String, String> cacheHoroscopo;

    private DataOutputStream out;
    private DataInputStream in;
    
    private Socket socket;
    private int idSession;

    private String[] horoscopos = {"Rata", "Búfalo", "Tigre", "Conejo", "Dragón", "Serpiente", "Caballo", "Cabra", "Mono", "Gallo", "Perro", "Cerdo"};
    private String[] predicciones = {"Si te caes siete veces, levántate ocho.",
        "Antes de iniciar la labor de cambiar el mundo, da tres vueltas por tu propia casa.",
        "Si quieres que algo se haga, encárgaselo a una persona ocupada.",
        "Excava el pozo antes de que tengas sed.",
        "El que teme sufrir ya sufre el temor.",
        "La tinta más pobre de color vale más que la mejor memoria.",
        "Un pájaro no canta porque tenga una respuesta. Canta porque tiene una canción.",
        "Sigue participando"};
    
    
    public HiloHoroscopo(Socket socket,int idSession,ConcurrentHashMap<String, String> cacheHoroscopo){
        
        this.socket = socket;
        this.idSession = idSession;
        this.cacheHoroscopo = cacheHoroscopo;
        
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
            Logger.getLogger(HiloMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
		
	private boolean verificarExistencia(String horoscopo){
		int longitud  = horoscopos.length, i=0;
		boolean encontrado = false;
		while (i < longitud & !encontrado){
			encontrado = horoscopos[i].equalsIgnoreCase(horoscopo);
			i++;
		}
		return encontrado;
	}
	
	private String retornarHoroscopo(String horoscopo){
		String respuesta = cacheHoroscopo.get(horoscopo.toUpperCase());
		if (respuesta == null){
			Random aleatorio = new Random();
			int numero = aleatorio.nextInt(predicciones.length); 
			respuesta = predicciones[numero]; 
			//Se cargan los valores de hash siempre en mayuscula
			cacheHoroscopo.put(horoscopo.toUpperCase(),respuesta);
		}
		
		return respuesta;
	}

    @Override
    public void run() {
		String horoscopo;
        try {
            horoscopo = in.readUTF();
            System.out.println(horoscopo);
            if (!verificarExistencia(horoscopo)){
			out.writeUTF("El horoscopo no existe");
		}else{
			out.writeUTF(retornarHoroscopo(horoscopo));
		}
        } catch (IOException ex) {
            Logger.getLogger(HiloHoroscopo.class.getName()).log(Level.SEVERE, null, ex);
        }
		
    }
	
}
