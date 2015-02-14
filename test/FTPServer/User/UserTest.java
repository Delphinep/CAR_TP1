package FTPServer.User;

import static org.junit.Assert.*;

import org.junit.Test;

public class UserTest {

    @Test
    public void test() {
        User user1 = new User("Grutier", "magruemalife");
        //Nom correspondant
        assertTrue("Grutier".equals(user1.getUsername()));
        //Mot de passe correspondant
        assertTrue("magruemalife".equals(user1.getPasswd()));
        //Format du toString() correspondant
        assertTrue("User: Grutier".equals(user1.toString()));
        //Méthode de vérification de l'égalité entre deux User correspondant
        
        User user2 = new User("Grutier", "magruemalife");
        assertTrue(user1.equals(user2));
    }

}
