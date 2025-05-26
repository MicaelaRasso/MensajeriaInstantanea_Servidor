package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    public static String pHost;
    public static int pPort;
    public static String sHost;
    public static int sPort;
    

    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            pHost = props.getProperty("proxy.host");
            pPort = Integer.parseInt(props.getProperty("proxy.port"));
            sHost = props.getProperty("server.host");
            sPort = Integer.parseInt(props.getProperty("server.port"));
            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

}