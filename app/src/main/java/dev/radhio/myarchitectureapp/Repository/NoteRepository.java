package dev.radhio.myarchitectureapp.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import dev.radhio.myarchitectureapp.DAO.NoteDao;
import dev.radhio.myarchitectureapp.Entities.Note;
import dev.radhio.myarchitectureapp.RoomDatabase.NoteDatabase;

import java.util.List;
/*
 The Repository is a simple Java class that abstracts the data layer from the rest of the app and mediates between different data sources, like a web service and a local cache.
 It hides the different database operations (like SQLite queries) and provides a clean API to the ViewModel.
 Since Room doesn't allow database queries on the main thread, we use AsyncTasks to execute them asynchronously.

 Also, we will add a RoomDatabase.Callback to our database builder where we populate our database in the onCreate method so we don't start with an empty table.
 We can also override onOpen if we want to execute code every time our Room database is opened.

 */

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    // use application as an context to create database instance
    public NoteRepository(Application application) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        noteDao = noteDatabase.noteDao();
        allNotes = noteDao.getALlNotes();
    }

    /*we have to execute these 4 method on background thread that's why use asynctask class
    because room doesn't allow bd operation in main thread
     */
    public void insert(Note note){
        new InsertAsynctask(noteDao).execute(note);
    }
    public void update(Note note){
        new UpdateAsynctask(noteDao).execute(note);
    }
    public void delete(Note note){
        new DeleteAsynctask(noteDao).execute(note);
    }
    public void deleteAllNotes(){
        new DeleteAllNotesAsynctask(noteDao).execute();
    }

    // room will automatically execute the db operation and return the the LiveData on Background thread
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    /*
    Four Different AsyncTask for to clear understand but we can make it more complex by making one method
     */

    private static class InsertAsynctask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao; // need noteDao to make db operation

        // as this an static class we can access NoteDao directly here we have to make construction
        private InsertAsynctask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateAsynctask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao; // need noteDao to make db operation

        // as this an static class we can access NoteDao directly here we have to make construction
        private UpdateAsynctask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteAsynctask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao; // need noteDao to make db operation

        // as this an static class we can access NoteDao directly here we have to make construction
        private DeleteAsynctask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsynctask extends AsyncTask<Void, Void, Void>{
        private NoteDao noteDao; // need noteDao to make db operation

        // as this an static class we can access NoteDao directly here we have to make construction
        private DeleteAllNotesAsynctask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNote();
            return null;
        }
    }
}
