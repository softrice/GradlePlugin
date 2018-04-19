package com.softrice.gradle

public class FileContent {

    String MD5
    File file

    FileContent(String MD5, File file) {
        this.MD5 = MD5
        this.file = file
    }

    @Override
    boolean equals(Object o) {
        if(o instanceof FileContent) {
            return o.getMD5().equals(getMD5())
        }
        return false
    }
}