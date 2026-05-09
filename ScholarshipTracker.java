import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;

// -----------------------------------------------
// INTERFACE
// -----------------------------------------------
interface Displayable {
    void display();
}

// -----------------------------------------------
// ABSTRACT CLASS  (Abstraction)
// -----------------------------------------------
abstract class Person {

    protected String name;
    protected String department;

    public Person(String name, String department) {
        this.name       = name;
        this.department = department;
    }

    public String getName()       { return name; }
    public String getDepartment() { return department; }

    // every child class must write its own version
    public abstract void showInfo();
}

// -----------------------------------------------
// STUDENT CLASS
// Inheritance  -> extends Person
// Polymorphism -> overrides showInfo()
// Interface    -> implements Displayable
// -----------------------------------------------
class Student extends Person implements Displayable {

    private double cgpa;
    private double familyIncome;

    public Student(String name, String department, double cgpa, double familyIncome) {
        super(name, department);   // calling Person constructor
        this.cgpa         = cgpa;
        this.familyIncome = familyIncome;
    }

    public double getCgpa()         { return cgpa; }
    public double getFamilyIncome() { return familyIncome; }

    // Polymorphism - overriding abstract method from Person
    public void showInfo() {
        System.out.println("  Name          : " + name);
        System.out.println("  Department    : " + department);
        System.out.println("  CGPA          : " + cgpa);
        System.out.println("  Family Income : " + familyIncome);
    }

    // implementing interface method
    public void display() {
        System.out.println("  Student -> " + name + " | " + department + " | CGPA: " + cgpa);
    }
}

// -----------------------------------------------
// SPONSOR CLASS
// Inheritance  -> extends Person
// Polymorphism -> overrides showInfo()
// -----------------------------------------------
class Sponsor extends Person implements Displayable {

    private double budget;

    public Sponsor(String name, double budget) {
        super(name, "Sponsor");
        this.budget = budget;
    }

    public double getBudget() { return budget; }

    public void showInfo() {
        System.out.println("  Sponsor Name : " + name);
        System.out.println("  Budget       : Rs " + budget);
    }

    public void display() {
        System.out.println("  Sponsor -> " + name + " | Budget: Rs " + budget);
    }
}

// -----------------------------------------------
// MAIN CLASS
// -----------------------------------------------
public class ScholarshipTracker {

    static String DB_URL  = "jdbc:mysql://localhost:3306/scholarship_db";
    static String DB_USER = "root";
    static String DB_PASS = "M@rw@123?";

    static Connection conn = null;
    static Scanner sc = new Scanner(System.in);

    // -----------------------------------------------
    public static void main(String[] args) {

        System.out.println();
        System.out.println("  ============================================================");
        System.out.println("       SCHOLARSHIP AND FINANCIAL AID TRACKER");
        System.out.println("       Developed by : Marwa Karim");
        System.out.println("  ============================================================");

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("  Database connected successfully.");
        } catch (SQLException e) {
            System.out.println("  Could not connect to database: " + e.getMessage());
            return;
        }

        int choice = -1;

        while (choice != 0) {
            printMainMenu();

            System.out.print("  Enter any option: ");
            choice = readInt();

            if (choice == 1) {
                showAppliedStudents();
            } else if (choice == 2) {
                showScholarships();
            } else if (choice == 3) {
                applyForScholarship();
            } else if (choice == 4) {
                studentReport();
            } else if (choice == 5) {
                addNewSponsor();
            } else if (choice == 0) {
                System.out.println();
                System.out.println("  Thank you for using this system. Goodbye!");
            } else {
                System.out.println("  Wrong option. Please try again.");
            }
        }

        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // -----------------------------------------------
    static void printMainMenu() {
        System.out.println();
        System.out.println("  ============================================================");
        System.out.println("  MAIN MENU");
        System.out.println("  ------------------------------------------------------------");
        System.out.println("  1. View Students Who Applied for Scholarships");
        System.out.println("  2. View All Scholarships with Sponsor Info");
        System.out.println("  3. Apply for a Scholarship");
        System.out.println("  4. My Application Report (Enter Student ID)");
        System.out.println("  5. Add New Sponsor");
        System.out.println("  0. Exit");
        System.out.println("  ------------------------------------------------------------");
    }

