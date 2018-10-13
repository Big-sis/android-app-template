package fr.wildcodeschool.vyfe;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.wildcodeschool.vyfe.view.StopwatchView;

import static org.junit.Assert.*;

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