package com.darkweb.genesissearchengine.helperManager;

import com.darkweb.genesissearchengine.constants.enums;

import java.util.List;

public class eventObserver
{
    public interface eventListener
    {
        Object invokeObserver(List<Object> data, enums.etype event_type);
    }
}