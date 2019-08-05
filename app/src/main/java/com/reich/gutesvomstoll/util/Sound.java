package com.reich.gutesvomstoll.util;

public class Sound {

    int mID;
    String mName;
    boolean mIsFave;

    public Sound() {}

    public Sound(int ID, String name, boolean isFave) {

        this.mID = ID;
        this.mIsFave = isFave;
        this.mName = name;

    }

    // GETTERS
    public int getID()  {

        return mID;
    }

    public String getName()  {

        return mName;
    }

    public boolean isFave() {

        return mIsFave;
    }

    // SETTERS

    public void setID(int mID) {

        this.mID = mID;
    }

    public void setName(String mName) {

        this.mName = mName;
    }

    public void setIsFave(boolean mIsFave) {

        this.mIsFave = mIsFave;
    }
}
