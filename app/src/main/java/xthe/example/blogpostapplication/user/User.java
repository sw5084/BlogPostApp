package xthe.example.blogpostapplication.user;

public class User {

    private static final String TAG = "User";

    private Integer id;
    private Integer role;
    private String name;
    private String email;
    private String created_date;
    private String updated_date;

    public User(Integer id, Integer role, String name, String email, String created_date, String updated_date) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.email = email;
        this.created_date = created_date;
        this.updated_date = updated_date;
    }


    public Integer getId() {
        return id;
    }

    public String getName () {
        return name;
    }
}
