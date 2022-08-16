package com.example.packagist_scimport;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import java.io.File;

public class svnkit_test {
    public static void main(String[] args) throws SVNException {
        final String url = "https://plugins.svn.wordpress.org/buddypress/tags/2.3.0-beta-2";
        final String tags = "/tags/2.3.0-beta-2/@1160761";

        SVNClientManager clientManager = SVNClientManager.newInstance();
        SVNUpdateClient client = clientManager.getUpdateClient();

        client.doCheckout(
                SVNURL.parseURIEncoded(url),
                new File("/Users/ddukddi/Documents/programming/svn/test1/"),
                SVNRevision.HEAD,
                SVNRevision.HEAD,
                true
                );
    }
}
