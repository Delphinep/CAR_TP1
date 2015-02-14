package FTPServer.Server;

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import util.RootFileCSV;


public class FTPRequestTest {

    @Test
    public void testProcess() {
        FTPRequest req = new FTPRequest(null,new RootFileCSV());
        
        /**
         * Resquest USER / PASS
         */
        //Request USER with a login that exist into the account file -> OKAY
        assertTrue(req.processRequest("USER", "grutier").equals("331 : User name okay, need password.\n"));
        //Request PASS with the existing user and the good password -> OKAY
        assertTrue(req.processRequest("PASS", "magruemalife").equals("200 : Command okay.\n"));
        //Request PASS with the existing user but a wrong password -> FAIL
        assertTrue(req.processRequest("PASS", "nawak").equals("530 : Not logged in.\n"));
        //Request PASS with the existing user but an empty password -> FAIL
        assertTrue(req.processRequest("PASS", "").equals("530 : Not logged in.\n"));
        //Request USER with a login that not exist into the account file -> OKAY
        assertTrue(req.processRequest("USER", "Je n'existe pas").equals("331 : User name okay, need password.\n"));
        //Request PASS with a non existing user -> FAIL
        assertTrue(req.processRequest("PASS","nawak").equals("530 : Not logged in.\n"));
        //Request PASS with a non existing user and an empty password -> FAIL
        assertTrue(req.processRequest("PASS","").equals("530 : Not logged in.\n"));

        /**
         * Request SYST
         */
        assertTrue(req.processRequest("SYST","" ).equals("215 : Unix system.\n"));
        
    }

}
