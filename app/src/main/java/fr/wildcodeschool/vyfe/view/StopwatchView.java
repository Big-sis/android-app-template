package fr.wildcodeschool.vyfe.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;


/**
 * This class Overrides the Chronometer widget from android standard widgets library with user
 * friendly methods to run a stop watch.
 *
 * for example, when you run stop method and then start, the stop watch resume form the time you
 * stopped it.
 */
public class StopwatchView extends Chronometer {

    private long latestStop;

    public StopwatchView(Context context) {
        this(context, null, 0);
    }

    public StopwatchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StopwatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTime(0);
        latestStop = getTime();
    }


    public long getTime() {
        return SystemClock.elapsedRealtime() - this.getBase();
    }

    @Override
    public void start() {
        super.start();
        setTime(latestStop);
    }

    @Override
    public void stop() {
        super.stop();
        latestStop = getTime();
    }

    public void setTime(long t) {
        setBase(SystemClock.elapsedRealtime() - t);
        latestStop = getTime();
    }
}
