package everton.uratedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

class PlaceAdapter extends ArrayAdapter<String> {

    public PlaceAdapter(Context context, String[] places) {
        super(context, R.layout.item_row_layout, places);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.item_row_layout,
                parent, false);

        String[] tokens = getItem(position).split(",");
        String place = tokens[0];
        float rating = Float.parseFloat(tokens[1]);

        TextView theTextView = (TextView) theView.findViewById(R.id.tv_place);

        theTextView.setText(place);

        ImageView theImageView = (ImageView) theView.findViewById(R.id.iv_place);

        theImageView.setImageResource(R.drawable.ic_urate);

        RatingBar theRatingBar = (RatingBar) theView.findViewById(R.id.rb_place);

        theRatingBar.setRating(rating);

        return theView;

    }
}
