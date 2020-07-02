package com.example.bitcashier.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bitcashier.R;

public class HelpAdapter extends ArrayAdapter<String> {

    String [] questionsAns;
    Context helpContext;

    public HelpAdapter(@NonNull Context context, String [] questions) {
        super(context, R.layout.listview_help);
        this.questionsAns = questions;
        this.helpContext = context;

    }

    @Override
    public int getCount() {
        return questionsAns.length;
    }

//    @NonNullnew Viewh
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder helpHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater helpInflater = (LayoutInflater) helpContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = helpInflater.inflate(R.layout.listview_help, parent, false);
            helpHolder.questions = (TextView) convertView.findViewById(R.id.help_textquestion);
            convertView.setTag(helpHolder);
        }else{
            helpHolder = (ViewHolder)convertView.getTag();
        }

        helpHolder.questions.setText(questionsAns[position]);

        return convertView;
    }

    static class ViewHolder{
        TextView  questions;
    }
}
