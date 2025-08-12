package com.pandaPass.repositories;

import com.pandaPass.models.Vault;
import com.pandaPass.persistence.DB;
import com.pandaPass.utils.EncryptionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Repository class providing methods for interacting with the 'users' table.
 * Inserting the users, retrieving authentication data and vaults as well as updating vaults.
 *
 */
public class UserRepository {

    /**
     * Insert a new user into the database with a new vault.
     * @param mail User's mail
     * @param salt User-specific salt
     * @param authenticationHash User's derived authentication hash
     * @param key Encryption key used for the user's vault
     * @return True if insertion was successful, otherwise false
     */
    public boolean insertUser(String mail, String salt, String authenticationHash, byte[] key, String encryptedTotpSecret){
        String query = "insert into panda_pass.users(username, salt, auth_hash, encrypted_vault, totp_secret_encrypted) values (?, ?, ?, ?, ?)";

        int rowsAffected;
        try(
                Connection conn = DB.connect();
                PreparedStatement stmt = conn.prepareStatement(query)
        ){
            stmt.setString(1, mail);
            stmt.setString(2, salt);
            stmt.setString(3, authenticationHash);

            String encryptedFreshVault = new Vault(mail, salt).encrypt(key);
            stmt.setString(4, encryptedFreshVault);

            stmt.setString(5, encryptedTotpSecret);

            rowsAffected = stmt.executeUpdate();

            if(rowsAffected == 0){
                System.err.println("Error inserting user: No user was added.");

            } else if(rowsAffected > 1){
                System.err.println("Error inserting user: More than one user was added.");

            } else {
                System.out.println("User was successful added.");
            }

            return rowsAffected == 1;
        } catch (Exception e) {
            System.err.println("Error while inserting new user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves the authentication hash for a given user.
     * @param mail User's mail
     * @return Authentication hash, or null if not found
     */
    public String findAuthHashByUserMail(String mail){
        String query = "select auth_hash from panda_pass.users where username = ?";

        ResultSet result;
        try(
                Connection connection = DB.connect();
                PreparedStatement stmt = connection.prepareStatement(query)
        ){
            stmt.setString(1, mail);
            result = stmt.executeQuery();

            if(result.next()){
                return result.getString("auth_hash");
            }
        } catch (Exception e){
            System.err.println("Error while querying for auth hash: " + e.getMessage());
        }

        return null;
    }

    /**
     * Retrieves the salt for a given user.
     * @param mail User's mail
     * @return The salt as String, or null if not found.
     */
    public String findSaltByUserMail(String mail){
        String query = "select salt from panda_pass.users where username = ?";

        ResultSet result;
        try(
                Connection connection = DB.connect();
                PreparedStatement stmt = connection.prepareStatement(query)
        ){
            stmt.setString(1, mail);
            result = stmt.executeQuery();

            if(result.next()){
                return result.getString("salt");
            }
        } catch (Exception e){
            System.err.println("Error while querying for salt: " + e.getMessage());
        }

        return null;
    }

    /**
     * Retrieves the totp secret for a given user.
     * @param mail User's mail
     * @return Totp secret, or null if not found
     */
    public String findTotpSecretByUserMail(String mail){
        String query = "select totp_secret_encrypted from panda_pass.users where username = ?";

        ResultSet result;
        try(
                Connection connection = DB.connect();
                PreparedStatement stmt = connection.prepareStatement(query)
        ){
            stmt.setString(1, mail);
            result = stmt.executeQuery();

            if(result.next()){
                return result.getString("totp_secret_encrypted");
            }
        } catch (Exception e){
            System.err.println("Error while querying for totp secret: " + e.getMessage());
        }

        return null;
    }

    /**
     * Retrieves the encrypted vault for a given user.
     * @param email User's mail
     * @return Encrypted vault string or null if not found
     */
    public String findVaultByUserMail(String email) {
        String query = "select encrypted_vault from panda_pass.users where username = ?";

        try(
                Connection conn = DB.connect();
                PreparedStatement stmt = conn.prepareStatement(query)
        ){
            stmt.setString(1, email);
            ResultSet result = stmt.executeQuery();

            if(result.next()){
                return result.getString("encrypted_vault");
            }
        } catch (Exception e) {
            System.err.println("Error while querying vault: " + e.getMessage());
        }
        return null;
    }

    /**
     * Update the vault of a given user.
     * @param mail User's mail
     * @param encryptedVault New data for encrypted vault
     * @return True if update was successful, else false
     */
    public boolean saveVaultForUser(String mail, String encryptedVault){
        String query = "update panda_pass.users set encrypted_vault = ? where username = ?";

        try(
                Connection connection = DB.connect();
                PreparedStatement stmt = connection.prepareStatement(query)
        ){
            stmt.setString(1, encryptedVault);
            stmt.setString(2, mail);

            int rowsAffected = stmt.executeUpdate();

            if(rowsAffected == 0){
                System.err.println("Updating vault: update did not affect any row.");
            } else if(rowsAffected > 1){
                System.err.println("Updating vault: more than one row was affected.");
            } else {
                System.out.println("Updating row was successful.");
            }

            return rowsAffected == 1;
        } catch (Exception e) {
            System.err.println("Error while updating vault: " + e.getMessage());
            return false;
        }
    }

    /**
     * Searches for user by mail.
     * @param mail User's mail
     * @return True if row with mail was found, otherwise false
     */
    public boolean findUserByMail(String mail){
        String query = "SELECT username FROM panda_pass.users WHERE username = ?";

        try(
                Connection connection = DB.connect();
                PreparedStatement stmt = connection.prepareStatement(query)
        ){
            stmt.setString(1, mail);
            ResultSet result = stmt.executeQuery();

            return result.next();
        } catch (Exception e) {
            System.err.println("Error while finding user: " + e.getMessage());
            return false;
        }
    }


}
