package com.hiddenservices.onionservices.appManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static com.hiddenservices.onionservices.constants.keys.EXTERNAL_SHORTCUT_COMMAND_NAVIGATE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.helperManager.helperMethod;

import org.torproject.android.service.wrapper.orbotExternalCommands;

public class orbotRequestManager extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();

        int m_command = getIntent().getIntExtra("command", 0);
        if(m_command == orbotExternalCommands.S_NEW_CIRCUIT){
            Intent mIntent = new Intent(this, homeController.class);
            helperMethod.openIntent(mIntent, this, constants.CONST_LIST_EXTERNAL_SHORTCUT);
            activityContextManager.getInstance().getHomeController().onNewCircuitInvoked();
        }
        else if(m_command == orbotExternalCommands.S_NOTIFICATION_SETTINGS){
            try {
                Intent intent;
                intent = new Intent(this, Class.forName("com.hiddenservices.onionservices.appManager.settingManager.notificationManager.settingNotificationController"));
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
