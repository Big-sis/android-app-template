package fr.vyfe.repository;


import android.os.Environment;

import com.google.common.hash.Hashing;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;

import fr.vyfe.mapper.SessionMapper;
import fr.vyfe.model.SessionModel;

import static android.os.Environment.DIRECTORY_MOVIES;

public class SessionRepository extends FirebaseDatabaseRepository<SessionModel> {

    public SessionRepository(String companyId) {
        super(new SessionMapper(), companyId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/Sessions/";
    }

    public String push(SessionModel sessionModel, String androidId) throws Exception {
        //TODO pourquoi pas mettre date, auteur etc de fixe
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
        File f1 = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/" + "Vyfe");
        if (!f1.exists()) {
            f1.mkdirs();
        }
        String mFileName = String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/" + "Vyfe");
        mFileName += "/" + sessionName + "-" + (new Date()).getTime() + ".mp4";
        String DeviceVideoLink = mFileName;
        File file = new File(DeviceVideoLink);

        return file.getAbsolutePath();
    }
}
