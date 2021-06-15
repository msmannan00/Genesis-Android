package com.widget.Genesis;

import java.util.List;

public class eventObserver
{
    public interface eventListener
    {
        Object invokeObserver(List<Object> data, Object event_type);
    }
}
