import java.time.LocalDateTime;

class Visitor {
    String name;
    int age;
    String category;
    int token;
    LocalDateTime timeEntered;  // ✅ Added

    public Visitor(String name, int age, String category, int token) {
        this.name = name;
        this.age = age;
        this.category = category;
        this.token = token;
        this.timeEntered = LocalDateTime.now(); // ✅ Capture entry time
    }

    @Override
    public String toString() {
        return name + " (Age: " + age + ", Category: " + category + ", Token: " + token + ")";
    }
}


// Insert log when visitor is added
public void logVisitorEntry(Visitor visitor) {
    String sql = "INSERT INTO logs (token, name, age, category, time_entered) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, visitor.token);
        ps.setString(2, visitor.name);
        ps.setInt(3, visitor.age);
        ps.setString(4, visitor.category);
        ps.setTimestamp(5, Timestamp.valueOf(visitor.timeEntered));
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// Update log when visitor is served
public void logVisitorServed(Visitor visitor) {
    String sql = "UPDATE logs SET time_served=?, duration_seconds=TIMESTAMPDIFF(SECOND, time_entered, ?) WHERE token=?";
    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement ps = conn.prepareStatement(sql)) {
        Timestamp servedTime = Timestamp.valueOf(java.time.LocalDateTime.now());
        ps.setTimestamp(1, servedTime);
        ps.setTimestamp(2, servedTime);
        ps.setInt(3, visitor.token);
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// Fetch all logs
public List<String> getAllLogs() {
    List<String> logs = new ArrayList<>();
    String sql = "SELECT * FROM logs ORDER BY id";
    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            String entry = String.format(
                "Token %d | %s | %s | Entered: %s | Served: %s | Duration: %s sec",
                rs.getInt("token"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getTimestamp("time_entered"),
                rs.getTimestamp("time_served"),
                rs.getObject("duration_seconds") != null ? rs.getInt("duration_seconds") : "Still waiting"
            );
            logs.add(entry);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return logs;
}


public void addVisitor(String name, int age, String category) {
    int token = tokenCounter++;
    if (category.equals("GENERAL") && age >= 60) {
        System.out.println(" Auto-upgraded " + name + " from GENERAL to SENIOR due to age.");
        category = "SENIOR";
    }
    Visitor visitor = new Visitor(name, age, category, token);
    queues.get(category).add(visitor);
    System.out.println("Added: " + visitor);

    visitorDAO.addVisitor(visitor);       // ✅ Store visitor
    visitorDAO.logVisitorEntry(visitor);  // ✅ Store log entry
}

public void processNextVisitor() {
    for (int i = 0; i < categoryOrder.length; i++) {
        String currentCategory = categoryOrder[categoryIndex];
        if (!queues.get(currentCategory).isEmpty()) {
            Visitor visitor = queues.get(currentCategory).poll();
            System.out.println("\n Processing Visitor: " + visitor);
            System.out.println(" Visitor with Token " + visitor.token + " is now proceeding for Darshan.");
            totalServed++;

            visitorDAO.updateVisitorStatus(visitor.token, "SERVED"); // ✅ Update DB
            visitorDAO.logVisitorServed(visitor); // ✅ Update logs

            categoryIndex = (categoryIndex + 1) % categoryOrder.length;
            return;
        }
        categoryIndex = (categoryIndex + 1) % categoryOrder.length;
    }
    System.out.println("\n No visitors in queue.");
}

// ✅ Show all logs
public void showLogs() {
    System.out.println("\n Visitor Logs:");
    List<String> logs = visitorDAO.getAllLogs();
    if (logs.isEmpty()) {
        System.out.println(" No logs available.");
    } else {
        for (String log : logs) {
            System.out.println(" " + log);
        }
    }
}


