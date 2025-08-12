package com.pandaPass.models;

public class PwnedCheckResult {
    private final String passwordIdentifier; // Hash prefix
    private final boolean pwned;
    private final int pwnCount;

    public PwnedCheckResult(String passwordIdentifier, boolean pwned, int pwnCount){
        this.passwordIdentifier = passwordIdentifier;
        this.pwned = pwned;
        this.pwnCount = pwnCount;
    }

    public String getPasswordIdentifier(){
        return passwordIdentifier;
    }

    public boolean isPwned(){
        return pwned;
    }

    public int getPwnCount(){
        return pwnCount;
    }

    @Override
    public String toString(){
        return "Result for \"" + passwordIdentifier + "\": " + (pwned? "is compromised (" + pwnCount + ")." : "is safe.");
    }

}
