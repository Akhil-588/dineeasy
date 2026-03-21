package com.dineeasy.model;

/** Represents a registered application user. Role is either "USER" or "ADMIN". */
public class User {

    private int    id;
    private String username;
    private String password;
    private String role;

    public User() {}

    public User(int id, String username, String password, String role) {
        this.id       = id;
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    /** Constructor for new users whose id will be assigned by the database. */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    public int    getId()                        { return id;              }
    public void   setId(int id)                  { this.id = id;           }

    public String getUsername()                  { return username;        }
    public void   setUsername(String username)   { this.username = username; }

    public String getPassword()                  { return password;        }
    public void   setPassword(String password)   { this.password = password; }

    public String getRole()                      { return role;            }
    public void   setRole(String role)           { this.role = role;       }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}
