package com.example.travelmantics.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelmantics.R;
import com.example.travelmantics.models.Deal;
import com.example.travelmantics.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class InsertActivity extends AppCompatActivity {
    private static final String TAG = InsertActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 20;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    EditText name, price, description;
    Deal deal;
    boolean update;
    Button btn;
    String image_uri;
    ImageView img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        btn = findViewById(R.id.button3);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("deals");
        img = findViewById(R.id.imageView);
        if(getIntent().hasExtra("deal")){
            deal = getIntent().getParcelableExtra("deal");
            name.setText(deal.getName());
            price.setText(deal.getPrice());
            description.setText(deal.getDescription());
            update = true;
            showImage(deal.getImageUrl());
        }


        btn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "choose image"), REQUEST_CODE);
            }
        });
    }

    public boolean validate(){
        boolean status = true;
        if(name.getText().toString().equals("")){
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_LONG);
            status = false;
        }

        if(price.getText().toString().equals("")){
            Toast.makeText(this, "Price value cannot be empty", Toast.LENGTH_LONG);
            status = false;
        }
        return status;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_btn:
                if(validate()) {
                    if (update){
                        updateDeal(deal);
                    }else {
                        saveDeal();
                    }
                    clear();
                    backToList();
                }
                break;
            case R.id.delete:
                deleteDeal();
                backToList();
                break;
            default: super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void saveDeal(){
        String deal_name = name.getText().toString();
        String deal_amount = price.getText().toString();
        String desc = description.getText().toString();
        deal = new Deal(deal_name, deal_amount, desc);
        deal.setImageUrl(image_uri);
        databaseReference.push().setValue(deal);
        showToast("deal inserted");
    }

    private void deleteDeal(){
        if(deal.get_id() == null){
            showToast("No deal to delete");
        }else{
            databaseReference.child(deal.get_id()).removeValue();
        }
    }

    private void updateDeal(Deal deal){
        Log.i(TAG, deal.get_id());
        databaseReference.child(deal.get_id()).setValue(deal);
        showToast("deal updated");
    }


    private void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void clear(){
        name.setText("");
        price.setText("");
        description.setText("");
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
           Uri file = data.getData();
            final StorageReference ref = FirebaseUtil.storageReference.child(file.getLastPathSegment());
            ref.putFile(file).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
//                            Log.i(TAG, uri.toString());
                            image_uri = uri.toString();
                            showImage(uri.toString());
                        }
                    });
                }
            });
        }
    }
    private void showImage(String url) {
        if (url != null && url.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(image_uri)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(img);
        }
    }
}
