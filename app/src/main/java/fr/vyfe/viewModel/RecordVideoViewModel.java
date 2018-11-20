package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Environment;

import java.io.File;
import java.util.Date;

import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.SessionRepository;

import static android.os.Environment.DIRECTORY_MOVIES;

public class RecordVideoViewModel extends ViewModel {

    private SessionModel session;
    private SessionRepository sessionRepository;
    private MutableLiveData<Integer> touchTagPosition;
    private MutableLiveData<String> stepRecord;
    private MutableLiveData<Long> chronometer;
    private MutableLiveData<Integer> count;

    public RecordVideoViewModel(String companyId) {
        sessionRepository = new SessionRepository(companyId);
        touchTagPosition = new MutableLiveData<>();
        stepRecord = new MutableLiveData<>();
        stepRecord.setValue("init");
        chronometer = new MutableLiveData<>();
        chronometer.setValue((long)0);
        count = new MutableLiveData<>();
        count.setValue(0);
    }

    public void init(SessionModel session) {
        this.session = session;
        session.setDate(new Date());

        File f1 = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/" + "Vyfe");
        if (!f1.exists()) {
            f1.mkdirs();
        }
        String mFileName = String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/" + "Vyfe");
        mFileName += "/" + session.getName() + " - " + (new Date()).getTime() + ".mp4";
        session.setDeviceVideoLink(mFileName);
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
    public String save(){
        stepRecord.setValue("save");
        String key = sessionRepository.push(session);
        session.setId(key);
        return key;
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
