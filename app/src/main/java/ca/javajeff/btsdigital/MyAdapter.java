package ca.javajeff.btsdigital;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by Саддам on 13.03.2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyAdapterViewHolder>{
    private ArrayList<String> mCategoryData = new ArrayList<>();
    private ArrayList<String> mPhoneData = new ArrayList<>();
    Context context;


    private final MyAdapter.MyAdapterOnClickHandler mClickHandler;


    public interface MyAdapterOnClickHandler {
        void onClick(String id);
    }


    public MyAdapter(MyAdapter.MyAdapterOnClickHandler ClickHandler) {
        mClickHandler= ClickHandler;
    }



    public class MyAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView categoryView;
        public final TextView phoneView;


        public MyAdapterViewHolder(View view) {
            super(view);
            context = view.getContext();
            categoryView = (TextView) view.findViewById(R.id.category_item);
            phoneView = view.findViewById(R.id.phone_view);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public MyAdapter.MyAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.my_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttach = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttach);
        return new MyAdapter.MyAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.MyAdapterViewHolder holder, final int position) {
        String category = mCategoryData.get(position);
        String phone = mPhoneData.get(position);
        holder.categoryView.setText(category);
        holder.phoneView.setText(phone);
    }

    @Override
    public int getItemCount() {
        if (null == mCategoryData) return 0;
        return mCategoryData.size();
    }

    public void setInfoData(ArrayList<String> categories, ArrayList<String> numbers) {
        for (int i=0;i<categories.size();i++) {
            mCategoryData.add(categories.get(i));
            mPhoneData.add(numbers.get(i));
        }
        notifyDataSetChanged();
        //Log.v("array",mInfoData.toString());
    }
}
