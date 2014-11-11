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


public class ListActivity extends Activity {
    private List<String> listGroup;
    private HashMap<String, List<Item>> listItem;
    private DbAccess dbAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbAccess = new DbAccess(this);
//        createDummy();

//        BD bd = new BD(this);
//        List<Usuario> list = bd.buscar();
//        setListAdapter(new UsuarioAdapter(this, list));

//        BD bd = new BD(this);
//        bd.atualizar(usuario);


    }

    @Override
    protected void onResume() {
        super.onResume();

        buildLists();
        ExpandableListView elv = (ExpandableListView) findViewById(R.id.elv);
        MyExpandableAdapter myExpandableAdapter = new MyExpandableAdapter(ListActivity.this, listGroup, listItem);
        elv.setAdapter(myExpandableAdapter);

        elv.setGroupIndicator(getResources().getDrawable(R.drawable.icon_group_indicator));

        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent("everton.urate.ITEM_DETAIL");
                intent.putExtra("editMode", false);
                startActivity(intent);

                return false;
            }
        });

        FloatingActionButton btnNewItem = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_new))
                .withButtonColor(Color.parseColor("#F50057"))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        btnNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("everton.urate.ITEM_DETAIL");
                intent.putExtra("editMode", true);
                startActivity(intent);
            }
        });
    }

    private void buildLists(){
        listGroup = new ArrayList<String>();
        listItem = new HashMap<String, List<Item>>();

        List<Item> auxListItem = dbAccess.retrieveItems();
        for (int i = 0; i < auxListItem.size(); i++){
            Item item = auxListItem.get(i);
            if (listGroup.contains(item.getCategory())){
                listItem.get(item.getCategory()).add(item);
            }else {
                listGroup.add(item.getCategory());
                List<Item> newList = new ArrayList<Item>();
                newList.add(item);
                listItem.put(item.getCategory(), newList);
            }
        }
    }

    private void createDummy(){
        Item item = new Item();

        item.setCategory("FastFood");
        item.setName("Subway");
        dbAccess.insert(item);
        item.setName("McDonald's");
        dbAccess.insert(item);
        item.setName("Wendy's");
        dbAccess.insert(item);
        item.setName("BURGER KING");
        dbAccess.insert(item);

        item.setCategory("Pizzeria");
        item.setName("Papa John's Pizza");
        dbAccess.insert(item);
        item.setName("Majestic Pizza");
        dbAccess.insert(item);
        item.setName("Pronto Pizza");
        dbAccess.insert(item);

        item.setCategory("Coffee Shop");
        item.setName("Starbucks");
        dbAccess.insert(item);
        item.setName("Dunkin' Donuts");
        dbAccess.insert(item);
        item.setName("Pearl Cafe");
        dbAccess.insert(item);

        item.setCategory("Mexican Food");
        item.setName("Chipotle Mexican Grill");
        dbAccess.insert(item);
        item.setName("Toloache Taqueria");
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
