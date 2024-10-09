package ru.pirum1ch.cloudsave.utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileLoad {
    String directoryToLoad = "/resources/load";

    public void fileLoad(String fileName){

        try(FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)){
             bufferedOutputStream.write(fileName.getBytes());
        }catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
    }

}
