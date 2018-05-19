package com.example.ahmed.sqlitedatabase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_CODE_GALLERY = 999;
    SqliteHelper sqliteHelper;
    EditText edit_Disease, edit_Medicine, edit_Details;
    ImageButton img_camera, img_device;
    Button btn_add, btn_show;
    ImageView img_Medicine;

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_Disease = (EditText) findViewById(R.id.edit_disease);
        edit_Medicine = (EditText) findViewById(R.id.edit_medicine);
        edit_Details = (EditText) findViewById(R.id.edit_details);
        img_Medicine = (ImageView) findViewById(R.id.img_medicine);

        img_camera = (ImageButton) findViewById(R.id.img_camera);
        img_device = (ImageButton) findViewById(R.id.img_device);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_show = (Button) findViewById(R.id.btn_show);

        sqliteHelper = new SqliteHelper(this);

        Animation anim_alpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        Animation anim_rotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        img_camera.setAnimation(anim_alpha);
        img_device.setAnimation(anim_alpha);
        btn_add.setAnimation(anim_alpha);
        btn_show.setAnimation(anim_alpha);
        img_Medicine.setAnimation(anim_alpha);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_setting) {
            Toast.makeText(this, "قاعده بيانات تتكون من اسم المرض واسم العلاج و صوره للعلاج وبعض التفاصيل", Toast.LENGTH_LONG).show();
        } else if (item.getItemId() == R.id.item_browse_internet) {
            Intent intent_google = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(intent_google);
        }
        return super.onOptionsItemSelected(item);
    }

    public void btn_save(View view) {
        try {
            if (edit_Disease.getText().toString().matches("")) {
                edit_Disease.setText(getString(R.string.name_disease_null));
            }
            if (edit_Medicine.getText().toString().matches("")) {
                edit_Medicine.setText(getString(R.string.name_medicine_null));
            }
            if (edit_Details.getText().toString().matches("")) {
                edit_Details.setText(getString(R.string.details_null));
            }

            sqliteHelper.add(
                    edit_Disease.getText().toString().trim(),
                    edit_Medicine.getText().toString().trim(),
                    edit_Details.getText().toString().trim(),
                    imageViewToByte(img_Medicine)
            );
            Toast.makeText(getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
            edit_Disease.setText("");
            edit_Medicine.setText("");
            edit_Details.setText("");
            img_Medicine.setImageResource(R.drawable.main);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 111);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                img_Medicine.setImageBitmap(bitmap);
            } else if (requestCode == 111 && resultCode == RESULT_OK) {

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                img_Medicine.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void btn_show(View view) {
        Intent intent = new Intent(MainActivity.this, Show_Data.class);
        startActivity(intent);
    }

    public void capture_photo(View view) {
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{Manifest.permission.CAMERA},
                111
        );
    }

    public void choose_photo(View view) {
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY
        );
    }
}
