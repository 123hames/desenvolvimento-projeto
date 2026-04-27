import java.io.*;
import java.nio.file.*;

public class Sessao {
    private static final String FILE_PATH = "sessao.txt";

    public static void salvarSessao(String email) {
        try {
            Files.write(Paths.get(FILE_PATH), email.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String recuperarSessao() {
        try {
            if (Files.exists(Paths.get(FILE_PATH))) {
                return new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void encerrarSessao() {
        try {
            Files.deleteIfExists(Paths.get(FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}