package android.example.mytrackerappvertwo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PersonAdapter extends ArrayAdapter {

    private int colorResourceId;


    public PersonAdapter(@NonNull Context context, ArrayList pNames, int color) {
        super(context, 0, pNames);
        colorResourceId = color;

    }


    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_items, parent, false);
        }
        Person curPeople = (Person) getItem(position);

        TextView txtView = listItemView.findViewById(R.id.tv_personName);
        txtView.setText(curPeople.getName());

        ImageView imgView = listItemView.findViewById(R.id.imgview_sample);

        imgView.setImageResource(curPeople.getmImageResourceId());

        int color = ContextCompat.getColor(getContext(), colorResourceId);

        listItemView.setBackgroundColor(color);

        return listItemView;


    }


}
