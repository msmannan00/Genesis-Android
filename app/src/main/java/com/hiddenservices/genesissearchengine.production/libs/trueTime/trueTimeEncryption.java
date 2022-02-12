package com.hiddenservices.genesissearchengine.production.libs.trueTime;

import com.instacart.library.truetime.TrueTime;
import com.macasaet.fernet.Key;
import com.macasaet.fernet.Token;
import java.util.Base64;

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


    public String getSecretToken() {
        Key mkey = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mkey = new Key(Base64.getUrlEncoder().encodeToString(S_FERNET_KEY.getBytes()));
        }
        Token token = Token.generate(mkey, S_APP_BLOCK_KEY + "----" + System.currentTimeMillis()/1000);
        return token.serialise();
    }
}
