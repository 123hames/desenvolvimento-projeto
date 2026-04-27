import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

public class funcoesFirebase {
	
	public interface RespostaVerificacao {
	    void onResultado(boolean existe);
	}
	
	public static class Utilizador {
	    public String id;       // Pode ser um UUID ou o MAC do ESP32
	    public String email;
	    public String password; // Hash da password

	    public Utilizador() { } // Necessário para o Firebase

	    public Utilizador(String id, String email, String password) {
	        this.id = id;
	        this.email = email;
	        this.password = password;
	    }
	}

	public static void conectar() {
        try {
            // 1. Carregar o ficheiro de credenciais que baixaste da consola
            FileInputStream serviceAccount = new FileInputStream("keyFirebase.json");

            // 2. Configurar as opções da ligação
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                // ATENÇÃO: Substitui pelo URL que aparece no teu Realtime Database
                .setDatabaseUrl("https://iot-solar-68422-default-rtdb.europe-west1.firebasedatabase.app/")
                .build();

            // 3. Inicializar a App
            if (FirebaseApp.getApps().isEmpty()) { // Evita inicializar duplicado
                FirebaseApp.initializeApp(options);
            }
            //criarNovoUtilizador("ruben@gmail.com", "1234");
            System.out.println("Conexão ao Firebase IOT-solar estabelecida!");
           

        } catch (IOException e) {
            System.err.println("Erro ao carregar ficheiro JSON: " + e.getMessage());
        }
    }
	
	public static void criarNovoUtilizador(String email, String pass) {
	    // 1. Referência para o nó de utilizadores
	    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("utilizadores");

	    // 2. Criar uma nova chave única (ID automático)
	    DatabaseReference novoNo = ref.push();
	    String idAutomatico = novoNo.getKey(); // Se quiseres saber qual foi o ID gerado

	    String  passHASH = BCrypt.hashpw(pass, BCrypt.gensalt());
	    // 3. Criar o objeto com esse ID
	    Utilizador user = new Utilizador(idAutomatico, email, passHASH);

	    // 4. Gravar os dados nesse novo nó gerado
	    novoNo.setValueAsync(user);

	    System.out.println("Utilizador inserido com ID automático: " + idAutomatico);
	}
	
	public static void verificarSeEmailExiste(String emailProcurado, RespostaVerificacao callback) {
	    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("utilizadores");
	    
	    // Procura pelo campo email
	    Query consulta = ref.orderByChild("email").equalTo(emailProcurado);

	    consulta.addListenerForSingleValueEvent(new ValueEventListener() {
	        @Override
	        public void onDataChange(DataSnapshot dataSnapshot) {
	            // Se o snapshot tiver filhos, significa que encontrou o email
	            callback.onResultado(dataSnapshot.exists());
	        }

	        @Override
	        public void onCancelled(DatabaseError databaseError) {
	            System.err.println("Erro na consulta: " + databaseError.getMessage());
	        }
	    });
	}
	
	public static void autenticarUtilizador(String emailProcurado, String passwordDigitada, RespostaVerificacao callback) {
	    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("utilizadores");
	    
	    // Procura o utilizador pelo e-mail
	    Query consulta = ref.orderByChild("email").equalTo(emailProcurado);

	    consulta.addListenerForSingleValueEvent(new ValueEventListener() {
	        @Override
	        public void onDataChange(DataSnapshot dataSnapshot) {
	            if (dataSnapshot.exists()) {
	                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
	                    String passwordHash = userSnapshot.child("password").getValue(String.class);

	                    // Verifica a password com BCrypt
	                    if (BCrypt.checkpw(passwordDigitada, passwordHash)) {
	                        callback.onResultado(true); // Sucesso total
	                    } else {
	                        callback.onResultado(false); // Pass errada
	                    }
	                    return;
	                }
	            } else {
	                callback.onResultado(false); // Email não existe
	            }
	        }

	        @Override
	        public void onCancelled(DatabaseError databaseError) {
	            callback.onResultado(false);
	        }
	    });
	}
	
	public static void monitorarMudancasNoUtilizador(String idDoUtilizador) {
	    DatabaseReference ref = FirebaseDatabase.getInstance()
	        .getReference("utilizadores")
	        .child(idDoUtilizador);

	    // Este método fica ativo para sempre enquanto o programa correr
	    ref.addValueEventListener(new ValueEventListener() {
	        @Override
	        public void onDataChange(DataSnapshot dataSnapshot) {
	            if (dataSnapshot.exists()) {
	                // ISTO CORRE SEMPRE QUE ALGUÉM MUDAR ALGO NO FIREBASE
	                Utilizador user = dataSnapshot.getValue(Utilizador.class);
	                System.out.println("[ALERTA] Os dados do utilizador " + user.email + " foram alterados!");
	                
	                // Exemplo: Se mudares a pass no site do Firebase, o Java deteta aqui na hora
	            }
	        }

	        @Override
	        public void onCancelled(DatabaseError databaseError) {
	            System.err.println("Erro ao monitorar: " + databaseError.getMessage());
	        }
	    });
	}
}
