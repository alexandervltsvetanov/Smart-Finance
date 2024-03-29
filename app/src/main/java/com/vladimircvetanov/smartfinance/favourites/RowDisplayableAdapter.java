package com.vladimircvetanov.smartfinance.favourites;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.RowDisplayable;

import java.util.ArrayList;
import java.util.Collection;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RowDisplayableAdapter extends RecyclerView.Adapter<RowDisplayableAdapter.IconViewHolder>{

    private ArrayList<RowDisplayable> categories;
    private Context context;

    private DBAdapter adapter;

    private IconViewHolder holder;

    RowDisplayableAdapter(ArrayList<RowDisplayable> favouriteCategories, Context context) {
        this.context = context;
        categories = favouriteCategories;
        adapter = DBAdapter.getInstance(context);
    }

    @Override
    public RowDisplayableAdapter.IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.icons_list_item, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RowDisplayableAdapter.IconViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final RowDisplayable categoryExpense = categories.get(position);
        holder.image.setImageResource(categoryExpense.getIconId());
        holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.fav_icon_backgroud));
        this.holder = holder;
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.image.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey));
                holder.removeButton.setVisibility(View.VISIBLE);

                if(!categoryExpense.getIsFavourite()){
                    if(!categories.contains(categoryExpense)) {
                        notifyItemRemoved(position);
                    }
                }
                holder.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(categories.size() > 1) {
                            adapter.deleteFavCategory(categoryExpense);
                            categories.remove(holder.getAdapterPosition());
                            notifyItemRemoved(position);

                        } else{
                            Message.message(context,"You can`t be without favourite categories!");
                        }
                    }
                });
            }
        });
    }

    public IconViewHolder getHolder() {
        return this.holder;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateCategories(ArrayList<RowDisplayable> newCategories) {
        categories.clear();
        for(RowDisplayable cat : newCategories) {
            categories.add(cat);
        }
        notifyDataSetChanged();
    }

    public void addCategory(RowDisplayable category) {
        categories.add(category);
    }

    public void removeCategory(RowDisplayable category) {
        categories.remove(category);
    }

    static class IconViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        ImageButton removeButton;
        ImageButton addButton;
        View viewGroup;

        IconViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            removeButton = (ImageButton) itemView.findViewById(R.id.remove_icon_btn);
            addButton = (ImageButton) itemView.findViewById(R.id.add_icon_btn);
            this.viewGroup = itemView.findViewById(R.id.viewGroup);
        }
    }
}
