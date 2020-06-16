package com.example.bitcashier;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class AppInfo extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDOts;

    private sliderAdapter sliderAdapter;

    private Button next;
    private Button previous;

    private int nCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfo);
        mDotLayout=(LinearLayout) findViewById(R.id.dotlayout);
        mSlideViewPager= (ViewPager) findViewById(R.id.slideViewPager);

        next=(Button) findViewById(R.id.next);
        previous=(Button) findViewById(R.id.previous);

        sliderAdapter=new sliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        next.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(nCurrentPage==5){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else mSlideViewPager.setCurrentItem(nCurrentPage+1);
            }
        });
        previous.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(nCurrentPage-1);
            }
        });
    }

    public void addDotsIndicator(int position){
        mDOts=new TextView[6];
        mDotLayout.removeAllViews();
        for(int i=0;i<mDOts.length;i++){
            mDOts[i]=new TextView(this);
            mDOts[i].setText(Html.fromHtml("&#8226;"));
            mDOts[i].setTextSize(35);
            mDOts[i].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            mDotLayout.addView(mDOts[i]);
        }
        if(mDOts.length>0){
            mDOts[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }
    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            nCurrentPage=position;

            if(position==0){
                next.setEnabled(true);
                previous.setEnabled(false);
                previous.setVisibility(View.INVISIBLE);

                next.setText("Next");
                previous.setText("");
            }else if(position==mDOts.length-1){

                next.setEnabled(true);
                previous.setEnabled(true);
                previous.setVisibility(View.VISIBLE);
                next.setText("Finish");
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                previous.setText("Back");
            }else{

                next.setEnabled(true);
                previous.setEnabled(true);
                previous.setVisibility(View.VISIBLE);

                next.setText("Next");
                previous.setText("Back");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}