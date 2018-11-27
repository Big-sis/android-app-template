package fr.vyfe.repository;

import fr.vyfe.entity.CompanyEntity;
import fr.vyfe.mapper.FirebaseMapper;
import fr.vyfe.model.CompanyModel;

public class CompanyMapper extends FirebaseMapper<CompanyEntity, CompanyModel> {

    @Override
    public CompanyModel map(CompanyEntity companyEntity, String key) {
        CompanyModel companyModel = new CompanyModel();
        companyModel.setId(key);
        companyModel.setVimeoAccessToken(companyEntity.getVimeoAccessToken());
        return companyModel;
    }

    @Override
    public CompanyEntity unMap(CompanyModel companyModel) {
        return null;
    }

}