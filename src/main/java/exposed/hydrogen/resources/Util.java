package exposed.hydrogen.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    public static String getPublicIP() {
        // https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * This code is completely generated by github copilot. I have no idea how it works. It just does.
     * @param file file to get hash from
     * @return hash of the file in string form
     * @throws IOException if file is not found
     * @throws NoSuchAlgorithmException if algorithm is not found (should never happen)
     */
    public static String getSHA1Hash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(Files.readAllBytes(file.toPath()));
        return bytesToHex(md.digest());
    }
    /**
     * This code is completely generated by github copilot. I have no idea how it works. It just does.
     * @param file file to get hash from
     * @return hash of the file in byte array form
     * @throws IOException if file is not found
     * @throws NoSuchAlgorithmException if algorithm is not found (should never happen)
     */
    public static byte[] getSHA1HashBytes(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(Files.readAllBytes(file.toPath()));
        return md.digest();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}