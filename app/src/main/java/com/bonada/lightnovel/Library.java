package com.bonada.lightnovel;

import android.content.Context;
import java.io.*;
import java.util.*;

public final class Library {
    private Library() {}
    private static File meta(Context c) { return new File(c.getFilesDir(), "library.dat"); }
    @SuppressWarnings("unchecked")
    public static ArrayList<Book> load(Context c) {
        File f=meta(c); if(!f.exists()) return new ArrayList<>();
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(f))) { return (ArrayList<Book>)in.readObject(); }
        catch(Exception e){ return new ArrayList<>(); }
    }
    public static void save(Context c, ArrayList<Book> books) {
        try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(meta(c)))) { out.writeObject(books); }
        catch(IOException ignored) {}
    }
}
