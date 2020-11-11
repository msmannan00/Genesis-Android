package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;

import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("CommitPrefEdits")
class tabDataModel
{
    private ArrayList<tabRowModel> mTabs = new ArrayList<>();

    /*List Tabs*/

    void addTabs(geckoSession mSession,boolean isHardCopy){
        if(!isHardCopy){
            mTabs.add(0,new tabRowModel(mSession,mTabs.size()));
        }
        else {
            mTabs.add(0,new tabRowModel(mSession,mTabs.size()));
        }
    }
    ArrayList<tabRowModel> getTab(){
        return mTabs;
    }
    void clearTab() {
        int size = mTabs.size();
        for(int counter = 0; counter< size; counter++){
            mTabs.get(0).getSession().stop();
            mTabs.remove(0);
        }
        if(mTabs.size()>0){
            mTabs.get(0).getSession().closeSession();
        }
    }
    void closeTab(geckoSession mSession) {
        for(int counter = 0; counter< mTabs.size(); counter++){
            if(mTabs.get(counter).getSession().getSessionID()==mSession.getSessionID())
            {
                mTabs.remove(counter);
                break;
            }
            else {
                mTabs.get(counter).setId(mTabs.get(counter).getmId()+1);
            }
        }
    }
    void moveTabToTop(geckoSession mSession) {

        for(int counter = 0; counter< mTabs.size(); counter++){

            if(mTabs.get(counter).getSession().getSessionID()==mSession.getSessionID())
            {
                /* BIG PROBLEM */
                mTabs.remove(counter);
                mTabs.add(0,new tabRowModel(mSession,0));
                break;
            }else {
                mTabs.get(counter).setId(mTabs.get(counter).getmId()+1);
            }
        }
    }
    tabRowModel getCurrentTab(){
        if(mTabs.size()>0){
            return mTabs.get(0);
        }
        else {
            return null;
        }
    }
    int getTotalTabs(){
        return mTabs.size();
    }

    /*List Suggestion*/
    public Object onTrigger(dataEnums.eTabCommands p_commands, List<Object> p_data){
        if(p_commands == dataEnums.eTabCommands.GET_TOTAL_TAB){
            return getTotalTabs();
        }
        else if(p_commands == dataEnums.eTabCommands.GET_CURRENT_TAB){
            return getCurrentTab();
        }
        else if(p_commands == dataEnums.eTabCommands.MOVE_TAB_TO_TOP){
            moveTabToTop((geckoSession)p_data.get(0));
        }
        else if(p_commands == dataEnums.eTabCommands.CLOSE_TAB){
            closeTab((geckoSession)p_data.get(0));
        }
        else if(p_commands == dataEnums.eTabCommands.M_CLEAR_TAB){
            clearTab();
        }
        else if(p_commands == dataEnums.eTabCommands.M_CLOSE_TAB_PARAMETERIZED){
            closeTab((geckoSession)p_data.get(0));
        }
        else if(p_commands == dataEnums.eTabCommands.M_ADD_TAB){
            addTabs((geckoSession)p_data.get(0), (boolean)p_data.get(1));
        }
        else if(p_commands == dataEnums.eTabCommands.GET_TAB){
            return getTab();
        }

        return null;
    }

}
