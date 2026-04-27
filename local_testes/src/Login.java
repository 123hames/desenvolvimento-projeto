import java.awt.*;
import java.util.Random;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.geom.RoundRectangle2D;

// Classe para criar bordas arredondadas nos campos e botões
class RoundedBorder extends AbstractBorder {
    private int radius;
    RoundedBorder(int radius) { this.radius = radius; }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(200, 200, 200));
        g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
        g2d.dispose();
    }
}

public class Login extends JFrame {

    private String processoAtual = "login"; 
    private String emailDestino;
    private String passTemp; // Armazena a pass durante o 2FA para o registo
    private int codigoGerado;
    private JCheckBox chkLembrar;

    public Login() {
        setTitle("Login");
        setSize(450, 420); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE); 

        // --- CAMPO EMAIL ---
        JLabel lblEmail = new JLabel("E-mail");
        lblEmail.setBounds(50, 20, 350, 20);
        lblEmail.setFont(new Font("Arial", Font.BOLD, 13));
        lblEmail.setForeground(new Color(50, 50, 50));
        mainPanel.add(lblEmail);

        JTextField txtEmail = new JTextField("");
        txtEmail.setBounds(50, 45, 350, 45);
        txtEmail.setBorder(new RoundedBorder(15));
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(txtEmail);

        // --- CAMPO PASSWORD ---
        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(50, 110, 350, 20);
        lblPass.setFont(new Font("Arial", Font.BOLD, 13));
        lblPass.setForeground(new Color(50, 50, 50));
        mainPanel.add(lblPass);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBounds(50, 135, 350, 45);
        txtPass.setBorder(new RoundedBorder(15));
        mainPanel.add(txtPass);

        // --- CHECKBOX ---
        chkLembrar = new JCheckBox("<html><body style='padding-left:10px;'>Lembrar-me neste computador</body></html>");
        chkLembrar.setBounds(50, 190, 350, 40);
        chkLembrar.setFont(new Font("Arial", Font.PLAIN, 14));
        chkLembrar.setBackground(Color.WHITE);
        chkLembrar.setFocusPainted(false);
        mainPanel.add(chkLembrar);

        // --- CRIAR CONTA (LINK) ---
        JLabel lblCriarConta = new JLabel("Ainda não tem conta? Criar conta!", SwingConstants.CENTER);
        lblCriarConta.setBounds(50, 250, 350, 20);
        lblCriarConta.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCriarConta.setForeground(new Color(0, 102, 204));
        lblCriarConta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(lblCriarConta);

