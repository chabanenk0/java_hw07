package com.github.chabanenk0;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dmitry on 30.12.16.
 */
public class MainTest
{
    @Test
    public void MainTest()
    {
        try {
            Main main = new Main();
            assertTrue(true);
        } catch (Exception exception) {
            assertTrue(false);
        }
    }
}
