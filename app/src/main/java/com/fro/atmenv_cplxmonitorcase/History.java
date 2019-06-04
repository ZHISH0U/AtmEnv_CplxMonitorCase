package com.fro.atmenv_cplxmonitorcase;
public class History {
    int issucceed;
    String date;
    String card_id;

    public History(String date, String card_id, int issucceed) {
        this.card_id = card_id;
        this.date = date;
        this.issucceed = issucceed;
    }
}


