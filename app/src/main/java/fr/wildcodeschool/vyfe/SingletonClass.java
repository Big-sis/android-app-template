package fr.wildcodeschool.vyfe;

public class SingletonClass {


    public static SingletonClass sInstance = null;
    private int mColor;
    private String mName;

    private SingletonClass() {
    }

    public static SingletonClass getInstance() {
        if (sInstance == null) {
            sInstance = new SingletonClass();
        }
        return sInstance;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}


