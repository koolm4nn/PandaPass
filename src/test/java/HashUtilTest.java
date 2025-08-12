public class HashUtilTest {
    /*
    byte[] salt = HashUtil.generateSalt();
    String genericPassword1 = "abcde123";
    String genericPassword2 = "312edcba";
    String longPassword = "a".repeat(10000);
    String shortPassword = "a";
    String unicodePassword = "密碼🔑🔥";
    String specialCharacterPassword = "^°!\"§³$%&/{([)]=}?\\´`+*~#'-_.:,;<>|";

    byte[] genericKey1;
    byte[] genericKey2;

    @Before
    public void setSalt() throws Exception{
        salt = HashUtil.generateSalt();
        genericKey1 = HashUtil.deriveKey(genericPassword1, salt);
        genericKey2 = HashUtil.deriveKey(genericPassword2, salt);
    }

    @Disabled
    @Test
    public void equalPasswordsEqualHashes() throws Exception{
        assertEquals(HashUtil.hashHMAC(genericKey1, salt), HashUtil.hashHMAC(genericKey2, salt));
    }

    @Test
    public void differentPasswordsDifferentHashes() throws Exception{
        assertNotEquals(HashUtil.hashHMAC(genericKey1, salt), HashUtil.hashHMAC(genericKey2, salt));
    }

    @Test
    public void differentSaltDifferentHashes() throws Exception{
        byte[] salt2 = HashUtil.generateSalt();
        byte[] key = HashUtil.deriveKey(genericPassword1, salt2);

        assertNotEquals(HashUtil.hashHMAC(genericKey1, salt), HashUtil.hashHMAC(key, salt2));
    }

    @Test
    public void longPasswordIsHashed() throws Exception{
        String password = "a".repeat(10000);
        assertNotNull(HashUtil.hashHMAC(password, salt));
    }

    @Test
    public void shortPasswordIsHashed() throws Exception{
        String password = "a";
        assertNotNull(HashUtil.hashHMAC(password, salt));
    }

    @Test
    public void unicodesAreHashed() throws Exception{
        String password = "密碼🔑🔥";
        assertNotNull(HashUtil.hashHMAC(password, salt));
    }

    @Test
    public void specialCharactersAreHashed() throws Exception{
        String password = "^°!\"§³$%&/{([)]=}?\\´`+*~#'-_.:,;<>|";
        assertNotNull(HashUtil.hashHMAC(password, salt));
    }

    @Test
    public void emptyPasswordThrowsException() throws Exception{
        assertThrows(Exception.class, () -> HashUtil.hashHMAC("", salt));
    }

    @Test
    public void nullPasswordThrowsException() throws Exception{
        assertThrows(Exception.class, () -> HashUtil.hashHMAC(null, salt));
    }

    @Test
    public void emptySaltThrowsException() throws Exception{
        assertThrows(Exception.class, () -> HashUtil.hashHMAC("password", new byte[0]));
    }*/
}