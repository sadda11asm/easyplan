package ca.javajeff.btsdigital;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;



/**
 * Created by Саддам on 13.03.2018.
 */

public class SmartAdapter extends RecyclerView.Adapter<SmartAdapter.SmartAdapterViewHolder>{
    private ArrayList<String> mCategoryData = new ArrayList<>();
    Context context;


    private final SmartAdapter.SmartAdapterOnClickHandler mClickHandler;


    public interface SmartAdapterOnClickHandler {
        void onClick(String id);
    }


    public SmartAdapter(SmartAdapter.SmartAdapterOnClickHandler ClickHandler) {
        mClickHandler= ClickHandler;
    }



    public class SmartAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView categoryView;


        public SmartAdapterViewHolder(View view) {
            super(view);
            context = view.getContext();
            categoryView = (TextView) view.findViewById(R.id.table);
        }

        @Override
        public void onClick(View view) {
//            Context cntxt = view.getContext();
//            Intent intent = new Intent(cntxt, TableActivity.class);
//            cntxt.startActivity(intent);
        }
    }

    @Override
    public SmartAdapter.SmartAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttach = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttach);
        return new SmartAdapter.SmartAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SmartAdapter.SmartAdapterViewHolder holder, final int position) {
        String category = mCategoryData.get(position);
        holder.categoryView.setText(category);
        holder.categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context cntxt = v.getContext();
//                Intent intent = new Intent(cntxt, TableActivity.class);
//                cntxt.startActivity(intent);
                Log.i("clicked", "yeeees");

            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mCategoryData) return 0;
        return mCategoryData.size();
    }

    public void setInfoData(ArrayList<String> categories) {
        for (int i=0;i<categories.size();i++) {
            mCategoryData.add(categories.get(i));
        }
        notifyDataSetChanged();
        //Log.v("array",mInfoData.toString());
    }
}
