package everton.urate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ListFragment extends Fragment {
    private MyApplication myApp;
    private DbAccess dbAccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myApp = (MyApplication) getActivity().getApplication();
        dbAccess = new DbAccess(getActivity());
//       createDummy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        buildLists();
        ExpandableListView elv = (ExpandableListView) getView().findViewById(R.id.elv);
        MyExpandableAdapter myExpandableAdapter = new MyExpandableAdapter(getActivity(), myApp.listGroup, myApp.listItem);
        elv.setAdapter(myExpandableAdapter);

        elv.setGroupIndicator(getResources().getDrawable(R.drawable.icon_group_indicator));

        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent("everton.urate.ITEM_DETAIL");
                intent.putExtra("isEditMode", false);
                intent.putExtra("groupPosition", groupPosition);
                intent.putExtra("childPosition", childPosition);
                startActivity(intent);
                return false;
            }
        });

        FloatingActionButton btnNewItem = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_new))
                .withButtonColor(Color.parseColor("#FF0099"))
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .create();

        btnNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("everton.urate.ITEM_DETAIL");
                intent.putExtra("isEditMode", true);
                intent.putExtra("isNewItem", true);
                startActivity(intent);
            }
        });
    }

    private void buildLists() {
        myApp.listGroup = new ArrayList<String>();
        myApp.listItem = new HashMap<String, List<Item>>();

        List<Item> auxListItem = dbAccess.retrieveItems();
        for (int i = 0; i < auxListItem.size(); i++) {
            Item item = auxListItem.get(i);
            if (myApp.listGroup.contains(item.getCategory())) {
                myApp.listItem.get(item.getCategory()).add(item);
            } else {
                myApp.listGroup.add(item.getCategory());
                List<Item> newList = new ArrayList<Item>();
                newList.add(item);
                myApp.listItem.put(item.getCategory(), newList);
            }
        }
    }

    private void createDummy() {
       // TODO
        // We need to create a dummy node at first otherwise we cannot point any new entries to the DB
        // So whatever works for a simple head, Might be able to remove it after first insertion into
        //

        Item item = new Item();
        String fileName;

        item.setNotes("");
        item.setRate(2.5f);
        item.setAddress("");

        item.setCategory("DUMMY Node");
        item.setName("Welcome to uRate");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.710162");
        item.setLng("-74.005500");
        dbAccess.insert(item);
         item.setName("McDonald's");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.708426");
        item.setLng("-74.004803");
        dbAccess.insert(item);
        item.setName("Wendy's");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.710191");
        item.setLng("-74.007974");
        dbAccess.insert(item);
        item.setName("BURGER KING");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.709605");
        item.setLng("-74.006944");
        dbAccess.insert(item);

        item.setCategory("Pizzeria");
        item.setName("Papa John's Pizza");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.709186");
        item.setLng("-74.008933");
        dbAccess.insert(item);
        item.setName("Majestic Pizza");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.709414");
        item.setLng("-74.009534");
        dbAccess.insert(item);
        item.setName("Pronto Pizza");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.710845");
        item.setLng("-74.012281");
        dbAccess.insert(item);

        item.setCategory("Coffee Shop");
        item.setName("Starbucks");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.711627");
        item.setLng("-74.0066");
        dbAccess.insert(item);
        item.setName("Dunkin' Donuts");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.711091");
        item.setLng("-74.006463");
        dbAccess.insert(item);
        item.setName("Pearl Cafe");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.708157");
        item.setLng("-74.003956");
        dbAccess.insert(item);

        item.setCategory("Mexican Food");
        item.setName("Chipotle Mexican Grill");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.709812");
        item.setLng("-74.006445");
        dbAccess.insert(item);
        item.setName("Toloache Taqueria");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        item.setLat("40.707604");
        item.setLng("-74.007463");
        dbAccess.insert(item);

    }
}