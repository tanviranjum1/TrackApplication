package android.example.mytrackerappvertwo;

public class Person {

    private String name;
    private int imageResourceId;


    public Person(String name, int imageResourceId) {
        this.name = name;
        this.imageResourceId = imageResourceId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getmImageResourceId() {
        return imageResourceId;
    }

    public void setmImageResourceId(int mImageResourceId) {
        this.imageResourceId = mImageResourceId;
    }
}
