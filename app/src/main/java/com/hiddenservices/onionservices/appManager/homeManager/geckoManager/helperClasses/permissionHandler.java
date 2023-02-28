package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses;

import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_OPEN_CICADA;

import android.Manifest;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class permissionHandler {

    private WeakReference<AppCompatActivity> mContext;
    public static permissionHandler ourInstance = new permissionHandler();

    /*Initializations*/
    public static permissionHandler getInstance() {
        return ourInstance;
    }

    public void onInitPermissionHandler(WeakReference<AppCompatActivity> pContext){
        mContext = pContext;
    }

    public void checkPermission(Callable<Void> pMethodParam){
        DexterBuilder.Permission mPermission = Dexter.withContext(mContext.get());
        DexterBuilder.MultiPermissionListener mListener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mListener = mPermission.withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.MEDIA_CONTENT_CONTROL,
                    Manifest.permission.MANAGE_MEDIA,
                    Manifest.permission.ACCESS_MEDIA_LOCATION,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mListener = mPermission.withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.MEDIA_CONTENT_CONTROL,
                    Manifest.permission.MANAGE_MEDIA,
                    Manifest.permission.ACCESS_MEDIA_LOCATION,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO);
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            mListener = mPermission.withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.MEDIA_CONTENT_CONTROL,
                    Manifest.permission.ACCESS_MEDIA_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            );
        }else {
            mListener = mPermission.withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.MEDIA_CONTENT_CONTROL,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            );
        }
        mListener.withListener(new MultiplePermissionsListener() {
             @Override
             public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                try {
                    pMethodParam.call();
                } catch (Exception e) {}
             }

             @Override
             public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                 // pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_OPEN_CICADA);
             }
        }).check();
    }

}
