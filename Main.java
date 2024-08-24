import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class User {
    private String username;
    private String password;
    private String role; // Roles: Admin, Bidder, Viewer
    private int performanceScore; // Bidder's past performance score (0-100)

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.performanceScore = 50; // Default performance score
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public int getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(int score) {
        this.performanceScore = score;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }
}

class Tender {
    private String tenderId;
    private String tenderName;
    private double tenderAmount;
    private String tenderDescription;
    private String awardedBidder;
    private double awardedAmount;
    private String awardedCriteria; // Criteria used for awarding (e.g., "Lowest Bid", "Best Value")

    public Tender(String tenderId, String tenderName, double tenderAmount, String tenderDescription) {
        this.tenderId = tenderId;
        this.tenderName = tenderName;
        this.tenderAmount = tenderAmount;
        this.tenderDescription = tenderDescription;
        this.awardedBidder = null;
        this.awardedAmount = 0.0;
        this.awardedCriteria = null;
    }

    public String getTenderId() {
        return tenderId;
    }

    public String getTenderName() {
        return tenderName;
    }

    public double getTenderAmount() {
        return tenderAmount;
    }

    public String getTenderDescription() {
        return tenderDescription;
    }

    public void awardTender(String bidderName, double amount, String criteria) {
        this.awardedBidder = bidderName;
        this.awardedAmount = amount;
        this.awardedCriteria = criteria;
    }

    public boolean isAwarded() {
        return awardedBidder != null;
    }

    public String getAwardedBidder() {
        return awardedBidder;
    }

    public double getAwardedAmount() {
        return awardedAmount;
    }

    public String getAwardedCriteria() {
        return awardedCriteria;
    }

    @Override
    public String toString() {
        return "Tender ID: " + tenderId + "\nTender Name: " + tenderName + "\nTender Amount: " + tenderAmount + "\nTender Description: " + tenderDescription +
                (isAwarded() ? "\nAwarded to: " + awardedBidder + " with amount: " + awardedAmount + " based on " + awardedCriteria : "");
    }
}

class Bid {
    private String tenderId;
    private String bidderName;
    private double bidAmount;
    private int deliveryTime; // Delivery time in days
    private int bidderPerformance; // Bidder's performance score

    public Bid(String tenderId, String bidderName, double bidAmount, int deliveryTime, int bidderPerformance) {
        this.tenderId = tenderId;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
        this.deliveryTime = deliveryTime;
        this.bidderPerformance = bidderPerformance;
    }

    public String getTenderId() {
        return tenderId;
    }

    public String getBidderName() {
        return bidderName;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public int getBidderPerformance() {
        return bidderPerformance;
    }

    @Override
    public String toString() {
        return "Bidder Name: " + bidderName + "\nBid Amount: " + bidAmount + "\nDelivery Time: " + deliveryTime + " days\nPerformance Score: " + bidderPerformance;
    }
}

class TenderManagementSystem {
    private List<Tender> tenders;
    private List<Bid> bids;
    private Map<String, User> users;

    public TenderManagementSystem() {
        tenders = new ArrayList<>();
        bids = new ArrayList<>();
        users = new HashMap<>();
    }

    public void registerUser(String username, String password, String role) {
        if (users.containsKey(username)) {
            System.out.println("User already exists.");
            return;
        }
        users.put(username, new User(username, password, role));
        System.out.println("User registered successfully!");
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.authenticate(password)) {
            System.out.println("Login successful. Welcome, " + username + "!");
            return user;
        }
        System.out.println("Invalid credentials.");
        return null;
    }

    public void addTender(String tenderId, String tenderName, double tenderAmount, String tenderDescription, User user) {
        if (!user.getRole().equals("Admin")) {
            System.out.println("Only Admin can add tenders.");
            return;
        }
        Tender tender = new Tender(tenderId, tenderName, tenderAmount, tenderDescription);
        tenders.add(tender);
        System.out.println("Tender added successfully!");
    }

    public void placeBid(String tenderId, String bidderName, double bidAmount, int deliveryTime, User user) {
        if (!user.getRole().equals("Bidder")) {
            System.out.println("Only Bidders can place bids.");
            return;
        }
        boolean tenderExists = false;
        for (Tender tender : tenders) {
            if (tender.getTenderId().equals(tenderId) && !tender.isAwarded()) {
                tenderExists = true;
                break;
            }
        }

        if (tenderExists) {
            int bidderPerformance = user.getPerformanceScore();
            Bid bid = new Bid(tenderId, bidderName, bidAmount, deliveryTime, bidderPerformance);
            bids.add(bid);
            System.out.println("Bid placed successfully!");
        } else {
            System.out.println("Tender ID not found or already awarded!");
        }
    }

    public void awardTender(String tenderId, User user) {
        if (!user.getRole().equals("Admin")) {
            System.out.println("Only Admin can award tenders.");
            return;
        }

        List<Bid> relevantBids = new ArrayList<>();
        for (Bid bid : bids) {
            if (bid.getTenderId().equals(tenderId)) {
                relevantBids.add(bid);
            }
        }

        if (relevantBids.isEmpty()) {
            System.out.println("No bids found for this tender.");
            return;
        }

        // Automatic Bid Evaluation based on a score combining price, delivery time, and performance
        Bid bestBid = relevantBids.get(0);
        double bestScore = evaluateBid(bestBid);

        for (Bid bid : relevantBids) {
            double currentScore = evaluateBid(bid);
            if (currentScore > bestScore) {
                bestBid = bid;
                bestScore = currentScore;
            }
        }

        for (Tender tender : tenders) {
            if (tender.getTenderId().equals(tenderId)) {
                tender.awardTender(bestBid.getBidderName(), bestBid.getBidAmount(), "Best Value");
                System.out.println("Tender awarded to " + bestBid.getBidderName() + " with amount " + bestBid.getBidAmount() + " based on Best Value criteria.");
                return;
            }
        }
    }

