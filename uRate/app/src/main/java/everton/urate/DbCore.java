package everton.urate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Everton on 11/8/14.
 */
public class DbCore extends SQLiteOpenHelper {
    private static final String DB_NAME = "uRateDB";
    private static final int DB_VERSION = 10;


    public DbCore(Context ctx){
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE items(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, " +
                   "category TEXT NOT NULL, address TEXT, rate REAL NOT NULL, notes TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE items;");
        onCreate(db);
    }
}
