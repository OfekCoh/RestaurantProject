package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;

@Entity
@Table(name = "workers", uniqueConstraints = @UniqueConstraint(columnNames = "email")) // Ensure unique emails
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true) // Ensures unique email
    private String email;

    @Column(nullable = false)
    private String password; // Consider hashing before storing

    private boolean isLoggedIn;

    private int role; // 0 = Customer, 1 = Admin, etc.

    public Worker() {
    }

    public Worker(String name, String email, String password, boolean isLoggedIn, int role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }


    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + "', email='" + email + "', role=" + role + "}";
    }
}
