import javax.swing.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Main {
    static String questionFile = "questions.txt";
    static String resultFile = "results.txt";

    public static void main(String[] args) {
        try { new File(questionFile).createNewFile(); } catch(Exception e) {}
        try { new File(resultFile).createNewFile(); } catch(Exception e) {}

        showRoleSelection();
    }

    // Role selection
    static void showRoleSelection() {
        JFrame f = new JFrame("Select Role");
        JButton adminBtn = new JButton("Admin");
        JButton userBtn = new JButton("User");

        adminBtn.setBounds(50,50,200,30);
        userBtn.setBounds(50,100,200,30);

        f.add(adminBtn); f.add(userBtn);
        f.setSize(300,200); f.setLayout(null); f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        adminBtn.addActionListener(e -> {
            f.dispose();
            showLogin();
        });

        userBtn.addActionListener(e -> {
            f.dispose();
            takeQuiz();
        });
    }

    // Admin login
    static void showLogin() {
        JFrame f = new JFrame("Admin Login");
        JLabel l1 = new JLabel("Username"), l2 = new JLabel("Password");
        JTextField tf1 = new JTextField();
        JPasswordField pf = new JPasswordField();
        JButton b = new JButton("Login");

        l1.setBounds(20,20,80,30); tf1.setBounds(100,20,150,30);
        l2.setBounds(20,60,80,30); pf.setBounds(100,60,150,30);
        b.setBounds(100,100,80,30);

        f.add(l1); f.add(tf1); f.add(l2); f.add(pf); f.add(b);
        f.setSize(300,200); f.setLayout(null); f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        b.addActionListener(e -> {
            String username = tf1.getText();
            String password = new String(pf.getPassword());
            if(username.equals("ChandanQuiz") && password.equals("chandan#963852147")){
                f.dispose();
                showAdminPanel();
            } else {
                JOptionPane.showMessageDialog(f,"Invalid Login");
            }
        });
    }

    // Admin panel
    static void showAdminPanel() {
        JFrame f = new JFrame("Admin Panel");
        JButton addQ = new JButton("Add Question");
        JButton viewResults = new JButton("View Results");
        JButton takeQuiz = new JButton("Take Quiz (as user)");

        addQ.setBounds(50,50,200,30);
        viewResults.setBounds(50,100,200,30);
        takeQuiz.setBounds(50,150,200,30);

        f.add(addQ); f.add(viewResults); f.add(takeQuiz);
        f.setSize(300,250); f.setLayout(null); f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addQ.addActionListener(e -> addQuestion());
        viewResults.addActionListener(e -> showResults());
        takeQuiz.addActionListener(e -> takeQuiz());
    }

    // Add question
    static void addQuestion() {
        JFrame f = new JFrame("Add Question");
        JTextField q = new JTextField(), o1 = new JTextField(), o2 = new JTextField(),
                  o3 = new JTextField(), o4 = new JTextField(), ans = new JTextField();
        JLabel l1=new JLabel("Question:"), l2=new JLabel("Option1:"), l3=new JLabel("Option2:"),
               l4=new JLabel("Option3:"), l5=new JLabel("Option4:"), l6=new JLabel("Answer (1-4):");
        JButton b = new JButton("Save");

        l1.setBounds(20,20,100,30); q.setBounds(120,20,200,30);
        l2.setBounds(20,60,100,30); o1.setBounds(120,60,200,30);
        l3.setBounds(20,100,100,30); o2.setBounds(120,100,200,30);
        l4.setBounds(20,140,100,30); o3.setBounds(120,140,200,30);
        l5.setBounds(20,180,100,30); o4.setBounds(120,180,200,30);
        l6.setBounds(20,220,100,30); ans.setBounds(120,220,200,30); b.setBounds(140,270,80,30);

        f.add(l1); f.add(q); f.add(l2); f.add(o1); f.add(l3); f.add(o2);
        f.add(l4); f.add(o3); f.add(l5); f.add(o4); f.add(l6); f.add(ans); f.add(b);

        f.setSize(370,370); f.setLayout(null); f.setVisible(true);

        b.addActionListener(e -> {
            try(PrintWriter pw = new PrintWriter(new FileWriter(questionFile,true))){
                pw.println(q.getText() + "|" + o1.getText() + "|" + o2.getText() + "|" + o3.getText() + "|" + o4.getText() + "|" + ans.getText());
                JOptionPane.showMessageDialog(f,"Question Added!");
            } catch(Exception ex){ JOptionPane.showMessageDialog(f, ex.getMessage()); }
        });
    }

    // Show results
    static void showResults() {
        try(BufferedReader br = new BufferedReader(new FileReader(resultFile))){
            String line; StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.length()==0?"No results yet":sb.toString());
        } catch(Exception ex){ JOptionPane.showMessageDialog(null,"No results yet"); }
    }

    // Take quiz as user
    static void takeQuiz() {
        String name = JOptionPane.showInputDialog("Enter your name:");
        if(name==null || name.trim().isEmpty()) return;

        List<String[]> questions = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(questionFile))){
            String line;
            while((line = br.readLine())!=null){
                questions.add(line.split("\\|"));
            }
        } catch(Exception ex){ JOptionPane.showMessageDialog(null,"No questions available"); return; }

        Collections.shuffle(questions); // 🔀 Randomize question order

        int score = 0;
        int attempted = 0;

        for(String[] q : questions){
    if(q.length < 6) continue; // Skip malformed lines

    Object answer = JOptionPane.showInputDialog(null, q[0],
        "Quiz", JOptionPane.QUESTION_MESSAGE, null,
        new String[]{q[1],q[2],q[3],q[4]}, q[1]);

    if(answer == null) break;
    attempted++;

    try {
        int correctIndex = Integer.parseInt(q[5].trim()) - 1;
        if(correctIndex >= 0 && correctIndex < 4 && answer.equals(q[correctIndex])) {
            score++;
        }
    } catch(Exception ex) {
        // Skip invalid answer index
    }
}

        JOptionPane.showMessageDialog(null, "Quiz Completed!\nScore: " + score + "/" + attempted);

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try(PrintWriter pw = new PrintWriter(new FileWriter(resultFile,true))){
            pw.println(name + " - " + score + "/" + attempted + " on " + date);
        } catch(Exception ex){ JOptionPane.showMessageDialog(null, ex.getMessage()); }
    }
}