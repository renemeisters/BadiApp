package com.example.bmeisr.badiapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class BadiData {
    private static ArrayList<ArrayList<String>> dataFromFile;

    //Liest Dateien aus CSV Datei und returnt diese als ArrayList
    private BadiData(Context c) {
        Scanner scanner = new Scanner(c.getResources().openRawResource(R.raw.badi_ids_dataset));
        scanner.useDelimiter(";");
        dataFromFile = new ArrayList<ArrayList<String>>();
        while (scanner.hasNext()) {
            String dataInRow = scanner.nextLine();
            String[] dataInRowArray = dataInRow.split(";");
            ArrayList<String> rowDataFromFile = new ArrayList<String>(Arrays.asList(dataInRowArray));
            dataFromFile.add(rowDataFromFile);
        }
        scanner.close();
    }

    public static ArrayList<ArrayList<String>> allBadis(Context c) {
        if (null == dataFromFile) {
            new BadiData(c);
        }
        return dataFromFile;
    }
}

