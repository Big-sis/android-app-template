package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.os.SystemClock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

public class StopwatchViewTest {

    StopwatchView stopwatch;

    @Mock Context context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        stopwatch = new StopwatchView(context);
    }

    @Test
    public void getTime() {
        assertEquals(0, stopwatch.getTime());
    }

}