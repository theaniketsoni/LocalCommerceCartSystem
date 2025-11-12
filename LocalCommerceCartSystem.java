package project;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;

// -------------------- Base Class --------------------
class User {
    String username;
    String password;
    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

// -------------------- Derived Class (Inheritance) --------------------
class LoginUser extends User {
    LoginUser(String username, String password) {
        super(username, password); // call base class constructor
    }
    boolean login(Map<String,String> users) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}

// -------------------- Product Class --------------------
class Product {
    final String id, name, category;
    final double price;
    Product(String id, String name, String category, double price) {
        this.id = id; this.name = name; this.category = category; this.price = price;
    }
    @Override public String toString() {
        return String.format("[%s] %-22s (%-11s) - ₹%.2f", id, name, category, price);
    }
}

// -------------------- Main Application --------------------
public class LocalCommerceCartSystem extends JFrame {

    private static final java.util.List<Product> PRODUCTS = Arrays.asList(
            new Product("E101","Laptop 15","Electronics",75000),
            new Product("E102","Smartphone A52","Electronics",35000),
            new Product("E103","Headphones","Electronics",2000),
            new Product("G201","Rice (10kg)","Grocery",1500),
            new Product("G202","Milk (1L)","Grocery",60),
            new Product("G203","Sugar (1kg)","Grocery",80)
    );

    private static final Map<String,String> USERS = new HashMap<>();
    static {
        USERS.put("admin","admin123");
        USERS.put("aryan","pass123");
        USERS.put("guest","guest");
    }

    private final Map<String,Integer> cart = new LinkedHashMap<>();
    private final Map<String,Product> byId = new HashMap<>();
    private String currentUser = "";

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private JTextField tfUser;
    private JPasswordField tfPass;

    private DefaultListModel<Product> productModel;
    private JList<Product> productList;
    private JSpinner spAddQty;

    private DefaultListModel<String> cartModel;
    private JList<String> cartList;
    private JSpinner spRemoveQty;
    private JLabel lblTotal;

    private static final NumberFormat INR = NumberFormat.getCurrencyInstance(new Locale("en","IN"));

    public LocalCommerceCartSystem() {
        setTitle("Local Commerce Shopping Cart System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 540);
        setLocationRelativeTo(null);

        for (Product p : PRODUCTS) byId.put(p.id, p);

        root.add(buildLoginPanel(), "login");
        root.add(buildShopPanel(), "shop");
        setContentPane(root);
        cards.show(root, "login");
    }

    // ----------------- LOGIN PANEL -----------------
    private JPanel buildLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,8,8,8); g.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Local Commerce - Login", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        tfUser = new JTextField(18);
        tfPass = new JPasswordField(18);
        JButton btnLogin = new JButton("Login");
        JLabel demo = new JLabel("Demo: admin/admin123, aryan/pass123, guest/guest", SwingConstants.CENTER);

        g.gridx=0; g.gridy=0; g.gridwidth=2; p.add(title,g);
        g.gridwidth=1; g.gridy++; p.add(new JLabel("Username"), g);
        g.gridx=1; p.add(tfUser, g);
        g.gridx=0; g.gridy++; p.add(new JLabel("Password"), g);
        g.gridx=1; p.add(tfPass, g);
        g.gridx=0; g.gridy++; g.gridwidth=2; p.add(btnLogin, g);
        g.gridy++; p.add(demo, g);

        btnLogin.addActionListener(e -> doLogin());
        return p;
    }

    private void doLogin() {
        String u = tfUser.getText().trim();
        String p = new String(tfPass.getPassword()).trim();
        LoginUser user = new LoginUser(u, p); // use inheritance here
        if (user.login(USERS)) {
            currentUser = u;
            cards.show(root, "shop");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials");
        }
    }

    // ----------------- SHOP PANEL -----------------
    private JPanel buildShopPanel() {
        JPanel page = new JPanel(new BorderLayout(8,8));
        JLabel welcome = new JLabel();
        JButton logout = new JButton("Logout");

        JPanel top = new JPanel(new BorderLayout());
        top.add(welcome, BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);
        page.add(top, BorderLayout.NORTH);

        new javax.swing.Timer(100, e -> welcome.setText("Welcome, " + currentUser)).start();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.5);
        page.add(split, BorderLayout.CENTER);

