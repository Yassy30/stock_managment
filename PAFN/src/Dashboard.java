import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Dashboard extends JFrame {

    private JTable tableProduits;
    private DefaultTableModel tableModel;

    // Connexion à la base de données
    private Connection connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/gestion_stock"; // Remplace par ton URL
            String user = "root"; // Remplace par ton utilisateur
            String password = ""; // Remplace par ton mot de passe
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

    public Dashboard() {
        setTitle("Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(5, 1));
        JButton btnDashboard = new JButton("Dashboard");
        JButton btnProduits = new JButton("Produits");
        JButton btnRapport = new JButton("Rapport"); 
        JButton btnLogout = new JButton("Logout");
        sidebar.add(btnDashboard);
        sidebar.add(btnProduits);
        sidebar.add(btnRapport);
        sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        // Main panel
        JPanel mainPanel = new JPanel(new CardLayout());
        JLabel lblDashboard = new JLabel("Bienvenue au Dashboard du gestion de stock", SwingConstants.CENTER);
        lblDashboard.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(lblDashboard, "Dashboard");

        // Produits panel
        JPanel produitsPanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Barcode", "Prix", "Quantité"}, 0);
        tableProduits = new JTable(tableModel);
        produitsPanel.add(new JScrollPane(tableProduits), BorderLayout.CENTER);

        // Buttons for Produits
        JPanel boutonsProduits = new JPanel();
        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");
        boutonsProduits.add(btnAjouter);
        boutonsProduits.add(btnModifier);
        boutonsProduits.add(btnSupprimer);
        produitsPanel.add(boutonsProduits, BorderLayout.SOUTH);
        mainPanel.add(produitsPanel, "Produits");

        add(mainPanel, BorderLayout.CENTER);

        // Actions
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        btnDashboard.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        btnProduits.addActionListener(e -> {
            cardLayout.show(mainPanel, "Produits");
            chargerProduits();
        });

        // Logout
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        // Ajouter produit
        btnAjouter.addActionListener(e -> ajouterProduit());

        // Modifier produit
        btnModifier.addActionListener(e -> modifierProduit());

        // Supprimer produit
        btnSupprimer.addActionListener(e -> supprimerProduit());

        setLocationRelativeTo(null);
    }

    private void chargerProduits() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM produits")) {
            tableModel.setRowCount(0); // Clear table
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("barcode"),
                        rs.getDouble("prix"),
                        rs.getInt("quantite")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des produits: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterProduit() {
        JTextField tfNom = new JTextField();
        JTextField tfBarcode = new JTextField();
        JTextField tfPrix = new JTextField();
        JTextField tfQuantite = new JTextField();

        Object[] message = {
                "Nom:", tfNom,
                "Barcode:", tfBarcode,
                "Prix:", tfPrix,
                "Quantité:", tfQuantite
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter Produit", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO produits (nom, barcode, prix, quantite) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, tfNom.getText());
                stmt.setString(2, tfBarcode.getText());
                stmt.setDouble(3, Double.parseDouble(tfPrix.getText()));
                stmt.setInt(4, Integer.parseInt(tfQuantite.getText()));
                stmt.executeUpdate();
                chargerProduits();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du produit: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierProduit() {
        int selectedRow = tableProduits.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à modifier.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        String barcode = (String) tableModel.getValueAt(selectedRow, 2);
        double prix = (double) tableModel.getValueAt(selectedRow, 3);
        int quantite = (int) tableModel.getValueAt(selectedRow, 4);

        JTextField tfNom = new JTextField(nom);
        JTextField tfBarcode = new JTextField(barcode);
        JTextField tfPrix = new JTextField(String.valueOf(prix));
        JTextField tfQuantite = new JTextField(String.valueOf(quantite));

        Object[] message = {
                "Nom:", tfNom,
                "Barcode:", tfBarcode,
                "Prix:", tfPrix,
                "Quantité:", tfQuantite
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Modifier Produit", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE produits SET nom=?, barcode=?, prix=?, quantite=? WHERE id=?")) {
                stmt.setString(1, tfNom.getText());
                stmt.setString(2, tfBarcode.getText());
                stmt.setDouble(3, Double.parseDouble(tfPrix.getText()));
                stmt.setInt(4, Integer.parseInt(tfQuantite.getText()));
                stmt.setInt(5, id);
                stmt.executeUpdate();
                chargerProduits();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification du produit: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerProduit() {
        int selectedRow = tableProduits.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à supprimer.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce produit?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM produits WHERE id=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                chargerProduits();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du produit: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}
