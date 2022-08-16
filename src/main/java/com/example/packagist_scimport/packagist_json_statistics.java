package com.example.packagist_scimport;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class packagist_json_statistics {
    public static void main(String[] args) {
        final String path_packagist_jsonfiles = "D://Exception/packagist/jsonfiles/";

        File[] pp_jsonfiles = new File(path_packagist_jsonfiles).listFiles();

        int count = 0;
        int jsonfilecount = 0;
        int source_obj_null = 0;
        int distobj_null = 0;
        int dist_keylist_count = 0;

        List<String> source_types = new ArrayList<>();

        for(File json : pp_jsonfiles){
            count ++;
            JSONParser parser = new JSONParser();
            if(json.getName().endsWith(".json")){
                jsonfilecount ++;
                try{
                    FileReader reader = new FileReader(json.getPath());
                    Object obj = parser.parse(reader);
                    JSONObject jobj = (JSONObject) obj;
                    reader.close();
                    try{
                       List<String> keylist = new ArrayList<>(jobj.keySet());
                       if(keylist.contains("dist")){
                           try{
                               JSONObject distobj = (JSONObject) jobj.get("dist");
                               if(distobj == null){
                                   try{
                                       JSONObject sourceobj = (JSONObject) jobj.get("source");
                                       if(sourceobj == null){
                                           source_obj_null++;
                                       }else{
                                           try{
                                               String type = (String) sourceobj.get("type");
                                               if(type==null){
                                                   type = "null";
                                               }
                                               if(type.equals("svn")){
                                                   System.out.println(json.getName());
                                               }
                                           } catch (Exception e) {
                                              System.out.println((String) sourceobj.get("type"));
                                           }
                                       }
                                   }catch (Exception e){
                                       //
                                   }
                               }
                           }catch(Exception e){
                               //System.out.println(json.getName() + e);
                           }
                       }else{
                           //System.out.println(keylist);
                       }
                    } catch(Exception e){
                        //System.out.println(json.getName());
                    }
                } catch (Exception e){
                    //System.out.println(e);
                }
            }
            /*if(count == 100000){
                break;
            }*/
        }
        System.out.println("전체 갯수 : " + count);
        System.out.println("json파일 갯수 : " + jsonfilecount);
        System.out.println("source도 null인 파일 갯수 : " + source_obj_null);
        System.out.println();
        for(String source_type:source_types){
            System.out.println(source_type);
        }
        System.out.println(source_types.size());
    }
}
