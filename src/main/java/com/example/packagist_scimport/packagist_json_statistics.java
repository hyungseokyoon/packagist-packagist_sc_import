package com.example.packagist_scimport;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class packagist_json_statistics {
    public static void main(String[] args) {
        final String path_packagist_jsonfiles = "D://Exception/packagist/jsonfiles/";

        File[] pp_jsonfiles = new File(path_packagist_jsonfiles).listFiles();

        for(File json : pp_jsonfiles){
            JSONParser parser = new JSONParser();
            if(json.getName().endsWith(".json")){
                try{
                    FileReader reader = new FileReader(json.getPath());
                    Object obj = parser.parse(reader);
                    JSONObject jobj = (JSONObject) obj;
                    reader.close();

                    jobj.keySet();

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
