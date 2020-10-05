package com.darkweb.genesissearchengine.dataManager;

import android.widget.ImageView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class imageCacheModel {

    private Map<String, ImageView> m_image_cache;

    public imageCacheModel(){
        m_image_cache = new HashMap<>();
    }

    public void saveImage(String p_host_url, ImageView p_image){
        m_image_cache.put(p_host_url, p_image);
    }

    public ImageView getImage(String p_host_url){
        return m_image_cache.get(p_host_url);
    }

    public void clearImages(){

    }

    public void clearOldImages(){

    }

    public Object onTrigger(dataEnums.eImageCacheCommands p_commands, List<Object> p_data){
        if(p_commands == dataEnums.eImageCacheCommands.M_SET_IMAGE){
            saveImage((String) p_data.get(0), (ImageView)p_data.get(1));
        }
        else if(p_commands == dataEnums.eImageCacheCommands.M_GET_IMAGE){
            return getImage((String) p_data.get(0));
        }
        else if(p_commands == dataEnums.eImageCacheCommands.M_CLEAR_IMAGE){
            clearImages();
        }
        else if(p_commands == dataEnums.eImageCacheCommands.M_CLEAR_OLD_IMAGES){
            clearOldImages();
        }

        return null;
    }
}
