package com.bersama.uploadimage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bersama.uploadimage.Api.ApiServices;
import com.bersama.uploadimage.Api.RetroClient;
import com.bersama.uploadimage.Model.ResponseApiModel;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Button btnUpload, btnGallery;
    ImageView ivHolder;
    String part_image;
    ProgressDialog pd;
    private int REQUEST_GALLERY = 9544;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnGallery = (Button) findViewById(R.id.btn_gallery);
        ivHolder = (ImageView) findViewById(R.id.iv_holder);
        pd = new ProgressDialog(this);
        pd.setMessage("tunggu ... ");

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Buka Galeri"), REQUEST_GALLERY);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                File imagefile = new File(part_image);
                final RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-file"), imagefile);
                MultipartBody.Part partImage = MultipartBody.Part.createFormData("imageupload", imagefile.getName(), requestBody);

                ApiServices apiServices = RetroClient.getApiServices();
                Call<ResponseApiModel> upload = apiServices.uploadImage(partImage);
                upload.enqueue(new Callback<ResponseApiModel>() {
                    @Override
                    public void onResponse(Call<ResponseApiModel> call, Response<ResponseApiModel> response) {
                        pd.dismiss();
                        Log.d("RETRO", "onResponse: "+response.body().toString());

                        if (response.body().getKode().equals("1"))
                        {
                            Toast.makeText(MainActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                        }else
                        {
                            Toast.makeText(MainActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseApiModel> call, Throwable t) {
                        Log.d("RETRO", "onFailure: "+ t.getMessage());
                        pd.dismiss();
                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == REQUEST_GALLERY)
            {
                Uri dataimage = data.getData();
                String[] imageprojection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(dataimage, imageprojection, null,null,null);

                if (cursor != null)
                {
                    cursor.moveToFirst();
                    int indexImage = cursor.getColumnIndex(imageprojection[0]);
                    part_image = cursor.getString(indexImage);

                    if (part_image != null)
                    {
                        File image = new File(part_image);
                        ivHolder.setImageBitmap(BitmapFactory.decodeFile(image.getAbsolutePath()));
                    }
                }
            }
        }
    }
}
