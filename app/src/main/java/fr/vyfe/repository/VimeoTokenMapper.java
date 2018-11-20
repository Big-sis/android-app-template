package fr.vyfe.repository;

import java.util.HashMap;
import java.util.List;

import fr.vyfe.entity.VimeoTokenEntity;
import fr.vyfe.mapper.FirebaseMapper;
import fr.vyfe.model.VimeoTokenModel;

public class VimeoTokenMapper extends FirebaseMapper<VimeoTokenEntity, VimeoTokenModel> {

    @Override
    public VimeoTokenModel map(VimeoTokenEntity vimeoTokenEntity, String key) {
        VimeoTokenModel vimeoToken = new VimeoTokenModel();
        vimeoToken.setVimeoAccessToken(vimeoTokenEntity.getVimeoAccessToken());
        return vimeoToken;
    }

    @Override
    public VimeoTokenEntity unMap(VimeoTokenModel vimeoTokenModel) {
        return null;
    }

    @Override
    public HashMap<String, VimeoTokenEntity> unMapList(List<VimeoTokenModel> to) {
        return null;
    }
}