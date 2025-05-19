package librarymanagementsystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
public final class LibraryManagementSystem extends JFrame {
    private final BookManager bookManager;
    private final AuthService authService;
    private final JPanel cards;
        private final String L = "Login";
    final String M = "Main Menu";
    final String A = "Add Book";
    final String S = "Search Book";
    final String D = "Delete Book";
    
    public LibraryManagementSystem() {
        super("Library Management System");
        bookManager = new BookManager();
        authService = new AuthService();
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        cards = new JPanel(new CardLayout());
        setupPanels();
        add(cards);
        showLoginScreen();
    }
    
    private void setupPanels() {
        cards.add(new LoginPanel(this, authService).getPanel(), L);
        cards.add(new MainMenuPanel(this).getPanel(), M);
        cards.add(new AddBookPanel(this, bookManager).getPanel(), A);
        cards.add(new SearchBookPanel(this, bookManager).getPanel(), S);
        cards.add(new DeleteBookPanel(this, bookManager).getPanel(), D);
    }
    
    public void showScreen(String screenName) {
        CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, screenName);
    }
    
    void showLoginScreen() {
        showScreen(L);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryManagementSystem().setVisible(true);
        });
    }
}

class BookManager {
    private final ArrayList<Book> books;
    
    public BookManager() {
        books = new ArrayList<>();
    }
    
   
    public void addBook(Book book) throws InvalidBookException {
        if (book.getId().isEmpty() || book.getTitle().isEmpty()) {
            throw new InvalidBookException("Book ID and Title are required!");
        }
        books.add(book);
    }
    
    public ArrayList<Book> searchBooks(String query) {
        ArrayList<Book> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (Book book : books) {
            if (book.getId().toLowerCase().contains(lowerQuery) || 
                book.getTitle().toLowerCase().contains(lowerQuery) || 
                book.getAuthor().toLowerCase().contains(lowerQuery)) {
                results.add(book);
            }
        }
        return results;
    }
    
    public boolean deleteBook(String id) {
        return books.removeIf(book -> book.getId().equals(id));
    }
    
    public ArrayList<Book> getAllBooks() {
        return new ArrayList<>(books);
    }
}
class AuthService {
    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "1234";
    
    public boolean authenticate(String username, String password) {
        return username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD);
    }
}
class Book {
    private final String id;
    private final String title;
    private final String author;
    
    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }
        public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    
    @Override
    public String toString() {
        return String.format("ID: %s\nTitle: %s\nAuthor: %s\n", id, title, author);
    }
}

class InvalidBookException extends Exception {
    public InvalidBookException(String message) {
        super(message);
    }
}

class LoginPanel {
    private final JPanel panel;
    
    public LoginPanel(LibraryManagementSystem system, AuthService authService) {
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("System Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(titleLabel, gbc);
        
   
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(userLabel, gbc);
        
        JTextField userField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(userField, gbc);
        
        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passLabel, gbc);
        
        JPasswordField passField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passField, gbc);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener((ActionEvent e) -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            
            if (authService.authenticate(username, password)) {
                system.showScreen(system.M);
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        panel.add(loginBtn, gbc);
    }
    
    public JPanel getPanel() { return panel; }
}

class MainMenuPanel {
    private final JPanel panel;
    
    public MainMenuPanel(LibraryManagementSystem system) {
        panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        // Buttons
        JButton addBookBtn = new JButton("Add New Book");
        addBookBtn.addActionListener(e -> system.showScreen(system.A));
        
        JButton searchBookBtn = new JButton("Search Book");
        searchBookBtn.addActionListener(e -> system.showScreen(system.S));
        
        JButton deleteBookBtn = new JButton("Delete Book");
        deleteBookBtn.addActionListener(e -> system.showScreen(system.D));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> system.showLoginScreen());
                panel.add(addBookBtn);
        panel.add(searchBookBtn);
        panel.add(deleteBookBtn);
        panel.add(logoutBtn);
    }
    
    public JPanel getPanel() { return panel; }
}

class AddBookPanel {
    private final JPanel panel;
    private final JTextField idField, nameField, authorField;
    
    public AddBookPanel(LibraryManagementSystem system, BookManager bookManager) {
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Add New Book");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(titleLabel, gbc);
        
        JLabel idLabel = new JLabel("Book ID:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(idLabel, gbc);
        
        idField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(idField, gbc);
        
        JLabel nameLabel = new JLabel("Title:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(nameLabel, gbc);
        
        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(nameField, gbc);
        
        JLabel authorLabel = new JLabel("Author:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(authorLabel, gbc);
        
        authorField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(authorField, gbc);
        
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            try {
                Book book = new Book(idField.getText(), nameField.getText(), authorField.getText());
                bookManager.addBook(book);
                JOptionPane.showMessageDialog(panel, "Book added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } catch (InvalidBookException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        panel.add(saveBtn, gbc);
        
        // Back Button
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            system.showScreen(system.M);
            clearFields();
        });
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(backBtn, gbc);
    }
    
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        authorField.setText("");
    }
    
    public JPanel getPanel() { return panel; }
}

class SearchBookPanel {
    private final JPanel panel;
    
    public SearchBookPanel(LibraryManagementSystem system, BookManager bookManager) {
        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel searchPanel = new JPanel(new FlowLayout());
        JLabel searchLabel = new JLabel("Search Book:");
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            resultArea.setText("");
            
            if (searchText.isEmpty()) {
                resultArea.append("All Books:\n\n");
                for (Book book : bookManager.getAllBooks()) {
                    resultArea.append(book.toString() + "\n");
                }
                return;
            }
            
            ArrayList<Book> results = bookManager.searchBooks(searchText);
            if (results.isEmpty()) {
                resultArea.setText("No matching books found!");
            } else {
                for (Book book : results) {
                    resultArea.append(book.toString() + "\n");
                }
            }
        });
        
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            system.showScreen(system.M);
            searchField.setText("");
            resultArea.setText("");
        });
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(backBtn, BorderLayout.SOUTH);
    }
    
    public JPanel getPanel() { return panel; }
}

class DeleteBookPanel {
    private final JPanel panel;
    
    public DeleteBookPanel(LibraryManagementSystem system, BookManager bookManager) {
        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel idLabel = new JLabel("Enter Book ID to delete:");
        JTextField idField = new JTextField(15);
        JButton deleteBtn = new JButton("Delete");
        
        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(deleteBtn);
        
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        deleteBtn.addActionListener(e -> {
            String id = idField.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Book ID is required!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (bookManager.deleteBook(id)) {
                resultArea.setText("Book deleted successfully!");
            } else {
                resultArea.setText("No book found with this ID!");
            }
        });
        
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            system.showScreen(system.M);
            idField.setText("");
            resultArea.setText("");
        });
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(backBtn, BorderLayout.SOUTH);
    }
    
    public JPanel getPanel() { return panel; }
}