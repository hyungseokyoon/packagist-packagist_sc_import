package com.example.packagist_scimport;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.packagist_scimport.metadata.MetadataHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

public class test {
    public static void main(String[] args) throws Exception {
        // 초기설정 후에 메인 기능 실행.
        // importer 에서 사용될 기본적인 인증정보
        //final List<String> bucket_names = new ArrayList<>(Arrays.asList("sca-packagist-md-crawler-0001"));
        final String bucket_name = "sca-packagist-md-crawler-0001";
        //아래 정보는 고정됨.
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2).build();
        final String gitID = "sca.github@gmail.com";
        final String gitPW = "!sparrow1605";

        //String savepath = "/home/ubuntu/af_output/" + bucket_name + "/";
        String savepath = "/Users/ddukddi/Documents/programming/packagist/";
        String record = "metadata/packagist/date=1660316400000/info/";

        int count = 0;
        int zipcount = 0;
        int gzcount = 0;
        int xzcount = 0;

        // 레코드별 key를 가져오므로써 전체 다운로드 되어야 하는 리스트 생성
        ArrayList<S3ObjectSummary> objects = MetadataHandler.getObejctKeys(bucket_name, s3, record);
        for(S3ObjectSummary object : objects){
            count ++;
            File json_file = MetadataHandler.getJSONfiles (object, bucket_name, savepath);

            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(json_file.getPath());
            Object obj = parser.parse(reader);
            JSONObject jobj = (JSONObject) obj;
            reader.close();

            if(jobj==null){
                json_file.delete();
            }else{
                try{
                    JSONObject dist_obj = (JSONObject) jobj.get("dist");
                    if(dist_obj!=null){
                        if(dist_obj.get("type").equals("zip")){
                            URL url = new URL((String)dist_obj.get("url"));
                            if(getContentDisposition(url).contains(".zip")){
                                zipcount++;
                            }else{
                                System.out.println(json_file.getName());
                            }
                        }
                    }
                }catch(Exception e){
                    // dist object가 없을경우 exception발생
                }
                json_file.delete();
            }
        }
        System.out.println("total count : " + count);
        System.out.println("zipcount : " + zipcount);
        System.out.println("xzcount : " + xzcount);
        System.out.println("gzcount : " + gzcount);
    }

    public static String getContentDisposition(URL url) {
        String filename = "";
        try{
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                String disposition = httpConn.getHeaderField("Content-Disposition");
                File saveFilePath = null;
                if(disposition!=null){
                    int index = disposition.indexOf("filename=");
                    filename = disposition.substring(index+10);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return filename;
    }
}
