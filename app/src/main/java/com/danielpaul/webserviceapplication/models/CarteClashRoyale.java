package com.danielpaul.webserviceapplication.models;

public class CarteClashRoyale {
    String name;
    int id;
    int maxLevel;
    IconeClashRoyale iconUrls;

    public CarteClashRoyale(String name, int id, int maxLevel, String icone) {
        this.name = name;
        this.id = id;
        this.maxLevel = maxLevel;
        this.iconUrls = new IconeClashRoyale(icone);
    }
}
