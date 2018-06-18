package com.peacecorps.malaria.adapter;

/**
 * Created by Ankita on 8/13/2015.
 */
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.peacecorps.malaria.R;

public class DrugArrayAdapter extends ArrayAdapter<String>{
    /**Customized List Adapter with an Image View on Left and Text View on Right**/
    private final Activity context;
    private final String[] dname;
    private final Integer[] imageId;
    private final String[] drugDescriptions;
    public DrugArrayAdapter(Activity context,
                      String[] dname, Integer[] imageId, String[] drugDescriptions) {
        super(context, R.layout.trip_drug_item, dname);
        this.context = context;
        this.dname = dname;
        this.imageId = imageId;
        this.drugDescriptions = drugDescriptions;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        //inflating the customized view
        View rowView= inflater.inflate(R.layout.trip_drug_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.drugItem);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgDrug);
        txtTitle.setText(dname[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }

    @Override
    public View getDropDownView(int position, View View, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        //inflating the customized view
        View rowView= inflater.inflate(R.layout.trip_drug_info, null, true);
        final TextView txtTitle = (TextView) rowView.findViewById(R.id.nameDrug);
        Button infoButton = (Button) rowView.findViewById(R.id.infoDrug);

        txtTitle.setText(dname[position]);
        addClickListener(position, infoButton);

        return rowView;
    }
    //When the info button is clicked for the drug, an alert dialog will show its description
    public void addClickListener(final int position, Button infoButton) {
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpannableString spannableString = new SpannableString(drugDescriptions[position]);
                Linkify.addLinks(spannableString, Linkify.ALL);
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setPositiveButton("OK", null)
                        .setMessage(spannableString)
                        .setTitle(dname[position])
                        .create();
                alertDialog.show();
                // Make the textview clickable. Must be called after show()
                ((TextView)alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        });
    }
}
