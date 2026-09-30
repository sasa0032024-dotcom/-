package com.bonada.lightnovel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Book implements Serializable {
    public String id, title, fileName;
    public List<Chapter> chapters = new ArrayList<>();
    public Book(String id, String title, String fileName) { this.id=id; this.title=title; this.fileName=fileName; }
    public static class Chapter implements Serializable {
        public String title; public int start, end;
        public Chapter(String title, int start, int end) { this.title=title; this.start=start; this.end=end; }
    }
}
