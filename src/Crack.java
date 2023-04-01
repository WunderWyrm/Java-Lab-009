/**
 *
 * @author Samuel Theiss
 *
 * @since Version 1.0
 *
 */

import org.apache.commons.codec.digest.Crypt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class Crack {
    private final User[] users;
    private final String dictionary;

    public Crack(String shadowFile, String dictionary) throws FileNotFoundException {
        this.dictionary = dictionary;
        try {
            this.users = Crack.parseShadow(shadowFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void crack() throws FileNotFoundException {

        BufferedReader reader = new BufferedReader( new FileReader( Path.of(this.dictionary).toFile() ) );

        String tempWord = "";

        while (true) {

            try {
                tempWord = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (tempWord == null) {
                break;
            }

            for (User element : this.users) {

                if (element.getPassHash().contains("$") && element.getPassHash().equals(Crypt.crypt(tempWord, element.getPassHash()))) {

                    System.out.printf("Found password %s for user %s\n", tempWord, element.getUsername());

                }

            }

        }

    }

    public static int getLineCount(String path) {
        int lineCount = 0;
        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8)) {
            lineCount = (int)stream.count();
        } catch(IOException ignored) {}
        return lineCount;
    }

    public static User[] parseShadow(String shadowFile) throws IOException {

        User[] userArray = new User[getLineCount(shadowFile)];
        BufferedReader reader = new BufferedReader( new FileReader( Paths.get(shadowFile).toFile() ) );

        for (int i = 0; i < getLineCount(shadowFile); i++) {
            String[] tempArray = reader.readLine().split(":");

            userArray[i] = new User(tempArray[0], tempArray[1]);
        }

        return userArray;

    }

    public static void main(String[] args) throws FileNotFoundException {

        Scanner sc = new Scanner(System.in);
        System.out.print("Type the path to your shadow file: ");
        String shadowPath = sc.nextLine();
        System.out.print("Type the path to your dictionary file: ");
        String dictPath = sc.nextLine();

        Crack c = new Crack(shadowPath, dictPath);
        c.crack();
    }
}
