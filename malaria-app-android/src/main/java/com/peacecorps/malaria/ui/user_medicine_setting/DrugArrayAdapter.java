package com.peacecorps.malaria.ui.user_medicine_setting;

/**
 * Created by Ankita on 8/13/2015.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.peacecorps.malaria.R;

public class DrugArrayAdapter extends ArrayAdapter<String>{
    /**Customized List Adapter with an Image View on Left and Text View on Right**/
    private final Activity context;
    private final String[] dname;
    private final Integer[] imageId;
    private final String[] drugDescriptions;
    DrugArrayAdapter(Activity context,
                     String[] dname, Integer[] imageId, String[] drugDescriptions) {
        super(context, R.layout.trip_drug_item, dname);
        this.context = context;
        this.dname = dname;
        this.imageId = imageId;
        this.drugDescriptions = drugDescriptions;

    }
    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        //inflating the customized view
        View rowView= inflater.inflate(R.layout.trip_drug_item, null, true);
        TextView txtTitle = rowView.findViewById(R.id.drugItem);

        ImageView imageView = rowView.findViewById(R.id.imgDrug);
        txtTitle.setText(dname[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }

    @Override
    public View getDropDownView(int position, View View, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        //inflating the customized view
        View rowView= inflater.inflate(R.layout.trip_drug_info, null, true);
        final TextView txtTitle = rowView.findViewById(R.id.nameDrug);
        Button infoButton = rowView.findViewById(R.id.infoDrug);

        txtTitle.setText(dname[position]);
        addClickListener(position, infoButton);

        return rowView;
    }
    //When the info button is clicked for the drug, an alert dialog will show its description
    private void addClickListener(final int position, Button infoButton) {
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogAlert  = new AlertDialog.Builder(context);
                dialogAlert.setMessage(drugDescriptions[position]);
                dialogAlert.setTitle(dname[position]);
                dialogAlert.setPositiveButton("OK", null);
                dialogAlert.setCancelable(true);
                dialogAlert.create().show();
            }
        });
    }
}
