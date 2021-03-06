package com.lucien3344.imageselector.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lucien3344.imageselector.config.CameraConfig;
import com.lucien3344.imageselector.objet.Image;
import com.lucien3344.imageselector.utils.DebugUtil;
import com.lucien3344.imageselector.utils.FileUtils;
import com.lucien3344.imageselector.utils.StatusBarUtil;

import java.io.File;
import java.util.List;

/**
 * 照相机
 *
 * @author lsh_2012@qq.com
 * on 2019/10/22.
 */
public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 5;
    private static final int IMAGE_CROP_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private File cropImageFile;
    private File tempPhotoFile;

    private CameraConfig config;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.compatTransStatusBar(this, 0x33333333);
        super.onCreate(savedInstanceState);
        config = (CameraConfig) getIntent().getSerializableExtra("config");
        if (config == null)
            return;
        startCamera();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
            return;
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            tempPhotoFile = new File(FileUtils.createRootPath(this) + "/" + System.currentTimeMillis() + ".jpg");
            DebugUtil.e(tempPhotoFile.getAbsolutePath());
            FileUtils.createFile(tempPhotoFile);

            Uri uri = FileProvider.getUriForFile(this,
                    FileUtils.getApplicationId(this) + ".image_provider", tempPhotoFile);

            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); //Uri.fromFile(tempFile)
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "无法启动相机！", Toast.LENGTH_SHORT).show();
        }
    }

    private void crop(String imagePath) {
        cropImageFile = new File(FileUtils.createRootPath(this) + "/" + System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getImageContentUri(new File(imagePath)), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", config.aspectX);
        intent.putExtra("aspectY", config.aspectY);
        intent.putExtra("outputX", config.outputX);
        intent.putExtra("outputY", config.outputY);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropImageFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, IMAGE_CROP_CODE);
    }

    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                if (cursor != null) {
                    cursor.close();
                }
                return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private void complete(Image image) {
        Intent intent = new Intent();
        if (image != null) {
            intent.putExtra("result", image.path);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CROP_CODE && resultCode == RESULT_OK) {
            complete(new Image(cropImageFile.getPath(), cropImageFile.getName()));
        } else if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (tempPhotoFile != null) {
                    if (config.needCrop) {
                        crop(tempPhotoFile.getAbsolutePath());
                    } else {
                        // complete(new Image(cropImageFile.getPath(), cropImageFile.getName()));
                        complete(new Image(tempPhotoFile.getPath(), tempPhotoFile.getName()));
                    }
                }
            } else {
                if (tempPhotoFile != null && tempPhotoFile.exists()) {
                    tempPhotoFile.delete();
                }
                finish();
            }
        } else {
            finish();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length >= 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    Toast.makeText(this, "获取拍照权限失败，无法启动相机！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
