import java.util.*;

class Visitor {
    String name;
    int age;
    String category;
    int token;

    public Visitor(String name, int age, String category, int token) {
        this.name = name;
        this.age = age;
        this.category = category;
        this.token = token;
    }

    @Override
    public String toString() {
        return name + " (Age: " + age + ", Category: " + category + ", Token: " + token + ")";
    }
}

class FairQueueTempleDarshan {
    Map<String, Queue<Visitor>> queues;
    String[] categoryOrder = {"VIP", "DISABLED", "SENIOR", "GENERAL"};
    int tokenCounter = 1;
    int categoryIndex = 0;
    int totalServed = 0;

    public FairQueueTempleDarshan() {
        queues = new LinkedHashMap<>();
        for (String cat : categoryOrder) {
            queues.put(cat, new LinkedList<>());
        }
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
    }

    public void processNextVisitor() {
        for (int i = 0; i < categoryOrder.length; i++) {
            String currentCategory = categoryOrder[categoryIndex];
            if (!queues.get(currentCategory).isEmpty()) {
                Visitor visitor = queues.get(currentCategory).poll();
                System.out.println("\n Processing Visitor: " + visitor);
                System.out.println(" Visitor with Token " + visitor.token + " is now proceeding for Darshan.");
                totalServed++;
                categoryIndex = (categoryIndex + 1) % categoryOrder.length;
                return;
            }
            categoryIndex = (categoryIndex + 1) % categoryOrder.length;
        }
        System.out.println("\n No visitors in queue.");
    }

    public void peekNextVisitor() {
        int originalIndex = categoryIndex;
        for (int i = 0; i < categoryOrder.length; i++) {
            String currentCategory = categoryOrder[categoryIndex];
            if (!queues.get(currentCategory).isEmpty()) {
                System.out.println("\n Next in line: " + queues.get(currentCategory).peek());
                categoryIndex = originalIndex;
                return;
            }
            categoryIndex = (categoryIndex + 1) % categoryOrder.length;
        }
        System.out.println("\n No visitors in queue.");
    }

    public void showQueue() {
        System.out.println("\n Current Queue Status:");
        for (String cat : categoryOrder) {
            System.out.println(cat + ":");
            for (Visitor visitor : queues.get(cat)) {
                System.out.println("  - " + visitor);
            }
        }
        System.out.println("\n Total Visitors Served: " + totalServed + "\n");
    }
}

public class TempleDarshanSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FairQueueTempleDarshan templeQueue = new FairQueueTempleDarshan();

        while (true) {
            System.out.println("\n---- Temple Darshan Ticketing ----");
            System.out.println("1. Add Visitor");
            System.out.println("2. Process Next Visitor");
            System.out.println("3. Peek Next Visitor");
            System.out.println("4. Show Queue");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter Visitor Name: ");
                    String name = sc.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println(" Name cannot be empty.");
                        continue;
                    }
                    System.out.print("Enter Age: ");
                    int age;
                    try {
                        age = Integer.parseInt(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid age. Please enter a valid number.");
                        continue;
                    }
                    System.out.print("Enter Category (VIP / Disabled / Senior / General): ");
                    String category = sc.nextLine().trim().toUpperCase();
                    if (!Arrays.asList("VIP", "DISABLED", "SENIOR", "GENERAL").contains(category)) {
                        System.out.println(" Invalid category. Please choose from VIP, Disabled, Senior, or General.");
                        continue;
                    }
                    templeQueue.addVisitor(name, age, category);
                    break;

                case "2":
                    templeQueue.processNextVisitor();
                    break;

                case "3":
                    templeQueue.peekNextVisitor();
                    break;

                case "4":
                    templeQueue.showQueue();
                    break;

                case "5":
                    System.out.println("\n Exiting. Have a blessed day!");
                    sc.close();
                    return;

                default:
                    System.out.println(" Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }
}
