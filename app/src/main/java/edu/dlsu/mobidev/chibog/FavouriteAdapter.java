package edu.dlsu.mobidev.chibog;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ira on 26/11/2017.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public FavouriteAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    class FavouriteViewHolder extends RecyclerView.ViewHolder{
        TextView nameText,placeCount;

        FavouriteViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.favourite_name);
            placeCount = (TextView) itemView.findViewById(R.id.places_count);
        }
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.favourite_item, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex("name"));

        long id = mCursor.getLong(mCursor.getColumnIndex("id"));
        DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext());
        int count = dbHelper.getRestaurantsFromFavourite(id).size();

        holder.nameText.setText(name);
        holder.placeCount.setText(String.valueOf(count));
        holder.itemView.setTag(id);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long n = (long) view.getTag();
                onItemClickListener.onItemClick(n);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;

        if (newCursor != null){
            notifyDataSetChanged();
        }
    }


    public interface OnItemClickListener{
        void onItemClick(long n);
    }

    private OnItemClickListener onItemClickListener;

    void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
