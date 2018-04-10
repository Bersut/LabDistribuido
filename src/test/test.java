/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joan
 */
public class test {
    public static void main(String[] args){
        Date dFecha;
        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            dFecha = dt1.parse("0a/01/2017");
            System.out.println(dFecha);
        } catch (ParseException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
