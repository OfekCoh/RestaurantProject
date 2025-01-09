package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;
import java.io.Console;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App 
{
	private static SimpleServer server;

   // methods:

    public static void main( String[] args ) throws IOException
    {
        String databasePassword= getDatabasePassword();
        server = new SimpleServer(3000,databasePassword);
        System.out.println("Server is listening");
        server.listen();
    }

    public static String getDatabasePassword() {
        Console console = System.console(); // if console doesnt work we can use normal scanner.
        if (console == null) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter database password: ");
            return scanner.nextLine();
        }

        char[] passwordArray = console.readPassword("Enter the database password: ");
        return new String(passwordArray);
    }
}