    // -----------------------------------------------
    // OPTION 1 - show students who applied
    // -----------------------------------------------
    static void showAppliedStudents() {
        System.out.println();
        System.out.println("  ------------------------------------------------------------");
        System.out.println("  STUDENTS WHO APPLIED FOR SCHOLARSHIPS");
        System.out.println("  ------------------------------------------------------------");

        String sql = "SELECT SR.AppID, ST.StudentID, ST.Name, ST.Department, ST.CGPA, "
                   + "SC.Name AS Scholarship, SR.ApplyDate, SR.Status "
                   + "FROM ScholarshipRecords SR "
                   + "JOIN Students ST ON SR.StudentID = ST.StudentID "
                   + "JOIN Scholarships SC ON SR.ScholarshipID = SC.ScholarshipID "
                   + "ORDER BY SR.AppID";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            System.out.printf("  %-6s %-5s %-20s %-22s %-5s %-28s %-12s %s%n",
                "AppID","StID","Name","Department","CGPA","Scholarship","Date","Status");
            System.out.println("  " + "-".repeat(115));

            boolean anyRecord = false;

            while (rs.next()) {
                anyRecord = true;
                System.out.printf("  %-6d %-5d %-20s %-22s %-5.2f %-28s %-12s %s%n",
                    rs.getInt("AppID"),
                    rs.getInt("StudentID"),
                    rs.getString("Name"),
                    rs.getString("Department"),
                    rs.getDouble("CGPA"),
                    rs.getString("Scholarship"),
                    rs.getDate("ApplyDate").toString(),
                    rs.getString("Status"));
            }

            if (anyRecord == false) {
                System.out.println("  No applications found.");
            }

            rs.close();
            st.close();

        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // -----------------------------------------------
    // OPTION 2 - show all scholarships with sponsor
    // -----------------------------------------------
    static void showScholarships() {
        System.out.println();
        System.out.println("  ------------------------------------------------------------");
        System.out.println("  ALL SCHOLARSHIPS WITH SPONSOR DETAILS");
        System.out.println("  ------------------------------------------------------------");

        String sql = "SELECT SC.ScholarshipID, SC.Name, SC.MinCGPA, SC.MaxIncome, "
                   + "SC.Amount, SC.ScholarshipType, SP.Name AS SponsorName, SP.Budget "
                   + "FROM Scholarships SC "
                   + "JOIN Sponsors SP ON SC.SponsorID = SP.SponsorID";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            System.out.printf("  %-4s %-32s %-8s %-12s %-10s %-12s %-28s %s%n",
                "ID","Scholarship Name","MinCGPA","Max Income","Amount","Type","Sponsor","Sponsor Budget");
            System.out.println("  " + "-".repeat(120));

            while (rs.next()) {
                System.out.printf("  %-4d %-32s %-8.2f %-12.0f %-10.0f %-12s %-28s Rs %.0f%n",
                    rs.getInt("ScholarshipID"),
                    rs.getString("Name"),
                    rs.getDouble("MinCGPA"),
                    rs.getDouble("MaxIncome"),
                    rs.getDouble("Amount"),
                    rs.getString("ScholarshipType"),
                    rs.getString("SponsorName"),
                    rs.getDouble("Budget"));
            }

            rs.close();
            st.close();

        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // -----------------------------------------------
    // OPTION 3 - student applies for scholarship
    // -----------------------------------------------
    static void applyForScholarship() {
        System.out.println();
        System.out.println("  ------------------------------------------------------------");
        System.out.println("  APPLY FOR SCHOLARSHIP");
        System.out.println("  ------------------------------------------------------------");

        sc.nextLine(); // clear buffer

        System.out.print("  Enter your Name          : ");
        String name = sc.nextLine().trim();

        System.out.print("  Enter your Department    : ");
        String dept = sc.nextLine().trim();

        System.out.print("  Enter your CGPA          : ");
        double cgpa = Double.parseDouble(sc.nextLine().trim());

        System.out.print("  Enter your Family Income : ");
        double income = Double.parseDouble(sc.nextLine().trim());

        // using Student object - OOP in action
        Student s = new Student(name, dept, cgpa, income);
        System.out.println();
        System.out.println("  Your entered details:");
        s.showInfo();   // polymorphism
        s.display();    // interface

        // save student to database first
        int newStudentId = -1;

        try {
            String insertStu = "INSERT INTO Students (Name, Department, CGPA, FamilyIncome) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertStu, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, s.getName());
            ps.setString(2, s.getDepartment());
            ps.setDouble(3, s.getCgpa());
            ps.setDouble(4, s.getFamilyIncome());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                newStudentId = keys.getInt(1);
                System.out.println("  Your Student ID is: " + newStudentId);
            }

            ps.close();

        } catch (SQLException e) {
            System.out.println("  Error saving student: " + e.getMessage());
            return;
        }

        // show available scholarships
        System.out.println();
        System.out.println("  Available Scholarships:");
        System.out.println("  " + "-".repeat(80));
        System.out.printf("  %-4s %-32s %-8s %-12s %-10s %s%n",
            "ID","Name","MinCGPA","Max Income","Amount","Type");
        System.out.println("  " + "-".repeat(80));

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Scholarships");

            while (rs.next()) {
                System.out.printf("  %-4d %-32s %-8.2f %-12.0f %-10.0f %s%n",
                    rs.getInt("ScholarshipID"),
                    rs.getString("Name"),
                    rs.getDouble("MinCGPA"),
                    rs.getDouble("MaxIncome"),
                    rs.getDouble("Amount"),
                    rs.getString("ScholarshipType"));
            }

            rs.close();
            st.close();

        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
            return;
        }

        System.out.println();
        System.out.print("  Enter Scholarship ID you want to apply for: ");
        int scholarshipId = readInt();

        // check if student already applied to any scholarship
        try {
            PreparedStatement checkPs = conn.prepareStatement(
                "SELECT COUNT(*) FROM ScholarshipRecords WHERE StudentID = ?"
            );
            checkPs.setInt(1, newStudentId);
            ResultSet checkRs = checkPs.executeQuery();
            checkRs.next();
            int alreadyApplied = checkRs.getInt(1);
            checkPs.close();

            if (alreadyApplied > 0) {
                System.out.println("  You have already applied for a scholarship. One application allowed at a time.");
                return;
            }

        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
            return;
        }

        // get scholarship details for eligibility check
        double minCgpa     = 0;
        double maxIncome   = 0;
        String schType     = "";
        String schName     = "";

        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Scholarships WHERE ScholarshipID = ?"
            );
            ps.setInt(1, scholarshipId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                minCgpa   = rs.getDouble("MinCGPA");
                maxIncome = rs.getDouble("MaxIncome");
                schType   = rs.getString("ScholarshipType");
                schName   = rs.getString("Name");
            } else {
                System.out.println("  Scholarship not found.");
                ps.close();
                return;
            }

