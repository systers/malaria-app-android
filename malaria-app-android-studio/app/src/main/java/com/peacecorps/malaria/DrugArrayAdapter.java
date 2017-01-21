package com.peacecorps.malaria;

/**
 * Created by Ankita on 8/13/2015.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrugArrayAdapter extends ArrayAdapter<String>{
    /**Customized List Adapter with an Image View on Left and Text View on Right**/
    private final Activity context;
    private final String[] dname;
    private final Integer[] imageId;
    public DrugArrayAdapter(Activity context,
                      String[] dname, Integer[] imageId) {
        super(context, R.layout.trip_drug_item, dname);
        this.context = context;
        this.dname = dname;
        this.imageId = imageId;

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
}
