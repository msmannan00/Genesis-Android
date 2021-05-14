package com.darkweb.genesissearchengine;

import com.darkweb.genesissearchengine.constants.enums;

import java.util.List;

public class eventObserver
{
    public interface eventListener
    {
        Object invokeObserver(List<Object> data, Object event_type);
    }
}
