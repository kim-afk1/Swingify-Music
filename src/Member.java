public class Member {
    private int memberId;
    private final String name;
    private String email;
    private String password;
    private String role;
    private String badge;

    public Member(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public boolean login (String name, String password) {
        return this.name.equals(name) && this.password.equals(password);
    }


    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return name;
    }
}

