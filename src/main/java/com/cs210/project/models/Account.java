package com.cs210.project.models;

import com.cs210.project.constants.Enums.AccountStatus;
import com.cs210.project.constants.Enums.RoleType;

public class Account {
    private int id;
    private int personId;
    private String username;
    private String passwordHash;
    private AccountStatus status;
    private RoleType roleType;
    private boolean isActive;
    
    // Associated objects
    private Person person;

    public Account() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public RoleType getRoleType() { return roleType; }
    public void setRoleType(RoleType roleType) { this.roleType = roleType; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
}