        // --- BOTÃO PRINCIPAL ---
        JButton btn = new JButton("Login");
        btn.setBounds(50, 280, 350, 50);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBorder(new RoundedBorder(15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(btn);

        add(mainPanel);
        this.getRootPane().setDefaultButton(btn);

        // Lógica de troca entre Login e Registo
        lblCriarConta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if(processoAtual.equals("login")) {
                    processoAtual = "registro";
                    setTitle("Registo");
                    lblCriarConta.setText("Já tenho conta! Entrar");
                    btn.setText("Criar conta");
                    chkLembrar.setVisible(false);
                } else {
                    processoAtual = "login";
                    setTitle("Login");
                    lblCriarConta.setText("Ainda não tem conta? Criar conta!");
                    btn.setText("Login");
                    chkLembrar.setVisible(true);
                }
                mainPanel.repaint();
            }
        });

        // Ação do botão principal
        btn.addActionListener(e -> {
            String email = txtEmail.getText();
            String pass = new String(txtPass.getPassword());
            codigoGerado = new Random().nextInt(9000) + 1000;
            
            if (!isEmailValido(email)) {
                JOptionPane.showMessageDialog(null, "Por favor, insira um endereço de e-mail válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(pass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            emailDestino = email;
            passTemp = pass;

            // TUA MENSAGEM HTML ORIGINAL
            String mensagemHtml = 
                "<div style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 60px 20px; text-align: center;'>" +
                    "<div style='background-color: #ffffff; padding: 50px; display: inline-block; border-radius: 12px; width: 100%; max-width: 500px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);'>" +
                        "<p style='color: #666; font-size: 18px; margin-bottom: 30px;'>O seu código de verificação é:</p>" +
                        "<div style='background-color: #eeeeee; padding: 30px 50px; border-radius: 10px; " +
                        "font-size: 48px; font-weight: 900; color: #222222; letter-spacing: 12px; display: inline-block;'>" + 
                            codigoGerado + 
                        "</div>" +
                        "<p style='color: #999; font-size: 14px; margin-top: 40px; line-height: 1.6;'>" +
                            "Se não solicitou este código, ignore este e-mail.<br>" +
                        "</p>" +
                    "</div>" +
                "</div>";

            if(processoAtual.equals("login")) {
                // Aqui no futuro adicionas a verificação da password no Firebase
            	
            	funcoesFirebase.autenticarUtilizador(email, pass, sucesso -> {
            	    if (sucesso) {
            	        // Se a pass e email estiverem corretos, avança para o 2FA
            	        iniciarProcesso2FA(btn, mensagemHtml);
            	    } else {
            	        // Se falhar (email não existe ou pass errada)
            	        JOptionPane.showMessageDialog(null, "E-mail ou password incorretos!", "Erro de Login", JOptionPane.ERROR_MESSAGE);
            	    }
            	});
                
                
            } else {
                // Registo: Verifica se o email já existe no Firebase
                funcoesFirebase.verificarSeEmailExiste(email, existe -> {
                    if (existe) {
                        JOptionPane.showMessageDialog(null, "Este e-mail já está registado!");
                    } else {
                    	System.out.println("NAo existe");
                        iniciarProcesso2FA(btn, mensagemHtml);
                    }
                });
            }
        });
    }

    private void iniciarProcesso2FA(JButton btnOriginal, String html) {
        new Thread(() -> {
            try {
                btnOriginal.setEnabled(false);
                btnOriginal.setText("A enviar código...");
                
                envioEmail.enviarEmailSmtp(emailDestino, "Código de Autenticação", html);
                
                SwingUtilities.invokeLater(() -> abrirJanela2FA());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro ao enviar e-mail: " + ex.getMessage());
            } finally {
                btnOriginal.setEnabled(true);
                btnOriginal.setText(processoAtual.equals("login") ? "Login" : "Criar conta");
            }
        }).start();
    }

    private void abrirJanela2FA() {
        JFrame f2 = new JFrame("Verificação 2FA");
        f2.setSize(350, 250);
        f2.setLocationRelativeTo(null);
        f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);

        JLabel lbl = new JLabel("Insira o código enviado:");
        lbl.setBounds(50, 20, 250, 20);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(lbl);

        JTextField txtCod = new JTextField();
        txtCod.setBounds(50, 50, 250, 45);
        txtCod.setBorder(new RoundedBorder(15));
        txtCod.setHorizontalAlignment(JTextField.CENTER);
        txtCod.setFont(new Font("Arial", Font.BOLD, 20));
        p.add(txtCod);

        JButton btnOk = new JButton("Confirmar");
        btnOk.setBounds(50, 115, 250, 50);
        btnOk.setBackground(new Color(0, 123, 255));
        btnOk.setForeground(Color.WHITE);
        btnOk.setBorder(new RoundedBorder(15));
        p.add(btnOk);

        f2.add(p);
        f2.getRootPane().setDefaultButton(btnOk);

        btnOk.addActionListener(ev -> {
            if (txtCod.getText().equals(String.valueOf(codigoGerado))) {
                if (processoAtual.equals("registro")) {
                    // SÓ CRIA NO FIREBASE SE O 2FA ESTIVER OK
                    funcoesFirebase.criarNovoUtilizador(emailDestino, passTemp);
                } else if (chkLembrar.isSelected()) {
                    Sessao.salvarSessao(emailDestino);
                }
                
                new Dashboard(emailDestino).setVisible(true);
                f2.dispose();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Código inválido!");
            }
        });

        f2.setVisible(true);
    }

    public static boolean isEmailValido(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" + 
                            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return email != null && pat.matcher(email).matches();
    }
}