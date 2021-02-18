package com.example.fireoperationmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CustomAdapter extends FirebaseRecyclerAdapter<User, CustomAdapter.CustomViewHolder> {

    private OnPersonItemClickLister mlistener;

    public interface OnPersonItemClickLister{
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnPersonItemClickLister listener){
        this.mlistener = listener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView id, st_name, address, structure, floor, st_type, facility, person, phone1, phone2;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            id = (TextView)itemView.findViewById(R.id.id);
            address = (TextView)itemView.findViewById(R.id.address);
            st_name = (TextView)itemView.findViewById(R.id.st_name);
            structure = (TextView)itemView.findViewById(R.id.structure);
            floor = (TextView)itemView.findViewById(R.id.floor);
            st_type = (TextView)itemView.findViewById(R.id.st_type);
            facility = itemView.findViewById(R.id.facility);
            person = (TextView)itemView.findViewById(R.id.person);
            phone1 = (TextView)itemView.findViewById(R.id.phone1);
            phone2 = (TextView)itemView.findViewById(R.id.phone2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        mlistener.onItemClick(view, position);
                    }
                }
            });
        }
    }

    public CustomAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomViewHolder holder, int position, @NonNull User model) {
        holder.id.setText(model.getTagId());
        holder.address.setText(model.getTagAddress());
        holder.floor.setText(model.getTagFloor());
        holder.st_name.setText(model.getTagSt_name());
        holder.structure.setText(model.getTagStructure());
        holder.st_type.setText(model.getTagSt_type());
        holder.facility.setText(model.getTagFaclility());
        holder.person.setText(model.getTagPerson());
        holder.phone1.setText(model.getTagPhone1());
        holder.phone2.setText(model.getTagPhone2());
    }
}
