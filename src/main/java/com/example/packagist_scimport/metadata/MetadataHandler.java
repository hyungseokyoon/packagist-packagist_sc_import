package com.example.packagist_scimport.metadata;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

// S3에 있는 metadata를 json의 형태로 가져와서, 정보를 바탕으로 source 코드를 저장하기 전까지의 프로세스

public class MetadataHandler {
    // 주어진 bucket_name을 가지고 S3 안에 존재하는 Object 들을 가져오는 메소드
    public static ArrayList<S3ObjectSummary> getObejctKeys(String bucket_name, AmazonS3 s3){
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket_name);
        ListObjectsV2Result result = s3.listObjectsV2(req);
        ArrayList<S3ObjectSummary> finalresult = new ArrayList<>();
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        finalresult.addAll(objects);
        boolean truncresult = true;
		while(truncresult == true){
			String token = result.getNextContinuationToken();
			req.setContinuationToken(token);

			result = s3.listObjectsV2(req);
			objects = result.getObjectSummaries();
			finalresult.addAll(objects);
			truncresult = result.isTruncated();
		}
        return finalresult;
    }
    // Object 정보를 가지고 json 파일 다운로드 진행.
    // 이때, json 파일은 저장이 필요하지 않으므로, json 파일은 overwrite하여 temporary로 저장한다.

    public static void getJSONfiles(S3ObjectSummary os, String bucket_name) throws Exception, Error {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
        String objectID;
        try {
            S3Object obj = s3.getObject(bucket_name, os.getKey());
            S3ObjectInputStream s3is = obj.getObjectContent();
            objectID = os.getKey().substring(os.getKey().lastIndexOf("/")+1).replace(".json", "");
            //임시파일로 저장해서 저장공간 save
            // 저장한 임시파일은 jsonfolder 안에 저장
            File file = new File( "/home/ubuntu/af_output/work1/jsons/" + objectID + ".json");
            //File file = new File(jsonfilesavepath+os.getKey().substring(os.getKey().lastIndexOf("/")+1));
            FileOutputStream fos = new FileOutputStream(file, false);
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();

           /* File jsonsavefile = new File(jsonfilesavepath+os.getKey().substring(os.getKey().lastIndexOf("/")+1));
            FileUtils.copyFile(file, jsonsavefile);*/
        }catch (Exception e){
            throw new Exception(e);
        }catch (Error er){
            throw new Error(er);
        }
    }
}
