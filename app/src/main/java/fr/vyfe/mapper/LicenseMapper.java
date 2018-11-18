package fr.vyfe.mapper;


import java.util.HashMap;
import java.util.List;

import fr.vyfe.entity.LicenseEntity;
import fr.vyfe.model.LicenceModel;

public class LicenseMapper extends FirebaseMapper<LicenseEntity, LicenceModel> {
    @Override
    public LicenceModel map(LicenseEntity licenseEntity, String key) {
        LicenceModel license = new LicenceModel();

        license.setIdLicence(key);
        license.setStart(licenseEntity.getStart());
        license.setEnd(licenseEntity.getEnd());
        license.setUserId(licenseEntity.getIdUser());

        return license;
    }

    @Override
    public LicenseEntity unMap(LicenceModel licenceModel) {
        return null;
    }

    @Override
    public HashMap<String, LicenseEntity> unMapList(List<LicenceModel> to) {
        return null;
    }

}
