package com.hiddenservices.onionservices.libs.trueTime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class trueTimeEncryption {

    public static String S_FERNET_KEY = "W#ZYBHQa9G_DB_iU@yjA3Es@COu4-UzU";
    public static String S_APP_BLOCK_KEY = "D~S=05y68#M77oj]vprm}9HE))Xp'VX?[p|m-Wg`mrg^";
    private static trueTimeEncryption ourInstance = new trueTimeEncryption();
    public static trueTimeEncryption getInstance()
    {
        return ourInstance;
    }

    public String getSecretToken() throws UnsupportedEncodingException {
        String mTime = String.valueOf(System.currentTimeMillis()/1000);
        com.hiddenservices.onionservices.libs.fernet.Key mKey = new com.hiddenservices.onionservices.libs.fernet.Key(android.util.Base64.encodeToString(S_FERNET_KEY.getBytes(), android.util.Base64.DEFAULT).replace("\n",""));
        com.hiddenservices.onionservices.libs.fernet.Token mToken = com.hiddenservices.onionservices.libs.fernet.Token.generate(mKey, S_APP_BLOCK_KEY + "----" + mTime);
        return URLEncoder.encode(mToken.serialise());
    }
}
