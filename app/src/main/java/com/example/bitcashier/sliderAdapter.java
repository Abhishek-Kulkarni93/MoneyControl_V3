package com.example.bitcashier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

public class sliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public sliderAdapter(Context context){
        this.context=context;
    }
    public int[] slide_images={
            R.drawable.ob_bitcashier,
            R.drawable.ob_home,
            R.drawable.ob_transaction_history,
            R.drawable.ob_recurring,
            R.drawable.ob_currency_ex,
            R.drawable.ob_threshold
    };
    public String[] slide_heading={
            "BitCashier - by BitCoders!",
            "HOME",
            "EXPENSES",
            "RECURRING EXPENSES",
            "CURRENCIES",
            "THRESHOLD"
    };
    public String[] slide_desc={
            "A simple budget tracker to view and manage all your expenses. Be careful not to overspend!",
            "Here, you can easily add in your Income and Expenses. Summary of your income and expenditure are also displayed here!",
            "View all your expenses at a single click. We've included several filters to track your expenses in a more easier way. You can also edit or delete them!",
            "Repeated expenses are common! You can manage them with a single click!",
            "Travel a lot? Don't worry, we have you covered with our different currency options! You can also change your currency any time from the Settings Page!",
            "Make sure to limit your expenditure! Set your threshold here so that you don't over spend!"

    };
    @Override
    public int getCount() {
        return slide_heading.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view==(RelativeLayout) o;
    }
    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImageView=(ImageView) view.findViewById(R.id.slide_images);
        TextView slideHeading=(TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription=(TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_heading[position]);
        slideDescription.setText(slide_desc[position]);

        container.addView(view);

        return view;
    }
    public void destroyItem(ViewGroup container, int position,Object object){
        container.removeView ((RelativeLayout) object);
    }
}
