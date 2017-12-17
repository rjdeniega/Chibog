package edu.dlsu.mobidev.chibog;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Ira on 10/21/2017.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {

    ArrayList<Place> data;

    public PlaceAdapter(ArrayList<Place> places) {
        this.data = places;
    }

    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create/Inflate the view for each item on the list
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);
        return new PlaceHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {
        //Place data on each item on the list
        Place currentPlace = data.get(position);
        holder.tvPlaceName.setText(currentPlace.getName());
        holder.tvPlaceVicinity.setText(currentPlace.getLocation());
        holder.itemView.setTag(currentPlace);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Place p = (Place)v.getTag();
                onItemClickListener.onItemClick(p);
            }
        });
    }

    @Override
    public int getItemCount(){return data.size();}

    public class PlaceHolder extends RecyclerView.ViewHolder{

        ImageView ivPlace;
        TextView tvPlaceName, tvPlaceVicinity;

        public PlaceHolder(View itemView){
            super(itemView);
            tvPlaceName = (TextView) itemView.findViewById(R.id.place_name);
            tvPlaceVicinity = (TextView) itemView.findViewById(R.id.place_vicinity);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Place p);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
