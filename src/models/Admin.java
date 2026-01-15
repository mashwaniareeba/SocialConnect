package models;

import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
    private static final long serialVersionUID = 1L;
    private List<String> bannedUserIds;

    public Admin(String id, String username, String password, String fullName, String email, int age) {
        super(id, username, password, fullName, email, age);
        this.bannedUserIds = new ArrayList<>();
    }
    
    public void banUser(String userId) {
        if (!bannedUserIds.contains(userId)) {
            bannedUserIds.add(userId);
        }
    }
    
    public void unbanUser(String userId) {
        bannedUserIds.remove(userId);
    }
    
    public boolean isUserBanned(String userId) {
        return bannedUserIds.contains(userId);
    }
    
    public List<String> getBannedUserIds() {
        return new ArrayList<>(bannedUserIds);
    }

    @Override
    public String getUserType() {
        return "Admin";
    }

    @Override
    public boolean canDeleteAnyPost() {
        return true; 
    }

    @Override
    public String toString() {
        return super.toString() + " [ADMIN]";
    }
}
    