    private double evaluateBid(Bid bid) {
        // Simple weighted scoring system for evaluation
        double priceWeight = 0.5;
        double deliveryWeight = 0.3;
        double performanceWeight = 0.2;

        double maxPrice = 100000; // Assume max price for normalization
        double maxDeliveryTime = 30; // Assume max delivery time for normalization
        double maxPerformance = 100; // Maximum performance score

        double priceScore = (maxPrice - bid.getBidAmount()) / maxPrice;
        double deliveryScore = (maxDeliveryTime - bid.getDeliveryTime()) / maxDeliveryTime;
        double performanceScore = bid.getBidderPerformance() / maxPerformance;

        return (priceWeight * priceScore) + (deliveryWeight * deliveryScore) + (performanceWeight * performanceScore);
    }

    public void compareBids(String tenderId) {
        List<Bid> relevantBids = new ArrayList<>();
        for (Bid bid : bids) {
            if (bid.getTenderId().equals(tenderId)) {
                relevantBids.add(bid);
            }
        }

        if (relevantBids.isEmpty()) {
            System.out.println("No bids found for this tender.");
            return;
        }

        System.out.println("Comparing bids for Tender ID: " + tenderId);
        for (Bid bid : relevantBids) {
            System.out.println(bid);
            System.out.println("--------------------");
        }
    }

    public void displayTenders(User user) {
        if (tenders.isEmpty()) {
            System.out.println("No tenders available.");
        } else {
            for (Tender tender : tenders) {
                System.out.println(tender);
                System.out.println("--------------------");
            }
        }
    }

    public void displayBids(String tenderId, User user) {
        if (!user.getRole().equals("Admin") && !user.getRole().equals("Viewer")) {
            System.out.println("Only Admins or Viewers can view bids.");
            return;
        }
        boolean bidsFound = false;
        for (Bid bid : bids) {
            if (bid.getTenderId().equals(tenderId)) {
                System.out.println(bid);
                System.out.println("--------------------");
                bidsFound = true;
            }
        }

        if (!bidsFound) {
            System.out.println("No bids found for the tender ID: " + tenderId);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TenderManagementSystem tms = new TenderManagementSystem();

        // Pre-register an Admin and Bidder for demonstration
        tms.registerUser("admin", "admin123", "Admin");
        tms.registerUser("bidder1", "bidder123", "Bidder");
        tms.registerUser("bidder2", "bidder456", "Bidder");

        User loggedInUser = null;

        while (true) {
            if (loggedInUser == null) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();
                loggedInUser = tms.login(username, password);
            }

            if (loggedInUser != null) {
                System.out.println("\nTender Management System");
                System.out.println("1. Add Tender");
                System.out.println("2. Place Bid");
                System.out.println("3. Display Tenders");
                System.out.println("4. Display Bids");
                System.out.println("5. Award Tender");
                System.out.println("6. Compare Bids");
                System.out.println("7. Logout");
                System.out.println("8. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter Tender ID: ");
                        String tenderId = scanner.nextLine();
                        System.out.print("Enter Tender Name: ");
                        String tenderName = scanner.nextLine();
                        System.out.print("Enter Tender Amount: ");
                        double tenderAmount = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                        System.out.print("Enter Tender Description: ");
                        String tenderDescription = scanner.nextLine();
                        tms.addTender(tenderId, tenderName, tenderAmount, tenderDescription, loggedInUser);
                        break;

                    case 2:
                        System.out.print("Enter Tender ID to bid on: ");
                        String bidTenderId = scanner.nextLine();
                        System.out.print("Enter Bid Amount: ");
                        double bidAmount = scanner.nextDouble();
                        System.out.print("Enter Delivery Time (in days): ");
                        int deliveryTime = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                        tms.placeBid(bidTenderId, loggedInUser.getUsername(), bidAmount, deliveryTime, loggedInUser);
                        break;

                    case 3:
                        if (loggedInUser.getRole().equals("Admin") || loggedInUser.getRole().equals("Viewer") || loggedInUser.getRole().equals("Bidder")) {
                            tms.displayTenders(loggedInUser);
                        } else {
                            System.out.println("You do not have permission to view tenders.");
                        }
                        break;

                    case 4:
                        System.out.print("Enter Tender ID to view bids: ");
                        String bidViewTenderId = scanner.nextLine();
                        tms.displayBids(bidViewTenderId, loggedInUser);
                        break;

                    case 5:
                        System.out.print("Enter Tender ID to award: ");
                        String awardTenderId = scanner.nextLine();
                        tms.awardTender(awardTenderId, loggedInUser);
                        break;

                    case 6:
                        System.out.print("Enter Tender ID to compare bids: ");
                        String compareTenderId = scanner.nextLine();
                        tms.compareBids(compareTenderId);
                        break;

                    case 7:
                        loggedInUser = null;
                        System.out.println("Logged out successfully.");
                        break;

                    case 8:
                        System.out.println("Exiting the system.");
                        scanner.close();
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }
}
