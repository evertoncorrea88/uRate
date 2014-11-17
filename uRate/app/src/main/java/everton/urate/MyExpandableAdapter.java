package everton.urate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Everton on 11/8/14.
 */
public class MyExpandableAdapter extends BaseExpandableListAdapter {
    private List<String> listGroup;
    private HashMap<String, List<Item>> listItem;
    private LayoutInflater inflater;

    public MyExpandableAdapter(Context context, List<String> listGroup, HashMap<String, List<Item>> listItem){
        this.listGroup = listGroup;
        this.listItem = listItem;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listItem.get(listGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listItem.get(listGroup.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.elv_category_line, null);
            holder = new ViewHolderGroup();
            convertView.setTag(holder);

            holder.tvCategoryTxt = (TextView) convertView.findViewById(R.id.tv_category_txt);
        }
        else{
            holder = (ViewHolderGroup) convertView.getTag();
        }

        holder.tvCategoryTxt.setText(listGroup.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderItem holder;
        Item item = (Item) getChild(groupPosition, childPosition);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.elv_item_line, null);
            holder = new ViewHolderItem();
            convertView.setTag(holder);

            holder.tvItemName = (TextView) convertView.findViewById(R.id.tv_item_name);
            holder.rbRate = (RatingBar) convertView.findViewById(R.id.rb_rate);
        }
        else{
            holder = (ViewHolderItem) convertView.getTag();
        }

        holder.tvItemName.setText(item.getName());
        holder.rbRate.setRating(item.getRate());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolderGroup {
        TextView tvCategoryTxt;
    }

    class ViewHolderItem {
        TextView tvItemName;
        RatingBar rbRate;
    }
}
