import com.pandaPass.persistence.DB;
import com.pandaPass.services.ServiceLocator;

/**
 * Entry point for the PandaPass Password Manager application.
 * <p>
 * PandaPass is a simple, secure password manager that:
 * <ul>
 *     <li>Stores passwords in an encrypted JSON file using AES-256.</li>
 *     <li>Derives encryption keys from a master password using PBKDF2 with HMAC-SHA256.</li>
 *     <li>Allows users to add, retrieve, and list stored credentials.</li>
 * </ul>
 *
 * <h3>Future Enhancements:</h3>
 * <ul>
 *     <li>Browser autofill support via native messaging (e.g., for Firefox)</li>
 *     <li>Secure password generation</li>
 *     <li>GUI interface (current implementation is CLI-based)</li>
 * </ul>
 *
 * <h3>Technologies:</h3>
 * <ul>
 *     <li>AES-256 encryption</li>
 *     <li>PBKDF2 key derivation</li>
 *     <li>Java Cryptography Architecture (JCA)</li>
 *     <li>Gson for JSON serialization</li>
 *     <li>CLI-based interface</li>
 * </ul>
 */
public class PandaPass {
    private PandaPass(){}

    /**
     * App entry point
     * @param args Command Line arguments
     */
    public static void main(String[] args){
        try{
            DB.connect();
            ServiceLocator.init();

            //UserDialogueView.greet();
            //UserDialogueView.showMainMenu();
        } catch (Exception e){
            System.err.println("An error occured while starting up PandaPass: " + e.getMessage());
        }
    }
}
