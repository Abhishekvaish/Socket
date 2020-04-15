package com.example.socket;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RowApps extends RecyclerView.Adapter<RowApps.myViewHolder> {
    public List<ResolveInfo> appslist;
    public Context context;
    public Itemselected Incontext;


    interface Itemselected{
        void onIntemSelected(int i);
    }


    public RowApps(List<ResolveInfo> appslist, Context context) {
        this.appslist = appslist;
        this.context = context;
        this.Incontext = (Itemselected) context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.gridrowapp,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        ResolveInfo app = appslist.get(position);
        holder.tvName.setText(app.loadLabel(context.getPackageManager()).toString());
        holder.appicon.setImageDrawable(app.loadIcon(context.getPackageManager()));
        holder.itemView.setId(position);
    }

    @Override
    public int getItemCount() {
        return appslist.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        ImageView appicon;

        public myViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            appicon = itemView.findViewById(R.id.appicon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Incontext.onIntemSelected(itemView.getId());
                }
            });
        }
    }
}
