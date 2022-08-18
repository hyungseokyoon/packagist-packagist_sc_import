package com.example.packagist_scimport.filedownload;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class directdownload {
    // 제공되는 url 링크를 사용해 파일 다운로드 메소드
    // 이 메소드의 최종 목표는 .zip 파일의 경우 다운로드 진행하면 되고
    // tar.gz와 같은 Gzip의 형태일 경우 압축을 풀고, 그것을 다시 .zip의 형태로 재 압축해주면 된다.
    public static void directDownload(String durl, String savepath, String idcode) {
        // 다운로드 될 위치로는
        // 1. tempzipdownpath : .zip이 아닐경우 저장되는 위치
        // 2. fulldownloadpath : 실제 저장될 .zip이 들어가는 디렉토리 위치
        final String tempzipdownpath = savepath + "work1/temp/";
        // 먼저 temp 폴더가 있는지 확인해야함
        File ftemp = new File(tempzipdownpath);
        if(!ftemp.exists()){
            // temp 폴더가 존재하지 않을경우 temp 폴더를 만듦
            ftemp.mkdir();
        }else{
            if(!ftemp.isDirectory()){
                throw new IllegalArgumentException("temp is not a directory");
            }
        }

        try{
            URL url = new URL(durl);
            String filename = getdisposition(url);
            // 압축 형식에 따라 다른 방법을 사용해야함
            // 이름은 바뀔예정
            System.out.println(filename);
            if(filename.contains(".zip")){
                try {
                    // zip 파일의 경우 그대로 저장
                    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    FileOutputStream fos = new FileOutputStream(savepath + "work1/destination/" + idcode + ".zip");
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();
                    rbc.close();
                    System.out.println(".zip download completed");
                } catch (Exception e) {
                    System.out.println(".zip exeception occurred");
                }

            }else if(filename.contains(".gz")) {
                try {
                    // 다운로드 진행
                    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    FileOutputStream fos = new FileOutputStream(tempzipdownpath + filename);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();
                    rbc.close();
                    // temp 밑에 해당 이름으로 폴더를 개설 후에 decompress하고,
                    //dirZipper.ungzFile(tempzipdownpath + filename, tempzipdownpath);
                    // decompress 후에 폴더 안의 내용을 다시 .zip으로 변환
                    //dirZipper.zipDirectory(tempzipdownpath + filename, savepath + "work1/destination/" + idcode + ".zip");
                    System.out.println(".gz download complete");
                } catch (Exception e) {
                    System.out.println(".gz exeception occurred");
                }
            } else if(filename.contains(".xz")) {
                try {
                    // 다운로드 진행
                    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    FileOutputStream fos = new FileOutputStream(tempzipdownpath + filename);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();
                    rbc.close();
                    // temp 밑에 해당 이름으로 폴더를 개설 후에 decompress하고,
                    //dirZipper.unxzFile(tempzipdownpath + filename, tempzipdownpath);
                    // decompress 후에 폴더 안의 내용을 다시 .zip으로 변환
                    //dirZipper.zipDirectory(tempzipdownpath + filename, savepath + "work1/destination/" + idcode + ".zip");
                    System.out.println(".xz download complete");
                } catch (Exception e) {
                    System.out.println(".xz exeception occurred");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getdisposition(URL url) throws IOException {
        return FilenameUtils.getName(url.getPath());
    }

    public static String downloadToDir(URL fileURL, File destination) throws IOException {
        if (fileURL==null) throw new IllegalArgumentException("url is null.");
        if (destination==null) throw new IllegalArgumentException("directory is null.");
        if (!destination.exists()) throw new IllegalArgumentException("directory is not existed.");
        if (!destination.isDirectory()) throw new IllegalArgumentException("directory is not a directory.");
        return downloadTo(fileURL, destination, true);
    }

    // download 실제 진행
    private static String downloadTo(URL url, File targetFile, boolean isDirectory) throws IOException {
        HttpURLConnection httpConn = null;
        File saveFilePath = null;
        String fileName = "";
        //System.out.println(url);
        try {
            httpConn = (HttpURLConnection) url.openConnection();
        } catch (Error e) {
            System.out.println(e);
        }
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String disposition = httpConn.getHeaderField("Content-Disposition");
            //System.out.println(disposition);

            if (isDirectory) {
                if (disposition != null) {
                    //int index = disposition.indexOf("filename=");
                    int index = disposition.lastIndexOf("/");
                    //System.out.println(index);
                    if (index > 0) {
                        fileName = disposition.substring(index+1);
                    }
                } /*else {
                    String fileURL = url.toString();
                    int idx1 = fileURL.indexOf("/");
                    int idx2 = fileURL.indexOf("/", idx1+1);
                    fileName = fileURL.substring(idx1+1, idx2);
                    int questionIdx = fileName.indexOf("?");
                    if (questionIdx>=0) {
                        fileName = name + "_" + fileName.substring(0, questionIdx);
                    }
                    //fileName = name + "_" + URLDecoder.decode(fileName);
                    //System.out.println(fileName);
                }*/
                //System.out.println(fileName);
                saveFilePath = new File(targetFile, fileName);
            } else {
                saveFilePath = targetFile;
            }

            InputStream inputStream = httpConn.getInputStream();

            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[4096];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            //System.out.println("File downloaded to " + saveFilePath);
        } else {
            System.err.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
        return saveFilePath.getAbsolutePath();
    }
}
