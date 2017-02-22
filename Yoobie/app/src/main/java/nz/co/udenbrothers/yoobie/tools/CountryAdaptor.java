package nz.co.udenbrothers.yoobie.tools;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import nz.co.udenbrothers.yoobie.models.Country;

public class CountryAdaptor extends ArrayAdapter<Country> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private Country[] values;

    public CountryAdaptor(Context context, int textViewResourceId, Country[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount(){
        return values.length;
    }

    public Country getItem(int position){
        return values[position];
    }

    public long getItemId(int position){
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        TextView label = (TextView) v;
        label.setTextColor(Color.parseColor("#FFFF00"));
        label.setText(values[position].name);
        return v;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView label = (TextView) view;
        label.setTextColor(Color.BLACK);
        label.setText(values[position].name);
        return view;
    }
}
