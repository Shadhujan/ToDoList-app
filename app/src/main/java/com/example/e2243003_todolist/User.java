package com.example.e2243003_todolist;


public class User {
    private static User currentUser = null;

    private String username;
    private String email;
    private String password;

    // Constructor
    private User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    public static synchronized void setCurrentUser(User user) {
        currentUser = user;
    }

    public static synchronized User createNewUser(String username, String email, String password) {
        User user = new User(username, email, password);
        setCurrentUser(user);
        return user;
    }

    // Getter and setter methods
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
