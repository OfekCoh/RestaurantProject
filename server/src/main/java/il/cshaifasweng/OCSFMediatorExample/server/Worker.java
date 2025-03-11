package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;
import java.util.List;

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

    /*
       0 - regular worker
       1 - costumer support
       2 - branch manager
       3 - dietitian
       4 - ceo
    */
    private int role;

    @ElementCollection
    @CollectionTable(name = "worker_branches", joinColumns = @JoinColumn(name = "worker_id"))
    @Column(name = "branch_id")
    private List<Integer> workingBranch; // Stores branch IDs

    public Worker() {
    }

    public Worker(String name, String email, String password, boolean isLoggedIn, int role, List<Integer> workingBranch) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
        this.role = role;
        this.workingBranch = workingBranch;
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

    public List<Integer> getWorkingBranch() {
        return workingBranch;
    }

    public void setWorkingBranch(List<Integer> workingBranch) {
        this.workingBranch = workingBranch;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", workingBranch=" + workingBranch +
                '}';
    }
}
