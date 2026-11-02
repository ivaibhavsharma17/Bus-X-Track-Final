package busxtrack.ui;

public class ComboItem {
    private int id;
    private String text;

    public ComboItem(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return text;
    }
}
