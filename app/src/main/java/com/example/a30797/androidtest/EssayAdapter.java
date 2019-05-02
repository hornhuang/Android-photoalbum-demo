package com.example.a30797.androidtest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class EssayAdapter extends ArrayAdapter<Informations> {
    private int resourceId;
    public EssayAdapter(Context context, int textViewResourceId,
                        List<Informations> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Informations informations = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent, false);
        ImageView informationImage = (ImageView) view.findViewById(R.id.image);
        TextView informationEssay = (TextView) view.findViewById(R.id.essay);
        informationImage.setImageBitmap(informations.getImageBitmap());
        informationEssay.setText(informations.getEssay());
        return view;
    }
}
