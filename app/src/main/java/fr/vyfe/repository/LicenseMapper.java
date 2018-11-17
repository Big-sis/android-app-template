package fr.vyfe.repository;


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

}
