# 🌐 SocialConnect - Complete Technical Documentation

A full-featured social media platform built in Java demonstrating advanced OOP concepts, data security, GUI development, and system architecture.

---

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [File Structure & Responsibilities](#file-structure--responsibilities)
4. [Core Functionalities](#core-functionalities)
5. [Security Implementation](#security-implementation)
6. [Data Flow & Logic](#data-flow--logic)
7. [Design Patterns](#design-patterns)
8. [Setup & Running](#setup--running)
9. [Common Questions & Answers](#common-questions--answers)

---

## 🎯 Project Overview

### What is SocialConnect?
SocialConnect is a desktop social networking application built using Java Swing that simulates real social media platforms like Facebook or Instagram. Users can create accounts, post content (text and images), interact with posts through likes and comments, follow other users, and manage their profiles.

### Key Technologies
- **Language**: Java (JDK 24.0.2)
- **GUI Framework**: Java Swing
- **Data Persistence**: Java Serialization with AES Encryption
- **Security**: SHA-256 Password Hashing, AES File Encryption
- **Architecture Pattern**: MVC (Model-View-Controller) with Singleton

### Educational Value
This project demonstrates:
- Object-Oriented Programming (OOP) principles
- GUI development with Swing
- Data persistence and serialization
- Security best practices (encryption, hashing)
- Design patterns (Singleton, Observer-like callbacks)
- Event-driven programming
- File I/O operations

---

## 🏗 System Architecture

### Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                         GUI Layer                            │
│  (MainApplication, Panels: Login, Feed, Profile, etc.)      │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      Controller Layer                        │
│              (SocialNetworkSystem - Singleton)               │
│  - Manages all business logic                                │
│  - Handles user authentication                               │
│  - Manages posts, comments, likes                            │
│  - Controls follow/unfollow operations                       │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                        Model Layer                           │
│  (User, Post, Comment, RegularUser, Admin, etc.)            │
│  - Data structures and entities                             │
│  - Business rules and validation                            │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Persistence Layer                         │
│              (DataPersistence - Static Class)                │
│  - Saves data to encrypted files                            │
│  - Loads data from encrypted files                          │
│  - Handles data migration                                   │
└─────────────────────────────────────────────────────────────┘
```

### Design Philosophy
1. **Separation of Concerns**: Each layer has distinct responsibilities
2. **Encapsulation**: Data is private with controlled access
3. **Single Responsibility**: Each class has one main purpose
4. **Dependency Flow**: GUI → Controller → Model → Persistence

---

## 📁 File Structure & Responsibilities

### 📂 `src/` Directory

#### 🎯 `Main.java`
**Purpose**: Application entry point

**What it does**:
- Creates the main JFrame window
- Initializes the SocialNetworkSystem singleton
- Shows the MainApplication GUI
- Sets up window properties (size, close operation, etc.)

**Key Code**:
```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        SocialNetworkSystem.getInstance(); // Initialize system
        MainApplication app = new MainApplication();
        app.setVisible(true);
    });
}
```

**Why it exists**: Every Java application needs a main method as the starting point.

---

### 📂 `src/models/` Directory
Contains all data model classes that represent entities in the system.

#### 👤 `User.java` (Abstract Class)
**Purpose**: Base class for all user types

**What it contains**:
- User ID (unique identifier)
- Username (login name)
- Password (SHA-256 hashed)
- Full name
- Email
- Bio
- Account status (active, banned, etc.)

**Key Methods**:
- `validatePassword(String password)`: Checks if entered password matches (compares hashes)
- `hashPassword(String password)`: Converts plain text password to SHA-256 hash
- `setPassword(String password)`: Automatically hashes password before storing
- Abstract methods: `getUserType()`, `canCreatePost()`, `canManageUsers()`

**Security Logic**:
```java
private String hashPassword(String password) {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] hashBytes = md.digest(password.getBytes());
    // Convert bytes to hexadecimal string
    StringBuilder sb = new StringBuilder();
    for (byte b : hashBytes) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString(); // Returns 64-character hex string
}
```

**Why abstract**: Different user types (RegularUser, Admin) need different behaviors while sharing common data.

#### 👥 `RegularUser.java`
**Purpose**: Represents normal users of the platform

**Additional Features**:
- Following list (users they follow)
- Followers list (users following them)
- Post history
- Verification status (verified badge)
- Verification request status (pending, approved, rejected)

**Key Methods**:
- `followUser(int userId)`: Add user to following list
- `unfollowUser(int userId)`: Remove user from following list
- `addFollower(int userId)`: Someone followed this user
- `removeFollower(int userId)`: Someone unfollowed this user
- `isFollowing(int userId)`: Check if following a specific user
- `requestVerification()`: Request verified badge
- `getUserType()`: Returns "REGULAR"
- `canCreatePost()`: Returns true (regular users can post)
- `canManageUsers()`: Returns false (regular users can't manage others)

**Why it exists**: Most users are regular users with social features.

#### 🛡️ `Admin.java`
**Purpose**: Represents administrators with special privileges

**Special Abilities**:
- Can ban/unban users
- Can verify users (give verified badges)
- Can view all posts (not just from followed users)
- Can manage verification requests

**Key Methods**:
- `getUserType()`: Returns "ADMIN"
- `canCreatePost()`: Returns true (admins can post)
- `canManageUsers()`: Returns true (admins can manage users)

**Why it exists**: System needs privileged users for moderation and management.

#### 📝 `Post.java` (Abstract Class)
**Purpose**: Base class for all post types

**What it contains**:
- Post ID (unique identifier)
- Author ID (who created it)
- Timestamp (when created)
- Likes (list of user IDs who liked)
- Comments (list of Comment objects)
- Share count

**Key Methods**:
- `addLike(int userId)`: User likes the post
- `removeLike(int userId)`: User unlikes the post
- `hasUserLiked(int userId)`: Check if user has liked
- `addComment(Comment comment)`: Add a comment
- `incrementShareCount()`: Increase share counter
- Abstract methods: `getPostType()`, `getContent()`, `getDisplayContent()`

**Why abstract**: Different post types (text, image) have different content.

#### 📄 `TextPost.java`
**Purpose**: Represents text-only posts

**Additional Data**:
- Content (the actual text)

**Implementation**:
```java
@Override
public String getPostType() {
    return "TEXT";
}

@Override
public String getContent() {
    return content; // Just the text
}

@Override
public String getDisplayContent() {
    return content; // Display as-is
}
```

#### 🖼️ `ImagePost.java`
**Purpose**: Represents posts with images

**Additional Data**:
- Image path (location of image file)
- Caption (text accompanying the image)

**Implementation**:
```java
@Override
public String getPostType() {
    return "IMAGE";
}

@Override
public String getContent() {
    return caption; // Return caption as content
}

@Override
public String getDisplayContent() {
    return "Image: " + imagePath + "\n" + caption;
}
```

**Image Handling**: The GUI reads the image file and displays it, while the model just stores the path.

#### 💬 `Comment.java`
**Purpose**: Represents comments on posts

**What it contains**:
- Comment ID
- User ID (commenter)
- Post ID (which post)
- Content (comment text)
- Timestamp

**Why separate class**: Comments are independent entities that can be queried, displayed, and managed separately.

---

### 📂 `src/system/` Directory
Contains core system logic and data management.

#### 🎛️ `SocialNetworkSystem.java` (Singleton)
**Purpose**: Central controller for all application logic

**Why Singleton**: We need exactly ONE instance managing the entire system state.

**Core Responsibilities**:
1. **User Management**: Login, registration, authentication
2. **Post Management**: Creating, deleting, retrieving posts
3. **Social Features**: Follow/unfollow, likes, comments
4. **Admin Features**: Ban/unban, verification management
5. **Data Persistence**: Auto-save after changes
6. **Session Management**: Track current logged-in user

**Key Data Structures**:
```java
private List<User> users;              // All registered users
private List<Post> posts;              // All posts in system
private User currentUser;              // Currently logged-in user
private int nextUserId;                // Auto-increment ID counter
private int nextPostId;                // Auto-increment ID counter
private int nextCommentId;             // Auto-increment ID counter
```

**Important Methods & Logic**:

**1. User Registration**:
```java
public boolean registerUser(String username, String password, String fullName, String email) {
    // Check if username already exists
    for (User u : users) {
        if (u.getUsername().equalsIgnoreCase(username)) {
            return false; // Username taken
        }
    }
    
    // Create new RegularUser
    RegularUser newUser = new RegularUser(
        nextUserId++,      // Assign unique ID
        username,
        password,          // Will be hashed in User constructor
        fullName,
        email
    );
    
    users.add(newUser);
    saveData();           // Persist to disk
    return true;
}
```

**2. User Login**:
```java
public User login(String username, String password) {
    // Find user by username
    User user = getUserByUsername(username);
    
    if (user == null) {
        return null; // User not found
    }
    
    // Check if user is banned
    if (user instanceof RegularUser) {
        RegularUser regUser = (RegularUser) user;
        if (regUser.isBanned()) {
            return null; // Banned users cannot login
        }
    }
    
    // Validate password (compares hashes)
    if (user.validatePassword(password)) {
        currentUser = user;
        return user;
    }
    
    return null; // Wrong password
}
```

**3. Creating Posts**:
```java
public Post createTextPost(String content) {
    if (currentUser == null || !currentUser.canCreatePost()) {
        return null; // Not logged in or no permission
    }
    
    TextPost post = new TextPost(
        nextPostId++,           // Unique ID
        currentUser.getId(),    // Author
        content
    );
    
    posts.add(post);
    
    // Add to user's post history
    if (currentUser instanceof RegularUser) {
        ((RegularUser) currentUser).addPost(post);
    }
    
    saveData();
    return post;
}
```

**4. Follow/Unfollow Logic**:
```java
public boolean followUser(int userIdToFollow) {
    if (!(currentUser instanceof RegularUser)) {
        return false; // Only regular users can follow
    }
    
    RegularUser currentRegular = (RegularUser) currentUser;
    User targetUser = getUserById(userIdToFollow);
    
    if (targetUser == null || !(targetUser instanceof RegularUser)) {
        return false; // Target doesn't exist or is admin
    }
    
    RegularUser targetRegular = (RegularUser) targetUser;
    
    if (currentRegular.isFollowing(userIdToFollow)) {
        // Already following - unfollow
        currentRegular.unfollowUser(userIdToFollow);
        targetRegular.removeFollower(currentUser.getId());
    } else {
        // Not following - follow
        currentRegular.followUser(userIdToFollow);
        targetRegular.addFollower(currentUser.getId());
    }
    
    saveData();
    return true;
}
```

**5. Like/Unlike Post**:
```java
public boolean toggleLike(int postId) {
    if (currentUser == null) return false;
    
    Post post = getPostById(postId);
    if (post == null) return false;
    
    if (post.hasUserLiked(currentUser.getId())) {
        post.removeLike(currentUser.getId()); // Unlike
    } else {
        post.addLike(currentUser.getId());    // Like
    }
    
    saveData();
    return true;
}
```

**6. Feed Generation Logic**:
```java
public List<Post> getFeedPosts() {
    if (currentUser == null) {
        return new ArrayList<>();
    }
    
    // Admins see all posts
    if (currentUser instanceof Admin) {
        return new ArrayList<>(posts); // All posts
    }
    
    // Regular users see posts from people they follow + their own
    RegularUser regUser = (RegularUser) currentUser;
    List<Post> feedPosts = new ArrayList<>();
    
    for (Post post : posts) {
        int authorId = post.getAuthorId();
        
        // Show if:
        // 1. User's own post
        // 2. Post from someone they follow
        if (authorId == currentUser.getId() || 
            regUser.isFollowing(authorId)) {
            feedPosts.add(post);
        }
    }
    
    // Sort by timestamp (newest first)
    feedPosts.sort((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()));
    
    return feedPosts;
}
```

**7. Password Migration** (Backward Compatibility):
```java
private void migratePlaintextPasswords() {
    boolean changed = false;
    
    for (User user : users) {
        // SHA-256 hash is always 64 characters (hex)
        // If password is not 64 chars, it's not hashed
        if (user.getPassword() != null && 
            user.getPassword().length() != 64) {
            
            System.out.println("Migrating password for: " + user.getUsername());
            user.setPassword(user.getPassword()); // Re-hash
            changed = true;
        }
    }
    
    if (changed) {
        saveData(); // Save migrated data
    }
}
```

**Why this matters**: Ensures old data files with plain-text passwords get automatically hashed without breaking existing accounts.

#### 💾 `DataPersistence.java` (Static Utility)
**Purpose**: Handles all file I/O operations with encryption

**Core Responsibilities**:
1. Save objects to encrypted files
2. Load objects from encrypted files
3. Handle backward compatibility (loading unencrypted old files)
4. Automatic migration to encrypted format

**File Structure**:
```
data/
├── users.dat                 # Encrypted user list
├── posts.dat                 # Encrypted post list
├── counters.dat              # Encrypted ID counters
└── verification_requests.dat # Encrypted verification requests
```

**Encryption Specification**:
- **Algorithm**: AES (Advanced Encryption Standard)
- **Mode**: ECB (Electronic Codebook)
- **Padding**: PKCS5Padding
- **Key**: 128-bit (16 bytes)
- **Key String**: "MySocialNetwork!"

**Save Logic with Encryption**:
```java
private static void saveToFile(String filename, Object data) {
    try {
        // Create AES cipher for encryption
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        
        // Create output streams
        FileOutputStream fileOut = new FileOutputStream(filename);
        CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
        ObjectOutputStream out = new ObjectOutputStream(cipherOut);
        
        // Write encrypted data
        out.writeObject(data);
        
        // Close streams
        out.close();
        cipherOut.close();
        fileOut.close();
        
        System.out.println("Saved encrypted: " + filename);
    } catch (Exception e) {
        System.err.println("Error saving file: " + e.getMessage());
    }
}
```

**Load Logic with Backward Compatibility**:
```java
private static Object loadFromFile(String filename) {
    File file = new File(filename);
    if (!file.exists()) return null;
    
    try {
        // Try loading as ENCRYPTED file first
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        
        FileInputStream fileIn = new FileInputStream(filename);
        CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
        ObjectInputStream in = new ObjectInputStream(cipherIn);
        
        Object data = in.readObject();
        in.close();
        
        System.out.println("Loaded encrypted: " + filename);
        return data;
        
    } catch (Exception e) {
        // Check if it's a decryption error (old unencrypted file)
        boolean isDecryptionError = 
            e instanceof javax.crypto.BadPaddingException ||
            e instanceof java.io.StreamCorruptedException ||
            (e.getCause() != null && 
             e.getCause() instanceof javax.crypto.BadPaddingException);
        
        if (isDecryptionError) {
            System.out.println("File is unencrypted, migrating...");
            
            try {
                // Load as PLAIN file
                ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(filename)
                );
                Object data = in.readObject();
                in.close();
                
                // Re-save as encrypted
                saveToFile(filename, data);
                
                return data;
            } catch (Exception e2) {
                System.err.println("Failed to load file: " + filename);
                return null;
            }
        }
        
        return null;
    }
}
```

**Why this complexity**: Ensures smooth transition from old unencrypted files to new encrypted files without losing data.

---

### 📂 `src/gui/` Directory
Contains all GUI-related code.

#### 🎨 `Theme.java`
**Purpose**: Centralized color scheme and styling

**What it defines**:
- Colors (background, buttons, text)
- Fonts (sizes, weights)
- Dimensions (sidebar width, button sizes)

**Color Scheme**:
```java
// Coral/Cream Theme
public static final Color BACKGROUND = new Color(0x93BFC7);      // Teal
public static final Color PRIMARY_BLUE = new Color(0xABE7B2);    // Medium Green
public static final Color LIGHT_BLUE = new Color(0xCBF3BB);      // Light Green
public static final Color CARD_BACKGROUND = new Color(0xECF4E8); // Very Light Green
public static final Color ACCENT_RED = new Color(0xDC, 0x14, 0x3C); // Bright Red
public static final Color SIDEBAR_TEXT = Color.BLACK;
public static final Color TEXT_PRIMARY = Color.BLACK;
```

**Why centralized**: Easy to change entire app theme by modifying one file.

#### 🖼️ `MainApplication.java`
**Purpose**: Main JFrame window container

**What it does**:
- Creates the main window
- Uses CardLayout to switch between login/registration/main app
- Handles window properties (size, title, close operation)

**Card System**:
```java
CardLayout cardLayout = new CardLayout();
JPanel container = new JPanel(cardLayout);

container.add(loginPanel, "login");
container.add(registrationPanel, "register");
container.add(mainAppPanel, "main");

// Switch screens
cardLayout.show(container, "login"); // Show login
```

#### 📂 `src/gui/components/` - Reusable UI Components

**Purpose**: Custom Swing components with modern styling

**`ModernButton.java`**: Styled button with hover effects
**`ModernTextField.java`**: Styled text input with placeholder
**`ModernPasswordField.java`**: Styled password input with placeholder
**`RoundedPanel.java`**: Panel with rounded corners

**Why custom components**: Consistent styling across the entire application.

#### 📂 `src/gui/panels/` - Application Screens

#### 🔐 `LoginPanel.java`
**Purpose**: User login screen

**Key Logic**:
```java
private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());
    
    if (username.isEmpty() || password.isEmpty()) {
        showError("Please fill in all fields");
        return;
    }
    
    // Attempt login
    User user = system.login(username, password);
    
    if (user == null) {
        // Check if user is banned
        if (system.isLoginFailedDueToBan(username)) {
            showError("Account is banned");
        } else {
            showError("Invalid username or password");
        }
    } else {
        // Success - show main app
        onLoginSuccess.run();
    }
}
```

#### 📝 `RegistrationPanel.java`
**Purpose**: New user registration

**Validation Logic**:
- Username: 3-20 characters, alphanumeric + underscore
- Password: At least 6 characters
- Email: Must contain @ and .
- Full name: Required

#### 🏠 `MainAppPanel.java`
**Purpose**: Main application layout with sidebar navigation

**Structure**:
```
┌─────────────┬──────────────────────────┐
│             │                          │
│   Sidebar   │     Content Panel        │
│             │   (Feed/Profile/etc.)    │
│  - Logo     │                          │
│  - Feed     │                          │
│  - Profile  │                          │
│  - Search   │                          │
│  - Create   │                          │
│             │                          │
│  - User     │                          │
│  - Logout   │                          │
└─────────────┴──────────────────────────┘
```

**Navigation Logic**:
```java
feedButton.addActionListener(e -> {
    feedPanel.loadPosts(); // Refresh feed
    showPanel("feed");     // Switch to feed
    setActiveSidebarButton(feedButton); // Highlight button
});
```

#### 📰 `FeedPanel.java`
**Purpose**: Display posts feed

**Post Card Structure**:
```
┌─────────────────────────────────┐
│ [Avatar] John Doe ✓             │
│          @john_doe              │
│                                 │
│ Post content here...            │
│ [Image if ImagePost]            │
│                                 │
│ ❤ 15  💬 3  🔄 1               │
│                                 │
│ [Like] [Comment] [Delete?]      │
│                                 │
│ Comment Section:                │
│ • Alice: Great post!            │
│ • Bob: Thanks for sharing       │
│ View all 3 comments →           │
└─────────────────────────────────┘
```

**Like Button Logic**:
```java
likeButton.addActionListener(e -> {
    system.toggleLike(post.getId());
    loadPosts(); // Refresh to show updated count
});
```

**Comment Logic**:
```java
commentButton.addActionListener(e -> {
    String comment = JOptionPane.showInputDialog("Add a comment:");
    if (comment != null && !comment.trim().isEmpty()) {
        system.addComment(post.getId(), comment.trim());
        loadPosts(); // Refresh to show new comment
    }
});
```

#### 👤 `ProfilePanel.java`
**Purpose**: Display user profiles

**Two Modes**:

**1. Own Profile View**:
- Edit profile button
- Request verification button (for regular users)
- Manage verification requests button (for admins)
- View followers/following

**2. Other User's Profile View**:
- Follow/Unfollow button
- Message button (placeholder)
- Ban/Unban button (for admins viewing regular users)
- Verify/Unverify button (for admins viewing regular users)

**Profile Stats Display**:
```java
private JPanel createStatLabel(String value, String label) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    
    JLabel valueLabel = new JLabel(value);
    valueLabel.setFont(Theme.FONT_HEADING);
    valueLabel.setAlignmentX(CENTER_ALIGNMENT);
    
    JLabel labelText = new JLabel(label);
    labelText.setFont(Theme.FONT_SMALL);
    labelText.setAlignmentX(CENTER_ALIGNMENT);
    
    panel.add(valueLabel);
    panel.add(labelText);
    
    return panel;
}
```

#### 🔍 `SearchPanel.java`
**Purpose**: Search for users

**Search Logic**:
```java
private void performSearch(String query) {
    List<User> results = new ArrayList<>();
    
    for (User user : system.getAllUsers()) {
        // Search by username or full name (case-insensitive)
        if (user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
            user.getFullName().toLowerCase().contains(query.toLowerCase())) {
            results.add(user);
        }
    }
    
    displayResults(results);
}
```

**Admin Features**:
- View verification requests button (only visible for admins)
- Shows number of pending requests

#### ✏️ `CreatePostPanel.java`
**Purpose**: Create new posts

**Tabbed Interface**:
- Tab 1: Text Post (text area)
- Tab 2: Image Post (file chooser + caption)

**Image Upload Logic**:
```java
private void selectImage() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter(
        "Image files", "jpg", "jpeg", "png", "gif", "bmp"
    ));
    
    int result = fileChooser.showOpenDialog(this);
    
    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        selectedImagePath = selectedFile.getAbsolutePath();
        imagePathLabel.setText(selectedFile.getName());
    }
}
```

**Post Creation**:
```java
private void publishTextPost() {
    String content = textArea.getText().trim();
    
    if (content.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Please enter some text", 
            "Empty Post", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    Post post = system.createTextPost(content);
    
    if (post != null) {
        JOptionPane.showMessageDialog(this,
            "Post published successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        textArea.setText(""); // Clear
    }
}
```

---

## 🔒 Security Implementation

### 1. Password Security (SHA-256 Hashing)

**Why Hash Passwords?**
- Storing plain-text passwords is a security risk
- If data files are stolen, passwords would be exposed
- Hashing makes passwords unreadable

**How SHA-256 Works**:
```
Plain Password: "password123"
         ↓
    SHA-256 Hash
         ↓
"ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"
```

**Properties**:
- One-way function (cannot reverse the hash)
- Same input always produces same output
- Different inputs produce completely different outputs
- Fixed length (64 hexadecimal characters = 256 bits)

**Implementation in User.java**:
```java
// When user registers or changes password
public void setPassword(String password) {
    this.password = hashPassword(password); // Store hash, not plain text
}

// When user logs in
public boolean validatePassword(String passwordToCheck) {
    String hashedInput = hashPassword(passwordToCheck);
    return this.password.equals(hashedInput); // Compare hashes
}
```

**Example**:
```
Registration:
Input: "password123"
Stored: "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"

Login:
Input: "password123"
Hash: "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"
Compare: Stored hash == Input hash? → YES → Login Success

Wrong Password:
Input: "wrongpass"
Hash: "4a7d1ed414474e4033ac29ccb8653d9aafc2c5b1f5a8f8d8c5b2c8c8c8c8c8c8"
Compare: Stored hash == Input hash? → NO → Login Failed
```

### 2. File Encryption (AES)

**Why Encrypt Files?**
- Protects user data if files are accessed
- Prevents unauthorized reading of data
- Industry-standard security practice

**How AES Works**:
```
Plain Data → [AES Encryption with Key] → Encrypted Data
Encrypted Data → [AES Decryption with Key] → Plain Data
```

**Key Points**:
- **Symmetric encryption**: Same key for encrypt/decrypt
- **Key**: "MySocialNetwork!" (16 bytes for AES-128)
- **Cannot decrypt without the key**

**Implementation Flow**:

**Saving Data**:
```
User Object
    ↓
Serialize to bytes (ObjectOutputStream)
    ↓
Encrypt bytes with AES (CipherOutputStream)
    ↓
Write to users.dat
```

**Loading Data**:
```
Read from users.dat
    ↓
Decrypt bytes with AES (CipherInputStream)
    ↓
Deserialize to User Object (ObjectInputStream)
    ↓
User Object ready to use
```

**Code Flow in DataPersistence.java**:
```java
// SAVE
SecretKeySpec key = new SecretKeySpec("MySocialNetwork!".getBytes(), "AES");
Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
cipher.init(Cipher.ENCRYPT_MODE, key);

FileOutputStream fileOut = new FileOutputStream("data/users.dat");
CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
ObjectOutputStream objOut = new ObjectOutputStream(cipherOut);
objOut.writeObject(usersList); // Encrypted automatically
objOut.close();

// LOAD
cipher.init(Cipher.DECRYPT_MODE, key);
FileInputStream fileIn = new FileInputStream("data/users.dat");
CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
ObjectInputStream objIn = new ObjectInputStream(cipherIn);
List<User> users = (List<User>) objIn.readObject(); // Decrypted automatically
objIn.close();
```

### 3. Backward Compatibility & Migration

**Problem**: What if old data files exist without encryption/hashing?

**Solution**: Graceful migration

**Password Migration**:
```java
// After loading data, check all passwords
for (User user : users) {
    if (user.getPassword().length() != 64) { // Not a SHA-256 hash
        // This is a plain-text password from old version
        user.setPassword(user.getPassword()); // Re-save as hash
    }
}
saveData(); // Persist migrated data
```

**File Migration**:
```java
// Try to load as encrypted
try {
    return loadEncrypted(filename);
} catch (BadPaddingException e) {
    // Failed to decrypt → must be old unencrypted file
    Object data = loadUnencrypted(filename);
    saveEncrypted(filename, data); // Migrate to encrypted
    return data;
}
```

**Why this matters**: Existing users can upgrade without losing their accounts.

---

## 🔄 Data Flow & Logic

### User Registration Flow
```
1. User fills registration form
   ↓
2. RegistrationPanel validates input
   ↓
3. SocialNetworkSystem.registerUser() called
   ↓
4. Check if username exists
   ↓
5. Create new RegularUser (password auto-hashed)
   ↓
6. Assign unique ID (nextUserId++)
   ↓
7. Add to users list
   ↓
8. DataPersistence.saveAllData() (encrypted)
   ↓
9. Return to login screen
```

### Login Flow
```
1. User enters credentials
   ↓
2. LoginPanel.handleLogin() called
   ↓
3. SocialNetworkSystem.login(username, password)
   ↓
4. Find user by username
   ↓
5. Check if banned
   ↓
6. Validate password (hash comparison)
   ↓
7. If valid: Set currentUser, show MainAppPanel
   ↓
8. If invalid: Show error message
```

### Creating a Post Flow
```
1. User types content in CreatePostPanel
   ↓
2. Click "Publish"
   ↓
3. SocialNetworkSystem.createTextPost(content)
   ↓
4. Check if user is logged in
   ↓
5. Check if user can create posts
   ↓
6. Create new TextPost with unique ID
   ↓
7. Add to posts list
   ↓
8. Add to user's post history
   ↓
9. DataPersistence.saveAllData()
   ↓
10. Clear text area
   ↓
11. Show success message
```

### Feed Generation Flow
```
1. User clicks "Feed" button
   ↓
2. MainAppPanel switches to FeedPanel
   ↓
3. FeedPanel.loadPosts() called
   ↓
4. SocialNetworkSystem.getFeedPosts()
   ↓
5. If admin: Return ALL posts
   ↓
6. If regular user:
   - Get posts from followed users
   - Get own posts
   ↓
7. Sort by timestamp (newest first)
   ↓
8. Return list
   ↓
9. FeedPanel creates UI card for each post
   ↓
10. Display in scrollable panel
```

### Like/Unlike Flow
```
1. User clicks "Like" button
   ↓
2. FeedPanel calls system.toggleLike(postId)
   ↓
3. SocialNetworkSystem finds the post
   ↓
4. Check if user already liked:
   - If yes: Remove like
   - If no: Add like
   ↓
5. Update post's like list
   ↓
6. DataPersistence.saveAllData()
   ↓
7. FeedPanel.loadPosts() to refresh display
   ↓
8. UI updates: Heart becomes red, count increases
```

### Follow/Unfollow Flow
```
1. User clicks "Follow" on profile
   ↓
2. ProfilePanel calls system.followUser(userId)
   ↓
3. Check if already following:
   - If yes: Unfollow
   - If no: Follow
   ↓
4. Update currentUser's following list
   ↓
5. Update target user's followers list
   ↓
6. DataPersistence.saveAllData()
   ↓
7. ProfilePanel.refreshProfile()
   ↓
8. Button text changes to "Unfollow"/"Follow"
```

### Comment Flow
```
1. User clicks "Comment" button
   ↓
2. FeedPanel shows input dialog
   ↓
3. User types comment
   ↓
4. SocialNetworkSystem.addComment(postId, commentText)
   ↓
5. Create new Comment object with unique ID
   ↓
6. Add comment to post's comment list
   ↓
7. DataPersistence.saveAllData()
   ↓
8. FeedPanel.loadPosts() to refresh
   ↓
9. Comment appears under post
```

### Admin Ban Flow
```
1. Admin views user's profile
   ↓
2. Clicks "Ban User" button
   ↓
3. Confirmation dialog
   ↓
4. ProfilePanel calls system.banUser(userId)
   ↓
5. SocialNetworkSystem finds user
   ↓
6. Set user.setBanned(true)
   ↓
7. DataPersistence.saveAllData()
   ↓
8. User is logged out if currently logged in
   ↓
9. Cannot login until unbanned
```

### Verification Request Flow
```
1. Regular user clicks "Request Verification"
   ↓
2. ProfilePanel calls system.requestVerification()
   ↓
3. Create VerificationRequest object
   ↓
4. Set status to "PENDING"
   ↓
5. Add to verification requests list
   ↓
6. DataPersistence.saveAllData()
   ↓
7. Button text changes to "Verification Pending"
   ↓
8. Admin sees request in "Manage Verification Requests"
   ↓
9. Admin can approve/reject:
   - Approve: user.setVerified(true)
   - Reject: Remove request
   ↓
10. User gets verified badge (✓) next to name
```

---

## 🎨 Design Patterns Used

### 1. Singleton Pattern
**Where**: `SocialNetworkSystem.java`

**Why**: Only one instance of the system should exist

**Implementation**:
```java
public class SocialNetworkSystem {
    private static SocialNetworkSystem instance;
    
    private SocialNetworkSystem() {
        // Private constructor prevents direct instantiation
    }
    
    public static SocialNetworkSystem getInstance() {
        if (instance == null) {
            instance = new SocialNetworkSystem();
        }
        return instance;
    }
}
```

**Benefits**:
- Global access point
- Controlled instantiation
- Single source of truth for system state

### 2. Factory Pattern (Implicit)
**Where**: Post creation methods

**Why**: Different post types need different creation logic

**Implementation**:
```java
public Post createTextPost(String content) {
    return new TextPost(nextPostId++, currentUser.getId(), content);
}

public Post createImagePost(String imagePath, String caption) {
    return new ImagePost(nextPostId++, currentUser.getId(), imagePath, caption);
}
```

### 3. Observer Pattern (Callback-style)
**Where**: Panel navigation

**Why**: Panels need to notify parent when events occur

**Implementation**:
```java
// In LoginPanel
private Runnable onLoginSuccess;

public void setOnLoginSuccess(Runnable callback) {
    this.onLoginSuccess = callback;
}

// When login succeeds
if (onLoginSuccess != null) {
    onLoginSuccess.run(); // Notify parent
}

// In MainApplication
loginPanel.setOnLoginSuccess(() -> {
    cardLayout.show(container, "main"); // Switch to main app
});
```

### 4. Model-View-Controller (MVC)
**Structure**:
- **Model**: User, Post, Comment classes
- **View**: GUI panels (LoginPanel, FeedPanel, etc.)
- **Controller**: SocialNetworkSystem

**Benefits**:
- Separation of concerns
- Easier to maintain
- Can change UI without touching logic

### 5. Template Method Pattern
**Where**: Abstract `Post` and `User` classes

**Why**: Define common behavior, let subclasses implement specifics

**Implementation**:
```java
public abstract class Post {
    // Common behavior (same for all post types)
    public void addLike(int userId) { /* implementation */ }
    public void addComment(Comment comment) { /* implementation */ }
    
    // Specific behavior (different for each type)
    public abstract String getPostType();
    public abstract String getContent();
    public abstract String getDisplayContent();
}

// Subclasses implement specific methods
public class TextPost extends Post {
    @Override
    public String getPostType() { return "TEXT"; }
    // ... etc
}
```

---

## 🚀 Setup & Running

### Prerequisites
- **Java JDK 24.0.2** or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code) or command line

### Method 1: Using IDE (Recommended for beginners)

#### IntelliJ IDEA
1. Open IntelliJ IDEA
2. Click "File" → "Open"
3. Navigate to the `SocialNetwork` folder and select it
4. Wait for the project to load
5. Right-click `src/Main.java`
6. Click "Run 'Main.main()'"

#### VS Code
1. Open VS Code
2. Install "Extension Pack for Java" from Extensions
3. Open the `SocialNetwork` folder
4. Open `src/Main.java`
5. Click the "Run" button above the `main` method
   (Or press F5)

#### Eclipse
1. Open Eclipse
2. Click "File" → "Open Projects from File System"
3. Navigate to `SocialNetwork` folder
4. Right-click `src/Main.java` in Project Explorer
5. Click "Run As" → "Java Application"

### Method 2: Using Command Line

#### Windows PowerShell
```powershell
# Navigate to project folder
cd D:\SocialNetwork

# Compile all source files
javac -d out -sourcepath src src/Main.java src/gui/*.java src/gui/components/*.java src/gui/panels/*.java src/models/*.java src/system/*.java

# Run the application
cd out
java Main
```

#### Windows Command Prompt
```cmd
cd D:\SocialNetwork
javac -d out -sourcepath src src/Main.java src/gui/*.java src/gui/components/*.java src/gui/panels/*.java src/models/*.java src/system/*.java
cd out
java Main
```

#### Linux/Mac Terminal
```bash
cd /path/to/SocialNetwork
javac -d out -sourcepath src $(find src -name "*.java")
cd out
java Main
```

### Verifying Installation
```bash
java -version
# Should show: java version "24.0.2" or higher

javac -version
# Should show: javac 24.0.2 or higher
```

### Troubleshooting

#### "java: error: invalid source release: 24"
**Solution**: Your Java version is too old. Install JDK 24.

#### "Could not find or load main class Main"
**Solution**: Make sure you're running from the `out` directory after compilation.

#### "UnsupportedClassVersionError"
**Solution**: Compiled with newer Java version than runtime. Recompile with same version.

#### Application doesn't start
**Solution**: 
1. Delete the `data` folder
2. Restart the application
3. Re-register users

---

## 📚 Common Questions & Answers

### Q1: What programming paradigm does this project use?
**A**: Object-Oriented Programming (OOP). The project demonstrates:
- **Encapsulation**: Private fields with getters/setters
- **Inheritance**: User → RegularUser/Admin, Post → TextPost/ImagePost
- **Polymorphism**: Different behaviors for different user/post types
- **Abstraction**: Abstract User and Post classes define contracts

### Q2: How is data stored?
**A**: Data is stored in serialized `.dat` files in the `data/` directory:
- `users.dat`: All user accounts (encrypted with AES)
- `posts.dat`: All posts (encrypted with AES)
- `counters.dat`: ID counters (encrypted with AES)
- `verification_requests.dat`: Verification requests (encrypted with AES)

Files use Java serialization and are encrypted with AES-128.

### Q3: Is the password storage secure?
**A**: Yes. Passwords are hashed using SHA-256 before storage:
- Plaintext passwords are never stored
- Hashes are irreversible (cannot get original password from hash)
- Each password produces a unique 64-character hash
- Login compares hash of input with stored hash

### Q4: Can someone read the data files?
**A**: Not without the encryption key. All data files are encrypted using AES:
- Files are encrypted before writing
- Files are decrypted when loading
- Without the key ("MySocialNetwork!"), data appears as gibberish
- Industry-standard encryption (same used by banks)

### Q5: What design patterns are used?
**A**:
1. **Singleton**: SocialNetworkSystem (only one instance)
2. **Factory**: Post creation methods
3. **Observer**: Callback-based event handling
4. **MVC**: Model (data) + View (GUI) + Controller (system)
5. **Template Method**: Abstract classes with concrete implementations

### Q6: How does the feed algorithm work?
**A**:
- **Regular users**: See posts from people they follow + their own posts
- **Admins**: See all posts from all users
- Posts are sorted by timestamp (newest first)
- Implemented in `SocialNetworkSystem.getFeedPosts()`

### Q7: What happens when a user is banned?
**A**:
- User's `banned` flag is set to `true`
- If currently logged in, they're logged out immediately
- Cannot login (login method checks banned status)
- Profile still visible to admins
- Can be unbanned by admin

### Q8: How are IDs generated?
**A**: Auto-increment counters:
```java
private int nextUserId = 1;    // Starts at 1
private int nextPostId = 1;
private int nextCommentId = 1;

// When creating new user
User newUser = new User(nextUserId++, ...); // Assigns 1, then increments to 2
```
Counters are saved to `counters.dat` so IDs remain unique across sessions.

### Q9: Can the application handle multiple users simultaneously?
**A**: No. This is a single-user desktop application (not a server):
- One user can be logged in at a time
- No network/database (uses local files)
- For multi-user: Would need client-server architecture

### Q10: How do I add a new feature?
**A**: Follow MVC pattern:
1. **Model**: Add data fields/methods to appropriate class
2. **Controller**: Add logic to SocialNetworkSystem
3. **View**: Add UI components to appropriate panel
4. **Persistence**: Update DataPersistence if new data needs saving

Example - Adding "Share Count":
```java
// 1. Model (Post.java)
private int shareCount = 0;
public void incrementShareCount() { shareCount++; }
public int getShareCount() { return shareCount; }

// 2. Controller (SocialNetworkSystem.java)
public void sharePost(int postId) {
    Post post = getPostById(postId);
    post.incrementShareCount();
    saveData();
}

// 3. View (FeedPanel.java)
JButton shareBtn = new JButton("Share (" + post.getShareCount() + ")");
shareBtn.addActionListener(e -> {
    system.sharePost(post.getId());
    loadPosts(); // Refresh
});
```

### Q11: What is the difference between Admin and RegularUser?
**A**:

| Feature | RegularUser | Admin |
|---------|-------------|-------|
| Create posts | ✅ Yes | ✅ Yes |
| Like/Comment | ✅ Yes | ✅ Yes |
| Follow users | ✅ Yes | ❌ No |
| See own followers | ✅ Yes | ❌ No |
| See all posts in feed | ❌ No | ✅ Yes |
| Ban users | ❌ No | ✅ Yes |
| Verify users | ❌ No | ✅ Yes |
| Request verification | ✅ Yes | ❌ No |
| Manage other users | ❌ No | ✅ Yes |

### Q12: How does the GUI update in real-time?
**A**: Event-driven programming:
```java
// When data changes
system.addComment(postId, comment); // Update data
saveData();                        // Persist to disk
loadPosts();                       // Reload UI

// loadPosts() clears panel and recreates all post cards
// User sees updated data immediately
```

### Q13: Why use abstract classes instead of interfaces?
**A**:
- Abstract classes can have **both** implemented methods and abstract methods
- `User` has common data (username, password) and common behavior (validatePassword)
- Subclasses inherit this common code
- Only implement what's different (getUserType, canCreatePost, etc.)

Interfaces would require duplicating common code in every subclass.

### Q14: How secure is this application?
**A**:

**What's Secure**:
- ✅ Passwords hashed with SHA-256
- ✅ Files encrypted with AES
- ✅ Cannot read data files without key
- ✅ Cannot reverse password hashes

**What's Not Secure** (educational project):
- ⚠️ Encryption key hardcoded in source code
- ⚠️ No network security (not a networked app)
- ⚠️ No SQL injection protection (no database)
- ⚠️ No rate limiting on login attempts

For production: Use bcrypt/scrypt for passwords, external key management, HTTPS for network.

### Q15: Can I change the color theme?
**A**: Yes! Edit `src/gui/Theme.java`:
```java
// Change these colors
public static final Color BACKGROUND = new Color(0x93BFC7);
public static final Color PRIMARY_BLUE = new Color(0xABE7B2);
// ... etc

// Or use hex codes directly
public static final Color BACKGROUND = new Color(0xFF5733);
```
All panels use Theme.java, so changes apply everywhere.

### Q16: What happens when I delete the `data` folder?
**A**: The application resets completely:
- All users are deleted (except hardcoded demo users)
- All posts are deleted
- All follows/likes/comments are deleted
- ID counters reset to 1
- Essentially a "factory reset"

### Q17: How are images stored?
**A**:
- Image file path is stored in ImagePost (e.g., "C:\Users\...\image.jpg")
- Actual image file stays in its original location
- When displaying, FeedPanel reads the file and creates ImageIcon
- If image file is moved/deleted, post shows error message

### Q18: What is serialization?
**A**: Converting objects to bytes for storage:
```
User object in memory:
{
  id: 1,
  username: "john_doe",
  password: "ef92b778...",
  ...
}
         ↓ Serialization
Byte array: [AC ED 00 05 73 72 00 0B ...]
         ↓ Write to file
users.dat (binary file)
         ↓ Read from file
Byte array: [AC ED 00 05 73 72 00 0B ...]
         ↓ Deserialization
User object in memory (reconstructed)
```

Allows saving complex objects to disk and loading them later.

### Q19: Why use Swing instead of JavaFX?
**A**: Swing is:
- Built into Java (no extra libraries)
- Simpler to learn
- Well-documented
- Sufficient for desktop applications
- Widely used in educational contexts

JavaFX is more modern but requires additional setup.

### Q20: Can this be converted to a web application?
**A**: Yes, but requires major changes:
1. **Backend**: Convert to Spring Boot (REST API)
2. **Frontend**: Replace Swing with HTML/CSS/JavaScript
3. **Database**: Replace .dat files with MySQL/PostgreSQL
4. **Authentication**: Use JWT tokens instead of sessions
5. **Deployment**: Host on cloud (AWS, Heroku, etc.)

The business logic (SocialNetworkSystem) could largely remain the same.

---

## 🎓 Learning Outcomes

After studying this project, you should understand:

### Programming Concepts
- ✅ Object-Oriented Programming (OOP) principles
- ✅ Inheritance hierarchies (superclass → subclass)
- ✅ Abstract classes vs concrete classes
- ✅ Encapsulation (private fields, public methods)
- ✅ Polymorphism (same method, different behaviors)
- ✅ Collections (List, ArrayList, HashMap usage)
- ✅ Exception handling (try-catch blocks)
- ✅ File I/O (reading and writing files)
- ✅ Serialization (object persistence)

### Security Concepts
- ✅ Password hashing (SHA-256)
- ✅ File encryption (AES)
- ✅ Why not to store plain-text passwords
- ✅ Symmetric vs asymmetric encryption
- ✅ Hash functions vs encryption

### Software Design
- ✅ MVC architecture pattern
- ✅ Singleton pattern
- ✅ Factory pattern
- ✅ Observer pattern (callbacks)
- ✅ Separation of concerns
- ✅ Code organization (packages)
- ✅ Modular design

### GUI Development
- ✅ Swing components (JFrame, JPanel, JButton, etc.)
- ✅ Layout managers (BorderLayout, BoxLayout, CardLayout, etc.)
- ✅ Event listeners (ActionListener, MouseAdapter)
- ✅ Custom components (extending Swing classes)
- ✅ UI/UX principles (spacing, colors, fonts)

### Data Management
- ✅ CRUD operations (Create, Read, Update, Delete)
- ✅ In-memory data structures
- ✅ File-based persistence
- ✅ Data validation
- ✅ Relationships (one-to-many, many-to-many)

---

## 📖 Additional Resources

### Learn More About...

**Java Swing**:
- [Oracle Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
- [Swing Layout Managers](https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html)

**Java Serialization**:
- [Java Object Serialization](https://docs.oracle.com/javase/tutorial/jndi/objects/serial.html)

**Cryptography**:
- [SHA-256 Explained](https://en.wikipedia.org/wiki/SHA-2)
- [AES Encryption](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard)

**Design Patterns**:
- [Singleton Pattern](https://refactoring.guru/design-patterns/singleton)
- [MVC Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)

---

## 🤝 Contributing

This is an educational project. Feel free to:
- Fork and modify for learning
- Add new features as practice
- Experiment with different designs
- Share with classmates

---

## 📝 License

This project is for educational purposes. Use freely for learning and teaching.

---

## 👨‍🏫 For Teachers

This project can be used to teach:
- **Week 1-2**: OOP basics (classes, objects, inheritance)
- **Week 3-4**: GUI development (Swing components)
- **Week 5-6**: Design patterns (Singleton, MVC)
- **Week 7-8**: Data persistence (serialization, file I/O)
- **Week 9-10**: Security (hashing, encryption)

### Assessment Ideas:
1. **Add a new feature**: Messaging between users
2. **Modify existing feature**: Change feed algorithm
3. **Improve security**: Add salt to password hashing
4. **Enhance UI**: Add profile pictures
5. **Code review**: Find and fix potential bugs

---

**🎉 Happy Learning with SocialConnect!**

For questions or issues, review the code structure and logic flow sections above.

---

**Project Statistics**:
- **Total Files**: 20+ Java files
- **Lines of Code**: ~5,000+
- **Concepts Covered**: 15+
- **Design Patterns**: 5
- **Security Features**: 2 (Hashing + Encryption)
#   S o c i a l C o n n e c t  
 