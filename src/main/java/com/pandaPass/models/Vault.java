package com.pandaPass.models;

import com.google.gson.Gson;
import com.pandaPass.services.ServiceLocator;
import com.pandaPass.services.SessionManager;
import com.pandaPass.utils.EncodingUtil;
import com.pandaPass.utils.EncryptionUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Represents a password vault containing credential entries for a user.
 * Each vault is associated with the user's mail and a user-specific salt.
 */
public class Vault {
    private final List<Entry> entries;
    private final String mail;
    private final String salt;

    public Vault(Entry[] entries, String mail, String salt){
        this.entries = Arrays.asList(entries);
        this.mail = mail;
        this.salt = salt;
    }

    public Vault(String mail, String salt){
        this.entries = new ArrayList<>();
        this.mail = mail;
        this.salt = salt;
    }

    /**
     * Searches for an entry by its service name.
     * @param name Service name to search for.
     * @return Optional containing the entry if found, otherwise empty.
     */
    public Optional<Entry> getEntryByServiceName(String name) {
        return entries
                .stream()
                .filter(entry -> entry.getService().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Creates a new entry and adds it to the vault, if no entry with that service-name exists
     * @param service Name of service
     * @param username Username
     * @param password Plaintext password for the entry
     * @return True if new entry was added, false if duplicate exists
     */
    public Entry add(String service, String username, String password, Category category){
        byte[] key = SessionManager.getVaultSession().getKey();

        if(getEntryByServiceName(service).isPresent()){
            System.out.println("Entry with service name \"" + service + "\" already exists.");
            return null;
        }

        Entry entryToAdd;
        try {
            byte[] entryKey = EncryptionUtil.generateRandomKey();

            String encryptedPassword = EncryptionUtil.encrypt(entryKey, password);
            String encryptedEntryKey = EncryptionUtil.encrypt(key, EncodingUtil.encodeByteArrayToBase64(entryKey));
            entryToAdd = new Entry(service, username, category, encryptedPassword, encryptedEntryKey);
            entryToAdd.initializeRuntimeFields();
            entries.add(entryToAdd);
            return entryToAdd;
        } catch (Exception e){
            System.out.println("Error initialising key/encryption for entry.");
            return null;
        }
    }

    /**
     * Deletes entry by its service-name.
     * @param name Service name of the entry to delete.
     * @return True if the entry was deleted, false if not found.
     */
    public boolean deleteEntryByName(String name){
        Optional<Entry> potentialEntry = getEntryByServiceName(name);

        AtomicBoolean result =  new AtomicBoolean(false);
        potentialEntry.ifPresent(
                entry -> result.set(entries.remove(entry))
        );

        return result.get();
    }

    public String getMail(){
        return mail;
    }

    public byte[] getSalt(){
        return salt.getBytes();
    }

    @Override
    public String toString(){
        if(entries.isEmpty()){
            return "Vault for \"" + mail + "\": Empty";
        }

        String entriesDescriptions = entries
                .stream()
                .map(Entry::toString)
                .collect(Collectors.joining("\n"));

        return "Vault for \"" + mail + "\": " + entriesDescriptions;
    }

    /**
     * Serializes the vault to json.
     * @return JSON string representation
     */
    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Deserializes a vault object from a JSON string.
     * @param vaultAsJson The JSON string
     * @return Deserialized vault object
     */
    public static Vault fromJson(String vaultAsJson){
        Gson gson = new Gson();
        Vault vault = gson.fromJson(vaultAsJson, Vault.class);
        vault.getEntries().forEach(Entry::initializeRuntimeFields);
        return vault;
    }

    /**
     * Encrypts the serialized JSON vault with the given key.
     * @param key Encryption key.
     * @return Encrypted vault string.
     * @throws Exception Exception if encryption fails.
     */
    public String encrypt(byte[] key) throws Exception{
        String vaultAsJson = toJson();
        return EncryptionUtil.encrypt(key, vaultAsJson);
    }

    public List<Entry> getEntries(){
        return entries;
    }

    public boolean updateEntry(Entry entry, String service, String username, String plainTextPassword, Category category){
        boolean passwordUpdatedSuccessfully = entry.setPassword(plainTextPassword);

        if(passwordUpdatedSuccessfully){
            entry.setService(service);
            entry.setUsername(username);
            entry.setCategoryId(category.getId());
        }
        return passwordUpdatedSuccessfully;
    }
}
