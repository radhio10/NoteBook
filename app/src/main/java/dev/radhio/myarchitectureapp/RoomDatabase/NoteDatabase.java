package dev.radhio.myarchitectureapp.RoomDatabase;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import dev.radhio.myarchitectureapp.DAO.NoteDao;
import dev.radhio.myarchitectureapp.Entities.Note;

/*
1. Create DB
2. execute onCreate in DB
3. Populate data in DB
The RoomDatabase is an abstract class that ties all the pieces together and connects the entities to their corresponding DAO.
Just as in an SQLiteOpenHelper, we have to define a version number and a migration strategy. With fallbackToDestructiveMigration we can let Room recreate our database if we increase the version number.
We create our database in form of a static singleton with the databaseBuilder, where we have to pass our database class and a file name.
 */

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    //not to create multiple instance
    private static NoteDatabase instance = null;

    public abstract NoteDao noteDao();

    /* use instance out side of the class
    avoid multithreading use synchronized */
    public static synchronized NoteDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),NoteDatabase.class,"note_database")
                    .fallbackToDestructiveMigration()
                    //attaching the RoomCallBack in Database
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    /* Populating a database
    instead of using an empty db we insert some note in db */
    // to get into onCreate method
    public static RoomDatabase.Callback roomCallBack= new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // execute PopulateDbAsyncTask in onCreate
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void>{
        private NoteDao noteDao;

        //onCreate is called after the db was Created
        private PopulateDbAsyncTask(NoteDatabase noteDatabase){
            noteDao = noteDatabase.noteDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title 1","Description 1",1));
            noteDao.insert(new Note("Title 2","Description 2",2));
            noteDao.insert(new Note("Title 3","Description 3",3));
            return null;
        }
    }

}
