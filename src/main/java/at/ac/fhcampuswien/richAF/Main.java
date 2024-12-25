package at.ac.fhcampuswien.richAF;

import at.ac.fhcampuswien.richAF.data.EventManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EventManager eventManager = new EventManager("/tmp/richAF.log");

        for(String line : eventManager.readLogFile()){
            System.out.println(line);
        }
    }







}