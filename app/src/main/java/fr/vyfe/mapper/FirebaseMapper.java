package fr.vyfe.mapper;

import com.google.firebase.database.DataSnapshot;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.vyfe.model.VyfeModel;

public abstract class FirebaseMapper<Entity, Model extends VyfeModel> implements IMapper<Entity, Model> {

    public Model map(DataSnapshot dataSnapshot, String key) {
        Entity entity = dataSnapshot.getValue(getEntityClass());
        return map(entity,dataSnapshot.getKey());
    }

    public List<Model> mapList(DataSnapshot dataSnapshot) {
        List<Model> list = new ArrayList<>();
        for (DataSnapshot item : dataSnapshot.getChildren()) {
            list.add(map(item,item.getKey()));
        }
        return list;
    }

    @Override
    public ArrayList<Model> mapList(HashMap<String, Entity> from) {
        ArrayList<Model> modelList = new ArrayList<>();
        if (from != null) {
            for (Map.Entry<String, Entity> entry : from.entrySet()) {
                modelList.add(map(entry.getValue(), entry.getKey()));
            }
        }
        return modelList;
    }

    @Override
    public HashMap<String, Entity> unMapList(List<Model> models) {
        if (models == null) return null;
        HashMap<String, Entity> result = new HashMap<>();
        for (Model model : models) {
            result.put(model.getId(), unMap(model));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Class<Entity> getEntityClass() {
        ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<Entity>) superclass.getActualTypeArguments()[0];
    }

}
