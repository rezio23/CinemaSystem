package com.cinema.model;

public class Hall {
    private int hallId;
    private String hallName;
    private int capacity;
    private String hallType;

    public Hall() {}

    public Hall(int hallId, String hallName, int capacity, String hallType) {
        this.hallId = hallId;
        this.hallName = hallName;
        this.capacity = capacity;
        this.hallType = hallType;
    }

    public int getHallId() { return hallId; }
    public void setHallId(int hallId) { this.hallId = hallId; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getHallType() { return hallType; }
    public void setHallType(String hallType) { this.hallType = hallType; }

    @Override
    public String toString() {
        return hallName + " (" + capacity + " seats)";
    }
}
