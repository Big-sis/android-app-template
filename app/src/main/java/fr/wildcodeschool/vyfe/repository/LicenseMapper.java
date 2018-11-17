package fr.wildcodeschool.vyfe.repository;


import java.util.HashMap;
import java.util.List;

import fr.wildcodeschool.vyfe.entity.LicenseEntity;
import fr.wildcodeschool.vyfe.model.LicenceModel;

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

}
