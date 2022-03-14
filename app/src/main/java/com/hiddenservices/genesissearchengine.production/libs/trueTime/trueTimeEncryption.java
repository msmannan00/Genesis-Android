package com.hiddenservices.genesissearchengine.production.libs.trueTime;

import com.instacart.library.truetime.TrueTime;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class trueTimeEncryption {

    public static String S_FERNET_KEY = "W#ZYBHQa9G_DB_iU@yjA3Es@COu4-UzU";
    public static String S_APP_BLOCK_KEY = "D~S=05y68#M25oj]vprm}9HE))Tr'VX?[p|m-Wg`mrg^";
    private static trueTimeEncryption ourInstance = new trueTimeEncryption();
    public static trueTimeEncryption getInstance()
    {
        return ourInstance;
    }

    public void initTime(){
        try{
            TrueTime.build().initialize();
        }catch (Exception ignored){ }
    }

    public String getSecretToken() throws UnsupportedEncodingException {
        String mTime = String.valueOf(System.currentTimeMillis()/1000);
        com.hiddenservices.genesissearchengine.production.libs.fernet.Key mKey = new com.hiddenservices.genesissearchengine.production.libs.fernet.Key(android.util.Base64.encodeToString(S_FERNET_KEY.getBytes(), android.util.Base64.DEFAULT).replace("\n",""));
        com.hiddenservices.genesissearchengine.production.libs.fernet.Token mToken = com.hiddenservices.genesissearchengine.production.libs.fernet.Token.generate(mKey, S_APP_BLOCK_KEY + "----" + mTime);
        return URLEncoder.encode(mToken.serialise());
    }
}
