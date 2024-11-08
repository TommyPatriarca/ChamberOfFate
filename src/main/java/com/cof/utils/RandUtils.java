package com.cof.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Serve per utilizzare le varie tipologie di random che vengono utilizzate da ogni funzione.
 */
public class RandUtils {
    private static final SecureRandom random = new SecureRandom();

    /**
     * Crea un "Integer" casuale che si trova tra un "inizio" e una "fine".
     * @param from numero da cui parte il random per estrarre ("inizio").
     * @param to ultimo numero che può essere estratto.
     * @return il numero estratto dal random.
     */
    public static int Integer(int from, int to) {
        if (from >= to) {
            throw new IllegalArgumentException("to must be greater than from");
        }
        return random.nextInt((to - from) + 1) + from;
    }

    /**
     * Crea una stringa casuale in base ad un numero definito di caratteri.
     * @param length numero di caratteri con il quale deve essere creata la stringa.
     * @return la stringa creata dal random.
     */
    public static String String(Integer length){
        return new BigInteger(130, random).toString(24).substring(1, length);
    }

    /**
     * Crea una stringa casuale di lunghezza 8.
     * @return la stringa estratta con il random.
     */
    public static String String(){
        return String(8);
    }

    /**
     * Crea un "Integer" casuale in base ad una numero minimo ed un numero massimo.
     * @param min numero da cui parte il random ad estrarre.
     * @param max numero massimo estratto dal random.
     * @return l' "Integer" estratto dal random.
     */
    public static Integer Integer(final Integer min, final Integer max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    /**
     * Crea un attributo "Boolean" in modo casuale.
     * @return il "Boolean" creato con il random.
     */
    public static Boolean Boolean(){
        return random.nextBoolean();
    }

    /**
     * Crea un email casuale utilizzando un metodo creato in questa classe.
     * @return l'email creata dal random.
     */
    public static String email(){
        return RandUtils.String() + "@acme";
    }

    /**
     * Crea una data casuale utilizzando dei metodi creati in questa classe.
     * @return la data creata con il random.
     */
    public static LocalDateTime LocalDateTime() {
        final int month = RandUtils.Integer(1, 12);
        final int day = RandUtils.Integer(1, 25);
        final int hour = RandUtils.Integer(1, 23);
        final LocalDateTime rand = LocalDateTime.of(2014, month, day, hour, 0);
        return rand.isBefore(LocalDateTime.now()) ? rand : LocalDateTime();
    }

    /**
     * Crea una data casuale(con diverso formato) utilizzando dei metodi creati in questa classe.
     * @return la data creata con il random.
     */
    public static LocalDate birthday(){
        final int year = RandUtils.Integer(1950, 1995);
        final int month = RandUtils.Integer(1, 12);
        final int day = RandUtils.Integer(1, 25);
        return LocalDate.of(year, month, day);
    }

}