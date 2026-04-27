import javax.swing.*;

public class Main extends JFrame {
	

    // O MÉTODO MAIN AGORA DECIDE QUEM ABRE
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	
        	funcoesFirebase.conectar();
            String loginGuardado = Sessao.recuperarSessao();

            //new Dashboard(loginGuardado).setVisible(true);
            
            if (loginGuardado != null) {
                // Existe login! Abre o Dashboard diretamente
                new Dashboard(loginGuardado).setVisible(true);
            } else {
                // Não existe! Chama a classe Login
                new Login().setVisible(true);
            }
        });
    }

    
}