            ps.close();

        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
            return;
        }

        // eligibility check logic
        System.out.println();
        System.out.println("  Checking eligibility for: " + schName);
        System.out.println("  Required CGPA       : " + minCgpa);
        System.out.println("  Required Max Income : Rs " + maxIncome);
        System.out.println("  Your CGPA           : " + cgpa);
        System.out.println("  Your Family Income  : Rs " + income);
        System.out.println();

        boolean cgpaOk   = cgpa >= minCgpa;
        boolean incomeOk = income <= maxIncome;
        String finalStatus = "";

        if (cgpaOk == true && incomeOk == true) {
            finalStatus = "Approved";
            System.out.println("  Result: You are ELIGIBLE for this scholarship!");
            System.out.println("  Status: APPROVED");
        } else {
            finalStatus = "Rejected";
            System.out.println("  Result: You are NOT eligible for this scholarship.");

            if (cgpaOk == false) {
                System.out.println("  Reason: Your CGPA (" + cgpa + ") is below required (" + minCgpa + ")");
            }
            if (incomeOk == false) {
                System.out.println("  Reason: Your income (Rs " + income + ") is above allowed (Rs " + maxIncome + ")");
            }
            System.out.println("  Status: REJECTED");
        }

        // save application with correct status
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ScholarshipRecords (StudentID, ScholarshipID, ApplyDate, Status) VALUES (?, ?, CURDATE(), ?)"
            );
            ps.setInt(1, newStudentId);
            ps.setInt(2, scholarshipId);
            ps.setString(3, finalStatus);
            ps.executeUpdate();

            System.out.println();
            System.out.println("  Application saved in database.");
            System.out.println("  Your Student ID : " + newStudentId);
            System.out.println("  Use this ID in Option 4 to check your report.");

            ps.close();

        } catch (SQLException e) {
            System.out.println("  Error saving application: " + e.getMessage());
        }
    }

    // -----------------------------------------------
    // OPTION 4 - student checks their own report
    // -----------------------------------------------
    static void studentReport() {
        System.out.println();
        System.out.println("  ------------------------------------------------------------");
        System.out.println("  MY APPLICATION REPORT");
        System.out.println("  ------------------------------------------------------------");

        System.out.print("  Enter your Student ID: ");
        int studentId = readInt();

        String sql = "SELECT ST.StudentID, ST.Name, ST.Department, ST.CGPA, ST.FamilyIncome, "
                   + "SC.Name AS ScholarshipName, SC.ScholarshipType, SC.Amount, "
                   + "SP.Name AS SponsorName, SR.ApplyDate, SR.Status "
                   + "FROM ScholarshipRecords SR "
                   + "JOIN Students ST ON SR.StudentID = ST.StudentID "
                   + "JOIN Scholarships SC ON SR.ScholarshipID = SC.ScholarshipID "
                   + "JOIN Sponsors SP ON SC.SponsorID = SP.SponsorID "
                   + "WHERE ST.StudentID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println();
                System.out.println("  ============================================================");
                System.out.println("  STUDENT APPLICATION REPORT");
                System.out.println("  ============================================================");
                System.out.println("  Student ID    : " + rs.getInt("StudentID"));
                System.out.println("  Name          : " + rs.getString("Name"));
                System.out.println("  Department    : " + rs.getString("Department"));
                System.out.println("  CGPA          : " + rs.getDouble("CGPA"));
                System.out.println("  Family Income : Rs " + rs.getDouble("FamilyIncome"));
                System.out.println("  ------------------------------------------------------------");
                System.out.println("  Scholarship   : " + rs.getString("ScholarshipName"));
                System.out.println("  Type          : " + rs.getString("ScholarshipType"));
                System.out.println("  Amount        : Rs " + rs.getDouble("Amount"));
                System.out.println("  Sponsor       : " + rs.getString("SponsorName"));
                System.out.println("  Apply Date    : " + rs.getDate("ApplyDate").toString());
                System.out.println("  ------------------------------------------------------------");

                String status = rs.getString("Status");

                if (status.equals("Approved")) {
                    System.out.println("  APPLICATION STATUS : ** APPROVED **");
                    System.out.println("  Congratulations! Your scholarship has been approved.");
                } else if (status.equals("Rejected")) {
                    System.out.println("  APPLICATION STATUS : ** REJECTED **");
                    System.out.println("  Sorry, your application did not meet the criteria.");
                } else {
                    System.out.println("  APPLICATION STATUS : ** PENDING **");
                    System.out.println("  Your application is under review.");
                }

                System.out.println("  ============================================================");

            } else {
                System.out.println("  No application found for Student ID: " + studentId);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // -----------------------------------------------
    // OPTION 5 - add new sponsor
    // -----------------------------------------------
    static void addNewSponsor() {
        System.out.println();
        System.out.println("  ------------------------------------------------------------");
        System.out.println("  ADD NEW SPONSOR");
        System.out.println("  ------------------------------------------------------------");

        sc.nextLine(); // clear buffer

        System.out.print("  Enter Sponsor Name   : ");
        String name = sc.nextLine().trim();

        System.out.print("  Enter Budget Amount  : ");
        double budget = Double.parseDouble(sc.nextLine().trim());

        // using Sponsor object - Inheritance + Polymorphism
        Sponsor sponsor = new Sponsor(name, budget);
        System.out.println();
        System.out.println("  Sponsor details you entered:");
        sponsor.showInfo();   // polymorphism
        sponsor.display();    // interface

        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Sponsors (Name, Budget) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, sponsor.getName());
            ps.setDouble(2, sponsor.getBudget());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                System.out.println("  Sponsor saved. New Sponsor ID = " + keys.getInt(1));
            }

            ps.close();

        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // -----------------------------------------------
    // helper - read integer safely
    // -----------------------------------------------
    static int readInt() {
        int value  = -1;
        boolean ok = false;

        while (ok == false) {
            try {
                value = Integer.parseInt(sc.nextLine().trim());
                ok    = true;
            } catch (NumberFormatException e) {
                System.out.print("  Please enter a valid number: ");
            }
        }

        return value;
    }
}