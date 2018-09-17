package se.opiflex.opiflexindustrialmonitoring;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseConnection {
    private FirebaseFirestore db;
    private String companyId;

    public String getCompanyId() {
        return companyId;
    }

    public DatabaseConnection(String companyid) {
        db = FirebaseFirestore.getInstance();
        companyId = companyid;
    }

    public DatabaseConnection() {
        db = FirebaseFirestore.getInstance();
        companyId = "testCompany";
    }

    public Task<ArrayList<Robot>> getRobotList() {
        if (companyId == null) {
            Log.w("DatabaseConnection", "The database can't be used if not logged in!");
        }
        return db.collection("Companies/" + companyId + "/Robots/").get().continueWith(new Continuation<QuerySnapshot, ArrayList<Robot>>() {
            @Override
            public ArrayList<Robot> then(Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    ArrayList<Robot> listRobot = new ArrayList<Robot>();
                    for (DocumentSnapshot document : task.getResult()) {
                        listRobot.add(new Robot(document));
                    }
                    return listRobot;
                } else {
                    throw task.getException();
                }
            }
        });
    }

    public ListenerRegistration onRobotListChange(EventListener<QuerySnapshot> listener) {
        return db.collection("Companies/" + companyId + "/Robots/").addSnapshotListener(listener);
    }

    public Task<Robot> getRobotById(String id) {
        if (companyId == null) {
            Log.w("DatabaseConnection", "The database can't be used if not logged in!");
        }
        return db.document("Companies/" + companyId + "/Robots/" + id).get().continueWith(new Continuation<DocumentSnapshot, Robot>() {
            @Override
            public Robot then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    return new Robot(task.getResult());
                } else {
                    throw task.getException();
                }
            }
        });
    }

    public ListenerRegistration onRobotByIdChange(String id, EventListener<DocumentSnapshot> listener) {
        return db.document("Companies/" + companyId + "/Robots/" + id).addSnapshotListener(listener);
    }

    public Task<ArrayList<Error>> getLogs() {
        if (companyId == null) {
            Log.w("DatabaseConnection", "The database can't be used if not logged in!");
        }
        return db.collection("Companies/" + companyId + "/Errors/").get().continueWith(new Continuation<QuerySnapshot, ArrayList<Error>>() {
            @Override
            public ArrayList<Error> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    ArrayList<Error> listError = new ArrayList<Error>();
                    for (DocumentSnapshot document : task.getResult()) {
                        listError.add(new Error(document));
                    }
                    return listError;
                } else {
                    throw task.getException();
                }
            }
        });
    }

    public ListenerRegistration onLogsChange(EventListener<QuerySnapshot> listener) {
        if (companyId == null) {
            Log.w("DatabaseConnection", "The database can't be used if not logged in!");
        }
        return db.collection("Companies/" + companyId + "/Errors/").addSnapshotListener(listener);
    }

    public Task<ArrayList<Error>> getLogsForRobot(String id) {
        if (companyId == null) {
            Log.w("DatabaseConnection", "The database can't be used if not logged in!");
        }
        return db.collection("Companies/" + companyId + "/Errors/").whereEqualTo("robotid", id).get().continueWith(new Continuation<QuerySnapshot, ArrayList<Error>>() {
            @Override
            public ArrayList<Error> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    ArrayList<Error> listError = new ArrayList<Error>();
                    for (DocumentSnapshot document : task.getResult()) {
                        if (!document.getBoolean("solved"))
                            listError.add(new Error(document));
                    }
                    return listError;
                } else {
                    throw task.getException();
                }
            }
        });
    }

    public Task<Void> markErrorAsSolved(final String robotId, final String errorId)
    {
        if (companyId == null) {
            Log.w("DatabaseConnection", "The database can't be used if not logged in!");
        }
        return db.collection("Companies/" + companyId + "/Errors/").whereEqualTo("robotid", robotId).get().continueWithTask(new Continuation<QuerySnapshot, Task<Void>>()
        {
            @Override
            public Task<Void> then(@NonNull Task<QuerySnapshot> task) throws Exception
            {
                QuerySnapshot query = task.getResult();
                List<DocumentSnapshot> documents = query.getDocuments();

                if(task.isSuccessful())
                {
                    for (DocumentSnapshot doc : documents)
                    {
                        String tmp = doc.getString("errorid");
                        if (tmp.equals(errorId))
                        {
                            return db.collection("Companies/" + companyId + "/Errors/").document(doc.getId()).update("solved", true);
                        }
                    }
                    return null;
                }
                else
                    throw task.getException();
            }
        });
    }

    public Task<Void> deleteErrorNote(final String id, final String noteId) {
        if (companyId == null) {
            Log.w("DatabaseConnection", "The database can't be used if not logged in!");
        }
        return db.collection("Companies/" + companyId + "/ErrorInfo/" + id + "/Notes/").whereEqualTo("id", noteId).get().continueWithTask(new Continuation<QuerySnapshot, Task<Void>>()
        {
            @Override
            public Task<Void> then(@NonNull Task<QuerySnapshot> task) throws Exception
            {
                if (task.isSuccessful())
                {
                    QuerySnapshot query = task.getResult();
                    for (int i = 0; i < query.size(); i++)
                    {
                        return db.collection("Companies/" + companyId + "/ErrorInfo/" + id + "/Notes/").document(query.getDocuments().get(i).getId()).delete();
                    }
                    return null;
                }
                else
                    throw task.getException();

            }
        });
    }

    public Task<DocumentReference> pushNote(String id, String note) {
        if (companyId == null) {
            Log.w("DatabaseConnection", "The database can't be used if not logged in!");
        }
        //Option 1 update 1 single note for each error type (the error must already exist on the database)
        /*
        return db.collection("Companies/" + companyId + "/ErrorInfo/").document(error.getId()).update("Note", note);
        */
        //Option 2 add one new note to the error type every time
        ///*
        Map<String, Object> Note = new HashMap<>();
        Note.put("Note", note);
        Note.put("id", UUID.randomUUID().toString());
        return db.collection("Companies/" + companyId + "/ErrorInfo/" + id + "/Notes/").add(Note);
        //*/
    }

    public Task<ArrayList<Note>> getNotesForError(String id) {
        //works only with pushNote option 2
        if (companyId == null) {
            Log.w("DatabaseConnection", "The database can't be used if not logged in!");
        }
        return db.collection("Companies/" + companyId + "/ErrorInfo/" + id + "/Notes/").get().continueWith(new Continuation<QuerySnapshot, ArrayList<Note>>() {
            public ArrayList<Note> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    ArrayList<Note> notesList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        notesList.add(new Note(document));
                    }
                    return notesList;
                } else {
                    throw task.getException();
                }
            }
        });
    }
}
