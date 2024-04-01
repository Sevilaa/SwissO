package ch.swisso;

import android.database.Cursor;

public class List {
    private final int id;
    private final Event event;
    private final int listType;

    public List(int id, Event event, int listType){
        this.id = id;
        this.event = event;
        this.listType = listType;
    }

    public int getId(){
        return id;
    }

    public Event getEvent(){
        return event;
    }

    public int getListType(){
        return listType;
    }

    public boolean isStartliste(){
        return Helper.isStartliste(listType);
    }
}
