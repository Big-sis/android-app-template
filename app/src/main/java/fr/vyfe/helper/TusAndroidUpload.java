package fr.vyfe.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.tus.java.client.TusUpload;

public class TusAndroidUpload extends TusUpload {

    public TusAndroidUpload(Uri uri, ContextWrapper context) throws FileNotFoundException {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{OpenableColumns.SIZE, OpenableColumns.DISPLAY_NAME}, null, null, null);

        if(cursor == null) {
            throw new FileNotFoundException();
        }

        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String name = cursor.getString(nameIndex);

        // On some files, ContentResolver#query will report the wrong filesize
        // even though the InputStream reads the correct length. This discrepancy
        // causes mismatching upload offsets when the upload should be finished.
        // Using the stat size from a file descriptor seems to always report the
        // correct filesize.
        // See https://github.com/tus/tus-android-client/issues/25
        // See https://stackoverflow.com/questions/21882322/how-to-correctly-get-the-file-size-of-an-android-net-uri
        ParcelFileDescriptor fd = resolver.openFileDescriptor(uri, "r");
        if(fd == null) {
            throw new FileNotFoundException();
        }
        long size = fd.getStatSize();
        try {
            fd.close();
        } catch (IOException e) {
            Log.e("TusAndroidUpload", "unable to close ParcelFileDescriptor", e);
        }

        setSize(size);
        setInputStream(resolver.openInputStream(uri));

        setFingerprint(String.format("%s-%d", uri.toString(), size));

        Map<String, String> metadata = new HashMap<>();
        metadata.put("filename", name);
        setMetadata(metadata);

        cursor.close();
    }

    public TusAndroidUpload(File file, ContextWrapper context) throws FileNotFoundException {
        this(getImageContentUri(context, file), context);
    }

    private static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}
