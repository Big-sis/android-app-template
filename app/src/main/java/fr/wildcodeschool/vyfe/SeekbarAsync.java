package fr.wildcodeschool.vyfe;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.VideoView;

public class SeekbarAsync extends AsyncTask<Void, Integer, Void> {
    int duration = 0;
    int current = 0;
    SeekBar seekBar;
    VideoView videoView;
    private int previous = -1;

    public SeekbarAsync(SeekBar seekBar, VideoView videoView) {
        this.seekBar = seekBar;
        this.videoView = videoView;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        videoView.start();
        duration = videoView.getDuration();
        videoView.pause();
        do {
            current = videoView.getCurrentPosition();
            try {
                publishProgress((int) (current * 100 / duration));
                if (seekBar.getProgress() >= 100) {

                    break;
                }
            } catch (Exception e) {
            }
        } while (seekBar.getProgress() <= 100);

        videoView.pause();
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] != previous) {
            previous = values[0];
            Log.d("DEBUGTIMELINE", "onProgressUpdate: " + String.valueOf(values[0]));
        }
        seekBar.setProgress(values[0]);
    }
}


