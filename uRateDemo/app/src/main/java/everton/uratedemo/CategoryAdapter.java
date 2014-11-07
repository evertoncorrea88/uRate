package everton.uratedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class CategoryAdapter extends ArrayAdapter<String> {

    public CategoryAdapter(Context context, String[] values) {
        super(context, R.layout.cat_row_layout, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.cat_row_layout,
                parent, false);

        String category = getItem(position);

        TextView theTextView = (TextView) theView.findViewById(R.id.tv_content);

        theTextView.setText(category);

        ImageView theImageView = (ImageView) theView.findViewById(R.id.iv_content);

        theImageView.setImageResource(R.drawable.ic_urate);

        return theView;

    }
}
