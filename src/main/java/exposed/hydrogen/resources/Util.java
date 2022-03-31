package exposed.hydrogen.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
}
