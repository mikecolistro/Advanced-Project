package com.lakehead.textbookmarket;

/**
 * A simple interface used to ensure objects implement a callback function that can be used with AsyncTasks.
 */
public interface OnTaskCompleted
{
    void onTaskCompleted(Object obj);
}