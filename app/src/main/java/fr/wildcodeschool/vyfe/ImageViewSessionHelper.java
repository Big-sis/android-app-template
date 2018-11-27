package fr.wildcodeschool.vyfe;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

public class ImageViewSessionHelper {
    public static Bitmap thumbnailSession(String fileLink){
        return ThumbnailUtils.createVideoThumbnail(fileLink, MediaStore.Images.Thumbnails.MINI_KIND);
    }
}
