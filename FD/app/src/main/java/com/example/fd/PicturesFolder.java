package com.example.fd;


import java.io.File;
import java.util.ArrayList;

public class PicturesFolder {
    private File folderPath;

    public PicturesFolder(File folderPath){
        this.folderPath = folderPath;
    }

    public ArrayList<String> getAllFiles(){
        ArrayList<String> result = new ArrayList<String>(); //ArrayList cause you don't know how many files there are
        File[] filesInFolder = folderPath.listFiles(); // This returns all the folders and files in your path
        assert filesInFolder != null;
        for (File file : filesInFolder) { //For each of the entries do:
            if (!file.isDirectory()) { //check that it's not a dir
                result.add(new String(file.getName())); //push the filename as a string
            }
        }

        return result;
    }
}
