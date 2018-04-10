/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

/**
 *
 * @author Joan
 */
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

public class Cliente {

    private final static int PORT = 30000;
    private final static String SERVER = "localhost";

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket serverSocket = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        String salida;
        String menu = "";
        boolean exit = false;
        try {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            while (!exit) {
                serverSocket = new Socket(SERVER, PORT);
                out = new DataOutputStream(serverSocket.getOutputStream());
                in = new DataInputStream(serverSocket.getInputStream());

                String userInput;

                menu = in.readUTF();
                System.out.println(menu);

                //leyendo tecladoe
                while (!exit && (userInput = stdIn.readLine()) != null) {
                    if (userInput.equals("salir")) {
                        out.writeUTF("exit");
                        exit = true;
                    } else {

                        out.writeUTF(userInput);
                        if (!(salida = in.readUTF()).equals("valido")) {
                            //La consulta posee algun error
                            System.out.println(salida);

                        } else {
                            //Tenia una , la secuencia
                            //leo la respuesta del servidor
                            salida = in.readUTF();
                            System.out.println(salida);
                            salida = in.readUTF();
                            System.out.println(salida);
                        }
                        System.out.println(menu);
                    }
                }

            }
            System.out.println("chau cliente");
            out.close();
            in.close();
            stdIn.close();

            serverSocket.close();
        } catch (UnknownHostException e) {
            System.err.println("Host desconocido");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("No se puede conectar a localhost");
            System.exit(1);
        }
    }

    public static void recibirMenu(BufferedReader in) {

    }

    public static int countLines(String str) {
        int count = 0;
        int total = str.length();
        for (int i = 0; i < total; ++i) {
            char letter = str.charAt(i);
            if (letter == '\n') {
                ++count;
            }
        }
        return count;
    }

    public static void limpiarpantalla() {
        Robot robot;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_L);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_L);

        } catch (AWTException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
