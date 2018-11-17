package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.repository.SessionRepository;

public class RecordVideoViewModel extends ViewModel {

    private SessionModel session;
    private SessionRepository sessionRepository;
    private MutableLiveData<Integer> touchTagPosition;
    private MutableLiveData<String> stepRecord;
    private MutableLiveData<Long> chronometer;
    private MutableLiveData<Integer> count;

    public RecordVideoViewModel() {
        touchTagPosition = new MutableLiveData<Integer>();
        stepRecord = new MutableLiveData<>();
        stepRecord.setValue("init");
        chronometer = new MutableLiveData<>();
        chronometer.setValue((long)0);
        count = new MutableLiveData<>();
        count.setValue(0);
    }

    public LiveData<Integer> getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count.setValue(count);
    }

    public LiveData<Integer> getTagPosition() {
        return touchTagPosition;
    }

    public void setTagPosition(Integer tagPosition) {
        this.touchTagPosition.setValue(tagPosition);
    }

    public LiveData<String> isRecording(){
        return stepRecord;
    }

    public void playRecord(){
        stepRecord.setValue("recording");
    }
    public void stop(){
        stepRecord.setValue("stop");
    }
    public void error(){
        stepRecord.setValue("error");
    }
    public void save(){
        stepRecord.setValue("save");
        sessionRepository.push(session).continueWith(new Continuation<Void, Object>() {

            @Override
            public Object then(@NonNull Task<Void> task) throws Exception {
                Object result = task.getResult();
                return result;
            }
        });
    }
    public void close() {stepRecord.setValue("close");}

    public LiveData<Long> getTimeChronometer() {
        return chronometer;
    }

    public void setTimeChronometer(Long chronometer) {
        this.chronometer.setValue(chronometer);
    }

    public SessionModel getSession() {

        if (session == null) {
            session = new SessionModel();
        }
        return session;
    }

}
