package fr.vyfe.repository;


import android.os.Environment;

import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.firebase.database.ServerValue;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fr.vyfe.Constants;
import fr.vyfe.entity.SessionEntity;
import fr.vyfe.mapper.SessionMapper;
import fr.vyfe.model.SessionModel;

import static android.os.Environment.DIRECTORY_MOVIES;

public class SessionRepository extends FirebaseDatabaseRepository<SessionModel> {

    public SessionRepository(String companyId) {
        super(new SessionMapper(), companyId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/" + Constants.BDDV2_SESSIONS_KEY + "/";
    }

    public String push(SessionModel sessionModel, String androidId) throws Exception {
        sessionModel.setIdAndroid(Hashing.sha256().hashString(androidId, Charset.defaultCharset()).toString());
        String filePath = createFile(sessionModel.getName());
        if (filePath != null) {
            sessionModel.setDeviceVideoLink(filePath);
            return super.push(sessionModel);
        } else throw new Exception("session cannot be created. No storage left on device");
    }

    /**
     * Create file to record video and return absolute path
     *
     * @param sessionName
     * @return file absolute path
     */
    private String createFile(String sessionName) {
        File f1 = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/" + Constants.VIDEO_DIRECTORY_NAME);
        if (!f1.exists()) {
            f1.mkdirs();
        }
        String mFileName = String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/" + Constants.VIDEO_DIRECTORY_NAME);
        mFileName += "/" + sessionName + "-" + (new Date()).getTime() + ".mp4";
        String DeviceVideoLink = mFileName;
        File file = new File(DeviceVideoLink);

        return file.getAbsolutePath();
    }

    public Task<Void> update(SessionModel model) {
        return databaseReference.child(model.getId()).updateChildren(((SessionEntity) mapper.unMap(model)).toHashmap());
    }

    public Task<Void> deleteObservers(SessionModel model) {
        Map<String, Object> observers = new HashMap<>();
        observers.put("observers", null);
        return databaseReference.child(model.getId()).updateChildren(observers);
    }

    public void setTimestamp(SessionModel model) {
        Map<String, Object> time = new HashMap<>();
        time.put("timestamp", ServerValue.TIMESTAMP);
        databaseReference.child(model.getId()).updateChildren(time);
    }
}
