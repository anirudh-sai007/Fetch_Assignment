package com.example.fetch_assignment;

public class ItemsList {

    private int id;
    private int listId;
    private String name;

    public ItemsList(int id, int listid, String name) {
        this.id = id;
        this.listId = listid;
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public int getListid() {
        return listId;
    }

    public String getName() {
        return name;
    }
}
