import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Tender {
    private String tenderId;
    private String tenderName;
    private double tenderAmount;
    private String tenderDescription;

    public Tender(String tenderId, String tenderName, double tenderAmount, String tenderDescription) {
        this.tenderId = tenderId;
        this.tenderName = tenderName;
        this.tenderAmount = tenderAmount;
        this.tenderDescription = tenderDescription;
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

    @Override
    public String toString() {
        return "Tender ID: " + tenderId + "\nTender Name: " + tenderName + "\nTender Amount: " + tenderAmount + "\nTender Description: " + tenderDescription;
    }
}

class Bid {
    private String tenderId;
    private String bidderName;
    private double bidAmount;

    public Bid(String tenderId, String bidderName, double bidAmount) {
        this.tenderId = tenderId;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
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

    @Override
    public String toString() {
        return "Bidder Name: " + bidderName + "\nBid Amount: " + bidAmount;
    }
}

class TenderManagementSystem {
    private List<Tender> tenders;
    private List<Bid> bids;

    public TenderManagementSystem() {
        tenders = new ArrayList<>();
        bids = new ArrayList<>();
    }

    public void addTender(String tenderId, String tenderName, double tenderAmount, String tenderDescription) {
        Tender tender = new Tender(tenderId, tenderName, tenderAmount, tenderDescription);
        tenders.add(tender);
        System.out.println("Tender added successfully!");
    }

    public void placeBid(String tenderId, String bidderName, double bidAmount) {
        boolean tenderExists = false;
        for (Tender tender : tenders) {
            if (tender.getTenderId().equals(tenderId)) {
                tenderExists = true;
                break;
            }
        }

        if (tenderExists) {
            Bid bid = new Bid(tenderId, bidderName, bidAmount);
            bids.add(bid);
            System.out.println("Bid placed successfully!");
        } else {
            System.out.println("Tender ID not found!");
        }
    }

    public void displayTenders() {
        if (tenders.isEmpty()) {
            System.out.println("No tenders available.");
        } else {
            for (Tender tender : tenders) {
                System.out.println(tender);
                System.out.println("--------------------");
            }
        }
    }

    public void displayBids(String tenderId) {
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

        while (true) {
            System.out.println("Tender Management System");
            System.out.println("1. Add Tender");
            System.out.println("2. Place Bid");
            System.out.println("3. Display Tenders");
            System.out.println("4. Display Bids");
            System.out.println("5. Exit");
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
                    tms.addTender(tenderId, tenderName, tenderAmount, tenderDescription);
                    break;

                case 2:
                    System.out.print("Enter Tender ID to bid on: ");
                    String bidTenderId = scanner.nextLine();
                    System.out.print("Enter Bidder Name: ");
                    String bidderName = scanner.nextLine();
                    System.out.print("Enter Bid Amount: ");
                    double bidAmount = scanner.nextDouble();
                    scanner.nextLine(); // consume newline
                    tms.placeBid(bidTenderId, bidderName, bidAmount);
                    break;

                case 3:
                    tms.displayTenders();
                    break;

                case 4:
                    System.out.print("Enter Tender ID to view bids: ");
                    String bidViewTenderId = scanner.nextLine();
                    tms.displayBids(bidViewTenderId);
                    break;

                case 5:
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
