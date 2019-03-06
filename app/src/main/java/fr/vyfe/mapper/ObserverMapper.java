package fr.vyfe.mapper;

import fr.vyfe.entity.ObserverEntity;
import fr.vyfe.model.ObserverModel;

public class ObserverMapper extends FirebaseMapper<ObserverEntity, ObserverModel> {


    @Override
    public ObserverModel map(ObserverEntity observerEntity, String key) {
        ObserverModel observerModel = new ObserverModel();
        observerModel.setId(key);
        observerModel.setName(observerEntity.getName());
        return observerModel;
    }

    @Override
    public ObserverEntity unMap(ObserverModel observerModel) {
        ObserverEntity observerEntity = new ObserverEntity();
        observerEntity.setName(observerModel.getName());
        return observerEntity;
    }
}
