package megabudgetstonks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Login {
    private final int KEY = 5;
    private final String DB_PATH = "src/main/java/megabudgetstonks/logins.db";

    public Login() {
    }

    public boolean login(String username, String password) {
        try {
            // Searches for username & password combination
            BufferedReader br = new BufferedReader(new FileReader(DB_PATH));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.equals(username) && br.readLine().equals(encryptPassword(password))) {
                    br.close();
                    return true;
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean createUser(String username, String password) {
        try {
            // Determine if username is unique
            BufferedReader br = new BufferedReader(new FileReader(DB_PATH));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.equals(username)) {
                    br.close();
                    return false;
                }
                br.readLine();
            }
            br.close();

            // Write username and encrypted password to file
            BufferedWriter bw = new BufferedWriter(new FileWriter(DB_PATH, true));
            bw.write(username + "\n");
            bw.write(encryptPassword(password) + "\n");
            bw.close();
            return true;
        } catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }

    private String encryptPassword(String password) {
        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] += KEY;
        }
        return String.valueOf(chars);
    }
}
