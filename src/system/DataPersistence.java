package system;

import models.*;
import java.io.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * DataPersistence - Handles saving and loading application data
 */
public class DataPersistence {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.dat";
    private static final String POSTS_FILE = DATA_DIR + File.separator + "posts.dat";
    private static final String COUNTERS_FILE = DATA_DIR + File.separator + "counters.dat";
    private static final String VERIFICATION_REQUESTS_FILE = DATA_DIR + File.separator + "verification_requests.dat";
    private static final String COMMENT_REPORTS_FILE = DATA_DIR + File.separator + "comment_reports.dat";
    
    // Encryption key 
    private static final String ENCRYPTION_KEY_STRING = "SocialNetwork2024!"; // 16 characters for AES-128
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    
    /**
     * Get the encryption key
     */
    private static SecretKey getSecretKey() {
        byte[] keyBytes = ENCRYPTION_KEY_STRING.getBytes();
        // Ensure key is exactly 16 bytes for AES-128
        byte[] key = new byte[16];
        System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 16));
        return new SecretKeySpec(key, ALGORITHM);
    }
    
    // Save all data to files
    public static void saveData(ArrayList<User> users, ArrayList<Post> posts, 
                                int userCounter, int postCounter, int commentCounter,
                                int reportCounter,
                                ArrayList<VerificationRequest> verificationRequests,
                                ArrayList<CommentReport> commentReports) {
        // Create data folder if it doesn't exist
        new File(DATA_DIR).mkdirs();
        
        // Save each type of data
        saveToFile(USERS_FILE, users);
        saveToFile(POSTS_FILE, posts);
        saveToFile(COUNTERS_FILE, new int[]{userCounter, postCounter, commentCounter, reportCounter});
        saveToFile(VERIFICATION_REQUESTS_FILE, verificationRequests);
        saveToFile(COMMENT_REPORTS_FILE, commentReports);
    }
    
    // Load all data from files
    @SuppressWarnings("unchecked")
    public static LoadResult loadData() {
        LoadResult result = new LoadResult();
        
        // Load users
        Object usersObj = loadFromFile(USERS_FILE);
        if (usersObj != null) {
            result.users = (ArrayList<User>) usersObj;
        }
        
        // Load posts
        Object postsObj = loadFromFile(POSTS_FILE);
        if (postsObj != null) {
            result.posts = (ArrayList<Post>) postsObj;
        }
        
        // Load counters
        Object countersObj = loadFromFile(COUNTERS_FILE);
        if (countersObj != null) {
            int[] counters = (int[]) countersObj;
            result.userIdCounter = counters[0];
            result.postIdCounter = counters[1];
            result.commentIdCounter = counters[2];
            // Handle backward compatibility: old files might only have 3 counters
            if (counters.length > 3) {
                result.reportIdCounter = counters[3];
            }
        }
        
        // Load verification requests
        Object verificationRequestsObj = loadFromFile(VERIFICATION_REQUESTS_FILE);
        if (verificationRequestsObj != null) {
            // Handle backward compatibility: old files might have ArrayList<String>
            if (verificationRequestsObj instanceof ArrayList) {
                ArrayList<?> list = (ArrayList<?>) verificationRequestsObj;
                if (!list.isEmpty() && list.get(0) instanceof VerificationRequest) {
                    result.verificationRequests = (ArrayList<VerificationRequest>) verificationRequestsObj;
                } else {
                    // Old format - convert String list to empty VerificationRequest list
                    result.verificationRequests = new ArrayList<>();
                }
            }
        }
        
        // Load comment reports
        Object commentReportsObj = loadFromFile(COMMENT_REPORTS_FILE);
        if (commentReportsObj != null) {
            result.commentReports = (ArrayList<CommentReport>) commentReportsObj;
        }
        
        result.success = (usersObj != null);
        return result;
    }
    
    // Helper: Save any object to a file (with encryption)
    private static void saveToFile(String filename, Object data) {
        try {
            // Create cipher for encryption
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            
            // Write encrypted data
            FileOutputStream fileOut = new FileOutputStream(filename);
            CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
            ObjectOutputStream out = new ObjectOutputStream(cipherOut);
            
            out.writeObject(data);
            out.close();
            cipherOut.close();
            fileOut.close();
        } catch (Exception e) {
            System.out.println("Error saving " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Helper: Load any object from a file (with decryption)
    private static Object loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filename);
            return null;
        }
        try {
            // Try to load as encrypted file first
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            
            FileInputStream fileIn = new FileInputStream(filename);
            CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
            ObjectInputStream in = new ObjectInputStream(cipherIn);
            
            Object data = in.readObject();
            in.close();
            cipherIn.close();
            fileIn.close();
            System.out.println("Successfully loaded encrypted file: " + filename);
            return data;
        } catch (Exception e) {
            // Check if this is a decryption error (file might be unencrypted)
            Throwable cause = e.getCause();
            boolean isDecryptionError = e instanceof javax.crypto.BadPaddingException || 
                                       e instanceof java.io.StreamCorruptedException ||
                                       (cause != null && cause instanceof javax.crypto.BadPaddingException);
            
            if (isDecryptionError) {
                // File is not encrypted or corrupted, try as unencrypted
                System.out.println("File appears to be unencrypted, attempting to load as plain: " + filename);
            } else {
                System.err.println("Error decrypting file " + filename + ": " + e.getMessage());
            }
            
            // Try loading as unencrypted
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
                Object data = in.readObject();
                in.close();
                // If successful, re-save as encrypted for future
                System.out.println("Migrating " + filename + " to encrypted format...");
                saveToFile(filename, data);
                return data;
            } catch (Exception e2) {
                System.err.println("Failed to load file in any format: " + filename);
                System.err.println("Encryption error: " + e.getMessage());
                System.err.println("Unencrypted load error: " + e2.getMessage());
                return null;
            }
        }
    }
    
    // Result class for loading data
    public static class LoadResult {
        public ArrayList<User> users = new ArrayList<>();
        public ArrayList<Post> posts = new ArrayList<>();
        public int userIdCounter = 1;
        public int postIdCounter = 1;
        public int commentIdCounter = 1;
        public int reportIdCounter = 1;
        public ArrayList<VerificationRequest> verificationRequests = new ArrayList<>();
        public ArrayList<CommentReport> commentReports = new ArrayList<>();
        public boolean success = false;
    }
}
