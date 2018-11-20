package fr.wildcodeschool.vyfe.repository;

import java.util.HashMap;
import java.util.List;
import fr.wildcodeschool.vyfe.entity.VimeoTokenEntity;
import fr.wildcodeschool.vyfe.model.VimeoTokenModel;

public class VimeoTokenMapper extends FirebaseMapper<VimeoTokenEntity, VimeoTokenModel> {

    @Override
    public VimeoTokenModel map(VimeoTokenEntity vimeoTokenEntity, String key) {
        VimeoTokenModel vimeoToken = new VimeoTokenModel();
        vimeoToken.setVimeoAccessToken(vimeoTokenEntity.getVimeoAccessToken());


        return vimeoToken;
    }

    @Override
    public List<VimeoTokenModel> mapList(HashMap<String, VimeoTokenEntity> from) {
        return null;
    }

}