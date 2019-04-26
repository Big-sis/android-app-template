package fr.vyfe.helper;

import java.util.ArrayList;
import java.util.List;

import fr.vyfe.model.TagSetModel;

public class TagSetsFilteredHelper {

    public static ArrayList<TagSetModel> tagSetByAuthorAndShared(List<TagSetModel> result, String userId){
        ArrayList<TagSetModel> tagSetModels = new ArrayList<>();
        for (TagSetModel tagSet : result) {
            //TagsSets Author
            if(tagSet.getAuthor()!=null && tagSet.getAuthor().getUid()!= null){
                if(tagSet.getAuthor().getUid().equals(userId)){
                    tagSetModels.add(tagSet);
                }
            }

            //TagsSets shared
            if (tagSet.getAuthor()!= null && tagSet.getAuthor().getUid()!= null &&!tagSet.getAuthor().equals(userId) && tagSet.isShared()) {
                tagSetModels.add(tagSet);
            }

        }
        return tagSetModels;
    }
}
