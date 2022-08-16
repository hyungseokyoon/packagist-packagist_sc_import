package com.example.packagist_scimport.filedownload;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class gitdownload {
    public static void downgittag(String git, String tag, String savepath) {
        System.out.println("cloning");
        try{
            Git gitandtag = Git.cloneRepository()
                    .setURI(git)
                    .setDirectory(new File(savepath))
                    .call();
            try{
                gitandtag.checkout().setName("refs/tags/" + tag).call();
                // checkout 하고 난 후에 압축파일로다시만들기
            } catch (Exception e){
                //
            }
            gitandtag.close();
        } catch (GitAPIException e) {
            //
        }
    }
}
