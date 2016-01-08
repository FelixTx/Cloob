package fr.turfu.cloobi;

/**
 * Created by FT on 08/01/2016.
 */
public class BStation{
    public int dispo;
    public int total;
    boolean cb;
    public String toString(){
        String res=Integer.toString(dispo)+"/"+Integer.toString(total);
        return res;
    }
}