        // ---------- Left: Products ----------
        productModel = new DefaultListModel<>();
        PRODUCTS.forEach(productModel::addElement);
        productList = new JList<>(productModel);
        productList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel left = new JPanel(new BorderLayout(6,6));
        left.add(new JScrollPane(productList), BorderLayout.CENTER);

        JPanel addPanel = new JPanel();
        spAddQty = new JSpinner(new SpinnerNumberModel(1,1,999,1));
        JButton btnAdd = new JButton("Add to Cart");
        addPanel.add(new JLabel("Qty:"));
        addPanel.add(spAddQty);
        addPanel.add(btnAdd);
        left.add(addPanel, BorderLayout.SOUTH);
        split.setLeftComponent(left);

        // ---------- Right: Cart ----------
        cartModel = new DefaultListModel<>();
        cartList = new JList<>(cartModel);
        cartList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel right = new JPanel(new BorderLayout(6,6));
        right.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        spRemoveQty = new JSpinner(new SpinnerNumberModel(1,1,999,1));
        JButton btnRemove = new JButton("Remove Qty");
        JButton btnClear = new JButton("Clear");
        JButton btnCheckout = new JButton("Checkout");
        lblTotal = new JLabel("Total: " + INR.format(0));
        actions.add(new JLabel("Qty:"));
        actions.add(spRemoveQty);
        actions.add(btnRemove);
        actions.add(btnClear);
        actions.add(btnCheckout);
        actions.add(lblTotal);
        right.add(actions, BorderLayout.SOUTH);
        split.setRightComponent(right);

        // ---------- Actions ----------
        btnAdd.addActionListener(e -> {
            Product sel = productList.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this,"Select a product"); return; }
            int q = (int) spAddQty.getValue();
            cart.put(sel.id, cart.getOrDefault(sel.id,0) + q);
            refreshCart();
        });

        btnRemove.addActionListener(e -> {
            int idx = cartList.getSelectedIndex();
            if (idx < 0) return;
            String line = cartModel.get(idx);
            String id = line.substring(1, line.indexOf(']'));
            int q = (int) spRemoveQty.getValue();
            int cur = cart.getOrDefault(id,0);
            if (cur <= q) cart.remove(id); else cart.put(id, cur - q);
            refreshCart();
        });

        btnClear.addActionListener(e -> { cart.clear(); refreshCart(); });

        btnCheckout.addActionListener(e -> {
            if (cart.isEmpty()) { JOptionPane.showMessageDialog(this,"Cart is empty"); return; }
            StringBuilder bill = new StringBuilder("Receipt for " + currentUser + "\n\n");
            double total = 0;
            for (Map.Entry<String,Integer> en : cart.entrySet()) {
                Product p = byId.get(en.getKey());
                int q = en.getValue();
                double line = p.price * q;
                total += line;
                bill.append(String.format("%s x %d = %s\n", p.name, q, INR.format(line)));
            }
            bill.append("\nTotal: ").append(INR.format(total));
            JOptionPane.showMessageDialog(this, bill.toString(), "Bill", JOptionPane.INFORMATION_MESSAGE);
            cart.clear();
            refreshCart();
        });

        logout.addActionListener(e -> { currentUser=""; cart.clear(); cards.show(root,"login"); });
        return page;
    }

    private void refreshCart() {
        cartModel.clear();
        double total = 0;
        for (Map.Entry<String,Integer> en : cart.entrySet()) {
            Product p = byId.get(en.getKey());
            int q = en.getValue();
            double line = p.price * q;
            total += line;
            cartModel.addElement(String.format("[%s] %-22s | Qty: %d | Line: %s",
                    p.id, p.name, q, INR.format(line)));
        }
        lblTotal.setText("Total: " + INR.format(total));
    }

    // ----------------- MAIN -----------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LocalCommerceCartSystem().setVisible(true));
    }
}
