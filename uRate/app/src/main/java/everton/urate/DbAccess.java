package everton.urate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Everton on 11/8/14.
 */
public class DbAccess {
    private SQLiteDatabase db;

    public DbAccess(Context context) {
        DbCore dbCore = new DbCore(context);
        db = dbCore.getWritableDatabase();
    }

    public void insert(Item item){
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("category", item.getCategory());
        values.put("address", item.getAddress());
        values.put("rate", item.getRate());
        values.put("notes", item.getNotes());
        values.put("fileName", item.getFileName());

        db.insert("items", null, values);
    }

    public void update(Item item){
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("category", item.getCategory());
        values.put("address", item.getAddress());
        values.put("rate", item.getRate());
        values.put("notes", item.getNotes());

//        db.update("items", values, "_id = ?", new String[]{Long.toString(item.getId())});
        db.update("items", values, "_id = "+item.getId(), null);
    }

    public void delete(Item item){
        db.delete("items", "_id = "+item.getId(), null);
    }

    public List<Item> retrieveItems(){
        List<Item> listItems = new ArrayList<Item>();

        String[] columns = new String[]{"_id", "name", "category", "address", "rate", "notes", "fileName"};

        Cursor cursor = db.query("items", columns, null, null, null, null, "name ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Item item = new Item();
            item.setId(cursor.getLong(0));
            item.setName(cursor.getString(1));
            item.setCategory(cursor.getString(2));
            item.setAddress(cursor.getString(3));
            item.setRate(cursor.getFloat(4));
            item.setNotes(cursor.getString(5));
            item.setFileName(cursor.getString(6));
            listItems.add(item);

            cursor.moveToNext();
        }
        cursor.close();
        return listItems;
    }

}
