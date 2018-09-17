package se.opiflex.opiflexindustrialmonitoring;

import com.google.firebase.firestore.DocumentSnapshot;

public class Note
{
    Note(DocumentSnapshot doc)
    {
        message = doc.getString("Note");
        noteId = doc.getString("id");
    }

    private String message;
    private String noteId;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getNoteId()
    {
        return noteId;
    }

    public void setNoteId(String noteId)
    {
        this.noteId = noteId;
    }
}
