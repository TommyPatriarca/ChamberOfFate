package com.cof.player;

public class Player {
    private String name;
    private int healt = 100;

    public Player(String name) {
        this.name = name;
    }
    public void removeLife(int amount){
        if(healt>amount){
            healt-=amount;
        }else{
            //player loses
        }
    }

    public void addLife(int amount){
        if(100-healt>amount){
            healt+=amount;
        }else{
            healt=100;
        }
    }

    public String getName() {
        return name;
    }

    public int getHealt() {
        return healt;
    }
}
