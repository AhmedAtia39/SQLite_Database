package com.example.ahmed.sqlitedatabase;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Show_Data extends AppCompatActivity {

    SqliteHelper sqliteHelper;
    MyAdapter myAdapter;
    ArrayList<List_medicine> items;
    ListView listView;
    ImageView img_Medicine;
    EditText edit_disease, edit_medicine, edit_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__data);

        listView = (ListView) findViewById(R.id.list_item);
        sqliteHelper = new SqliteHelper(this);
        items = new ArrayList<>();

        display_listDisease();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(Show_Data.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // update
                            Cursor c = sqliteHelper.getData("SELECT id FROM medicines");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            // show dialog update at here
                            showDialogUpdate(Show_Data.this, arrID.get(position));

                        } else {

                            Cursor c = sqliteHelper.getData("SELECT id FROM medicines");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }

                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    private void showDialogUpdate(Activity activity, final int position) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_item);
        dialog.setTitle("Update");

        img_Medicine = dialog.findViewById(R.id.img_medicine_update);
        edit_disease = dialog.findViewById(R.id.edit_disease);
        edit_medicine = dialog.findViewById(R.id.edit_medicine);
        edit_details = dialog.findViewById(R.id.edit_details);

        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);

        edit_disease.setText(sqliteHelper.getItemToUpdate(position));
        edit_medicine.setText(sqliteHelper.medicine);
        edit_details.setText(sqliteHelper.details);

        // تحويل ال بايت الي بيتماب
        Bitmap bmp = BitmapFactory.decodeByteArray(sqliteHelper.image, 0, sqliteHelper.image.length);
        img_Medicine.setImageBitmap(bmp);

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        img_Medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
                ActivityCompat.requestPermissions(
                        Show_Data.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (edit_disease.getText().toString().matches("")) {
                        edit_disease.setText(getString(R.string.name_disease_null));
                    }
                    if (edit_medicine.getText().toString().matches("")) {
                        edit_medicine.setText(getString(R.string.name_medicine_null));
                    }
                    if (edit_details.getText().toString().matches("")) {
                        edit_details.setText(getString(R.string.details_null));
                    }
                    sqliteHelper.update(
                            Integer.toString(position),
                            edit_disease.getText().toString().trim(),
                            edit_medicine.getText().toString().trim(),
                            edit_details.getText().toString().trim(),
                            MainActivity.imageViewToByte(img_Medicine)

                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update successfully!!!", Toast.LENGTH_SHORT).show();
                } catch (Exception error) {
                    Log.e("Update error", error.getMessage());
                }
                display_listDisease();
            }
        });
    }

    private void showDialogDelete(final int idPerson) {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Show_Data.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to this delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sqliteHelper.delete(idPerson);
                    Toast.makeText(getApplicationContext(), "Delete successfully!!!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                display_listDisease();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    public ArrayList display_listDisease() {
        ArrayList arr = sqliteHelper.show_all_Data();
        myAdapter = new MyAdapter(arr);
        listView.setAdapter(myAdapter);
        return arr;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 888 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                img_Medicine.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList arr = sqliteHelper.getDiseaseSearch(newText);
                myAdapter = new MyAdapter(arr);
                listView.setAdapter(myAdapter);

                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    public class MyAdapter extends BaseAdapter {
        ArrayList<List_medicine> items = new ArrayList<>();

        public MyAdapter(ArrayList<List_medicine> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i).getDisease_name();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = getLayoutInflater().inflate(R.layout.row_item, null);

            TextView txtDisease = v.findViewById(R.id.txtDisease);
            TextView txtMedicine = v.findViewById(R.id.txtMedicine);
            TextView txtDetails = v.findViewById(R.id.txtDetails);
            ImageView img_medicine = v.findViewById(R.id.img_medicine);


            txtDisease.setText(items.get(i).getDisease_name());
            txtMedicine.setText(items.get(i).getMedicine_name());
            txtDetails.setText(items.get(i).getDetails());

            byte[] medicineImage = items.get(i).getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(medicineImage, 0, medicineImage.length);
            img_medicine.setImageBitmap(bitmap);

            return v;
        }
    }
}
