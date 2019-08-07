package com.example.travelmantics.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travelmantics.R;
import com.example.travelmantics.activity.InsertActivity;
import com.example.travelmantics.models.Deal;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {
    private static final String TAG = DealAdapter.class.getSimpleName();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<Deal> deals;
    private ChildEventListener childEventListener;

    public DealAdapter(){
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        deals = FirebaseUtil.dealArrayList;

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Deal deal = dataSnapshot.getValue(Deal.class);
                deal.set_id(dataSnapshot.getKey());
                deals.add(deal);
                notifyItemInserted(deals.size() - 1);
                Log.i(TAG, deal.toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Deal deal = dataSnapshot.getValue(Deal.class);
                Log.i(TAG, deal.getName());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Deal deal = dataSnapshot.getValue(Deal.class);
                Log.i(TAG, deal.getName() +" has been removed");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);


    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.single_row, parent, false);

        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        Deal d = deals.get(position);
        holder.name.setText(d.getName());
        holder.description.setText(d.getDescription());
        holder.price.setText(d.getPrice());
        holder.showImage(d.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name, description, price;
        public ImageView img;

        public DealViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.deal_name);
            description = (TextView) itemView.findViewById(R.id.deal_description);
            price = (TextView) itemView.findViewById(R.id.deal_price);
            img = itemView.findViewById(R.id.deal_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Deal deal = deals.get(position);
            Intent intent = new Intent(v.getContext(), InsertActivity.class);
            intent.putExtra("deal", deal);
            v.getContext().startActivity(intent);
        }

        private void showImage(String url) {
            if (url != null && url.isEmpty() == false) {
                Picasso.get()
                        .load(url)
                        .resize(80,80)
                        .centerCrop()
                        .into(img);
            }
        }
    }
}
