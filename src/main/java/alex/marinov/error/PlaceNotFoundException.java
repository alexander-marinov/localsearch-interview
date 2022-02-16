package alex.marinov.error;

public class PlaceNotFoundException extends RuntimeException {

    private String id;

    public PlaceNotFoundException(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
