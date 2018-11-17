package fr.wildcodeschool.vyfe.repository;


import fr.wildcodeschool.vyfe.model.LicenceModel;

public class LicenceRepository extends  FirebaseDatabaseRepository<LicenceModel> {


    public LicenceRepository(String companyId) {
        super(new LicenseMapper(), companyId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/Licenses/";
    }

}
