package com.cof.okhttp;

public class OggettoCondiviso {
    private static String string;
    private static String health;

    public void setHealth(String health) {
        this.health = health;
    }

    public String getHealth() {
        return health;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
        System.out.println(string);
    }
}
