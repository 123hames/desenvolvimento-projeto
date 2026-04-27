import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import com.google.firebase.database.*;

public class Dashboard extends JFrame {

    private String userEmail;
    private JLabel lblVoltagem, lblCorrente, lblPotencia;

    public Dashboard(String email) {
        this.userEmail = email;
        
        // Configurações da Janela
        setTitle("Sistema Solar IoT - Painel de Controlo");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. ADICIONAR A NAVBAR (Barra Lateral)
        add(new SideNavbar(userEmail), BorderLayout.WEST);

        // 2. PAINEL DE CONTEÚDO PRINCIPAL
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(249, 250, 251)); // bg-gray-50
        mainContent.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Cabeçalho
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        JLabel title = new JLabel("Bem-vindo ao seu Painel Solar");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        JLabel subtitle = new JLabel("Monitorização de energia em tempo real");
        subtitle.setForeground(Color.GRAY);
        header.add(title);
        header.add(subtitle);
        mainContent.add(header, BorderLayout.NORTH);

        // Grid de Cards (Dados IOT)
        JPanel gridCards = new JPanel(new GridLayout(1, 3, 25, 0));
        gridCards.setOpaque(false);
        gridCards.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        double volts = 12, Amps = 2;
        double pot = volts * Amps;
        
        lblVoltagem = new JLabel(volts+"V", SwingConstants.CENTER);
        lblCorrente = new JLabel(Amps+"A", SwingConstants.CENTER);
        lblPotencia = new JLabel(pot+"W", SwingConstants.CENTER);

        gridCards.add(criarCard("Tensão (Voltagem)", lblVoltagem, new Color(59, 130, 246)));
        gridCards.add(criarCard("Intensidade (Corrente)", lblCorrente, new Color(16, 185, 129)));
        gridCards.add(criarCard("Potência Total", lblPotencia, new Color(245, 158, 11)));

        mainContent.add(gridCards, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // 3. ATIVAR FIREBASE
        escutarDadosFirebase();
    }

    // --- MÉTODOS AUXILIARES ---

    private JPanel criarCard(String titulo, JLabel valor, Color cor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(5, 0, 0, 0, cor),
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1)
        ));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setForeground(Color.DARK_GRAY);
        t.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        valor.setFont(new Font("Monospaced", Font.BOLD, 38));
        valor.setForeground(new Color(40, 40, 40));

        card.add(t, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);
        return card;
    }

    private void escutarDadosFirebase() {
        // Altera para o nó exato onde o teu ESP32 envia os dados
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sensores");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Verificação de segurança para evitar erros de cast
                    Object v = snapshot.child("voltagem").getValue();
                    Object c = snapshot.child("corrente").getValue();
                    Object p = snapshot.child("potencia").getValue();

                    SwingUtilities.invokeLater(() -> {
                        if (v != null) lblVoltagem.setText(v.toString() + "V");
                        if (c != null) lblCorrente.setText(c.toString() + "A");
                        if (p != null) lblPotencia.setText(p.toString() + "W");
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // --- CLASSE INTERNA: NAVBAR (O teu código React convertido) ---
    private class SideNavbar extends JPanel {
        public SideNavbar(String email) {
            setPreferredSize(new Dimension(280, 0));
            setBackground(Color.WHITE);
            setLayout(new BorderLayout());
            setBorder(new MatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

         // 1. TÍTULO (Ajustamos o padding para 25px)
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

            JLabel lblTitulo = new JLabel("Navegação");
            lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblTitulo.setForeground(new Color(31, 41, 55));
            headerPanel.add(lblTitulo);
            add(headerPanel, BorderLayout.NORTH);

            // 2. MENU DE NAVEGAÇÃO
            JPanel menu = new JPanel();
            menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
            menu.setOpaque(false);

            // O SEGREDO: Colocamos 25px aqui para alinhar com o FlowLayout acima
            menu.setBorder(BorderFactory.createEmptyBorder(20, 25, 0, 0)); 

            String[] itens = {"Início", "Dispositivos", "Estatísticas", "Configurações"};
            for (String item : itens) {
                JButton b = new JButton(item);
                
                // Removemos o padding interno do botão para o texto encostar na margem dos 25px
                b.setMargin(new Insets(0, 0, 0, 0));
                b.setHorizontalAlignment(SwingConstants.LEFT);
                b.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                // Definimos uma largura máxima mas permitimos que ele alinhe à esquerda
                b.setMaximumSize(new Dimension(250, 45));
                
                b.setFont(new Font("SansSerif", Font.PLAIN, 15));
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                b.setFocusPainted(false);
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                b.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        b.setContentAreaFilled(true); // Ativa o fundo
                        b.setBackground(new Color(243, 244, 246)); // Cor cinza clarinho (Tailwind gray-100)
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        b.setContentAreaFilled(false); // Volta a ficar transparente
                    }
                });
                
                b.addActionListener(e -> {
                    System.out.println("[MENU] Clicou em: " + item);
                    
                    
                });
                
                menu.add(b);
                menu.add(Box.createVerticalStrut(10));
            }
            add(menu, BorderLayout.CENTER);

         // --- 3. RODAPÉ DA NAVBAR (Logout + Texto do Utilizador) ---
            JPanel footerPanel = new JPanel();
            footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
            footerPanel.setBackground(new Color(252, 252, 252));
            footerPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

            // --- Botão Logout ---
            JButton btnLogout = new JButton("Sair / Eliminar Login");
            btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnLogout.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnLogout.setForeground(new Color(220, 38, 38));
            btnLogout.setContentAreaFilled(false);
            btnLogout.setBorderPainted(false);
            btnLogout.setFocusPainted(false);
            btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Margem de 25px à esquerda para alinhar com o menu
            btnLogout.setMargin(new Insets(10, 25, 0, 0)); 

            btnLogout.addActionListener(e -> {
                Sessao.encerrarSessao();
                JOptionPane.showMessageDialog(null, "Sessão encerrada com sucesso.");
                new Login().setVisible(true);
                Dashboard.this.dispose();
            });

         // --- Info da Conta (Clicável) ---
            JPanel contaPanel = new JPanel();
            contaPanel.setLayout(new BoxLayout(contaPanel, BoxLayout.Y_AXIS));
            contaPanel.setOpaque(true);
            contaPanel.setBackground(new Color(252, 252, 252));
            contaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contaPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 20, 25));

            // ESTA LINHA É A CHAVE: Faz o rato ficar clicável ao passar por cima
            contaPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblUsuario = new JLabel("Utilizador");
            lblUsuario.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblUsuario.setForeground(new Color(31, 41, 55));

            JLabel lblEmail = new JLabel(userEmail);
            lblEmail.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblEmail.setForeground(Color.GRAY);

            contaPanel.add(lblUsuario);
            contaPanel.add(lblEmail);

            // Adicionamos o efeito de cor (hover) para acompanhar o cursor
            contaPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    // Muda a cor de fundo para indicar que está selecionado
                    //contaPanel.setBackground(new Color(243, 244, 246)); 
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    // Volta à cor original
                    contaPanel.setBackground(new Color(252, 252, 252));
                }
                
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    // Aqui colocas o que acontece ao clicar
                    System.out.println("Clicou no perfil!");
                }
            });

            // Adicionar componentes ao rodapé
            footerPanel.add(btnLogout);
            footerPanel.add(contaPanel);

            add(footerPanel, BorderLayout.SOUTH);
        }
    }
}