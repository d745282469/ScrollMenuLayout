package com.dong.scrollmenulayout.demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dong.scrollmenulayout.ScrollMenuLayout;

import java.util.List;

/**
 * @author pd
 * time     2019/4/16 17:03
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
   private Context context;
   private List<String> itemList;

    public TestAdapter(Context context, List<String> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ScrollMenuLayout scrollMenuLayout = new ScrollMenuLayout(context,null);
        scrollMenuLayout.setItemView(LayoutInflater.from(context).inflate(R.layout.item,null));
        scrollMenuLayout.setRightMenuView(LayoutInflater.from(context).inflate(R.layout.item_menu,null));
        return new ViewHolder(scrollMenuLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, final int i) {
        vh.tv_content.setText(itemList.get(i));
        vh.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.remove(i);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_content;
        Button btn_delete,btn_close;
        public ViewHolder(@NonNull final View view) {
            super(view);
            tv_content = view.findViewById(R.id.item_tv_content);
            btn_delete = view.findViewById(R.id.item_menu_btn_delete);
            btn_close = view.findViewById(R.id.item_menu_btn_close);
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Click close btn.",Toast.LENGTH_SHORT).show();
                    ((ScrollMenuLayout) view).closeRightMenu();
                }
            });
        }
    }
}
