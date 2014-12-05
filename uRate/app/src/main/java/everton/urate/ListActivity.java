package everton.urate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ListActivity extends Activity {
    private MyApplication myApp;
    private DbAccess dbAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        myApp = (MyApplication) getApplication();
        dbAccess = new DbAccess(this);
//        createDummy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        buildLists();
        ExpandableListView elv = (ExpandableListView) findViewById(R.id.elv);
        MyExpandableAdapter myExpandableAdapter = new MyExpandableAdapter(ListActivity.this, myApp.listGroup, myApp.listItem);
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

        FloatingActionButton btnNewItem = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_new))
                .withButtonColor(Color.parseColor("#FF0099"))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        btnNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("everton.urate.ITEM_DETAIL");
                intent.putExtra("isEditMode", true);
                startActivity(intent);
            }
        });
    }

    private void buildLists(){
        myApp.listGroup = new ArrayList<String>();
        myApp.listItem = new HashMap<String, List<Item>>();

        List<Item> auxListItem = dbAccess.retrieveItems();
        for (int i = 0; i < auxListItem.size(); i++){
            Item item = auxListItem.get(i);
            if (myApp.listGroup.contains(item.getCategory())){
                myApp.listItem.get(item.getCategory()).add(item);
            }else {
                myApp.listGroup.add(item.getCategory());
                List<Item> newList = new ArrayList<Item>();
                newList.add(item);
                myApp.listItem.put(item.getCategory(), newList);
            }
        }
    }

    private void createDummy(){
        Item item = new Item();
        String fileName;

        item.setNotes("Test");
        item.setRate(2.5f);
        item.setAddress("Fulton Street");

        item.setCategory("FastFood");
        item.setName("Subway");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);
        item.setName("McDonald's");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);
        item.setName("Wendy's");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);
        item.setName("BURGER KING");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);

        item.setCategory("Pizzeria");
        item.setName("Papa John's Pizza");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);
        item.setName("Majestic Pizza");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);
        item.setName("Pronto Pizza");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);

        item.setCategory("Coffee Shop");
        item.setName("Starbucks");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);
        item.setName("Dunkin' Donuts");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);
        item.setName("Pearl Cafe");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);

        item.setCategory("Mexican Food");
        item.setName("Chipotle Mexican Grill");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);
        item.setName("Toloache Taqueria");
        item.setFileName(UUID.randomUUID().toString().replace("-", "_"));
        dbAccess.insert(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
