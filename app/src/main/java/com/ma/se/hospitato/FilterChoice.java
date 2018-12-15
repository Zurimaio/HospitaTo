package com.ma.se.hospitato;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterChoice extends Fragment {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    String filter;
    private FirebaseRecyclerAdapter<Hospital, RequestViewHolder> adapter;
    public FilterChoice() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Adapter", "start listening");
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Adapter", "stop listening");
        adapter.stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter_choice, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");

        recyclerView = view.findViewById(R.id.myrecycler);

        //Avoid unnecessary layout passes by setting setHasFixedSize to true
        //recyclerView.setHasFixedSize(true);

        filter = getArguments().getString("Department");
        Log.d("Filtered Result",databaseReference.getRef().toString());

        filtering();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    public void filtering(){
        Query query = FirebaseDatabase
                .getInstance()
                .getReference("Hospitals");


        FirebaseRecyclerOptions<Hospital> options =
                new FirebaseRecyclerOptions.Builder<Hospital>()
                        .setQuery(query, Hospital.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Hospital, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(RequestViewHolder viewHolder, int position, Hospital model) {
                final boolean t= model.getDepartments().get(filter);
                final Fragment displayED = DisplayED.newInstance(model);
                final FragmentTransaction tr = getFragmentManager().beginTransaction();

                if(t) {
                    viewHolder.setDetails(model.getName(), model.getAddress());
                }
                else{
                    viewHolder.noDetails();
                }


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(isLongClick){
                            recyclerView.setVisibility(View.GONE);
                            tr.replace(R.id.fragment_filter_choice, displayED);
                            tr.addToBackStack(null);
                            tr.commit();
                        }
                    }
                });
            }

            @Override
            public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_list_view, parent, false);
                return new RequestViewHolder(view);
            }


                    };
        recyclerView.setAdapter(adapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        public View mView;
        private ItemClickListener itemClickListener;
        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setDetails(String Nameh, String Addressh ) {
            TextView name = (TextView) mView.findViewById(R.id.ED_name);
            TextView address = (TextView) mView.findViewById(R.id.ED_address);
            name.setText(Nameh);
            address.setText(Addressh);
        }

        public void noDetails() {

            itemView.setVisibility(View.GONE);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
            return true;
        }
    }

}
