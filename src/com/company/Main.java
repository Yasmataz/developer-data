package com.company;

import java.sql.*;
import java.util.Scanner;

import static java.lang.System.exit;


public class Main {
    static Statement statement = null;

    public static void query1(String teamName) throws SQLException { // list applications worked on by a team
        String sql = String.format("SELECT Application.Name\n" +
                "FROM Employee\n" +
                "INNER JOIN Commits ON Employee.EmployeeId = Commits.EmployeeId\n" +
                "INNER JOIN ProductionBranch ON ProductionBranch.branchId = Commits.BranchId\n" +
                "INNER JOIN Application ON ProductionBranch.RepositoryName = Application.RepositoryName\n" +
                "WHERE TeamName = '%s'\n" +
                "GROUP by Application.Name", teamName);
        ResultSet rs = statement.executeQuery(sql);
        print("\nApplications\n-------------");
        while( rs.next() ) {
            print(rs.getString("Name"));
        }
    }

    public static void query2(String repo) throws SQLException { //Employees that contributed to a repository
        String sql = String.format("SELECT Employee.Name\n" +
                "FROM Commits\n" +
                "INNER JOIN ProductionBranch ON Commits.BranchId = ProductionBranch.branchId\n" +
                "INNER JOIN Repository ON ProductionBranch.RepositoryName = '%s' AND ProductionBranch.RepositoryName = Repository.Name\n" +
                "INNER JOIN Employee ON Commits.EmployeeId = Employee.EmployeeId", repo);
        ResultSet rs = statement.executeQuery(sql);

        print("\nEmployees\n-------------");

        while( rs.next()) {
            print(rs.getString("Name"));
        }
    }

    public static void query3() throws SQLException { //most popular platform
        String sql = "SELECT MAX(cnt) as cnt, Name\n" +
                "FROM\n" +
                "(SELECT Platform.Name, count(Application.PlatformId) as cnt\n" +
                "FROM Application\n" +
                "INNER JOIN Platform ON Application.PlatformId = Platform.pId\n" +
                "GROUP by PlatformId)";
        ResultSet rs = statement.executeQuery(sql);
        print("\nName\tCount\n--------------------");
        while( rs.next()) {
            print(rs.getString("Name") + "\t" + rs.getString("cnt"));
        }
    }

    public static void query4(String empId, String start, String end) throws SQLException { //commits by given employee in time range
        String sql = String.format("SELECT Commits.CommitId\n" +
                "FROM Commits\n" +
                "WHERE Commits.CommitDate BETWEEN '%s' AND '%s' AND Commits.EmployeeId = %s\n" +
                "ORDER BY CommitDate", start, end, empId);
        ResultSet rs = statement.executeQuery(sql);
        print("\nCommit Id\n-------------");

        while( rs.next()) {
            print(rs.getString("CommitId"));
        }
    }

    public static void query5(String start, String end) throws SQLException { //applications deployed in range
        String sql = String.format("SELECT Application.Name\n" +
                "FROM Application\n" +
                "WHERE Application.DateDeployed BETWEEN '%s' AND '%s'\n" +
                "ORDER By Application.DateDeployed", start, end);
        ResultSet rs = statement.executeQuery(sql);
        print("\nApplication\n-------------");

        while( rs.next()) {
            print(rs.getString("Name"));

        }
    }

    public static void query6() throws SQLException { //applications created off of unprotected branches
        String sql = "SELECT Application.name\n" +
                "FROM ProductionBranch\n" +
                "INNER JOIN Repository ON ProductionBranch.RepositoryName = Repository.Name\n" +
                "INNER JOIN Application ON Application.RepositoryName = Repository.Name\n" +
                "WHERE ProductionBranch.BranchProtections = \"not-protected\"";
        ResultSet rs = statement.executeQuery(sql);
        print("\nApplication\n-------------");

        while( rs.next()) {
            print(rs.getString("Name"));

        }
    }

    public static void query7() throws SQLException { //managers that are also developers
        String sql = "SELECT Employee.Name\n" +
                "FROM Commits\n" +
                "INNER JOIN Employee ON Commits.EmployeeId = Employee.EmployeeId\n" +
                "INNER JOIN Employee as emp ON Employee.EmployeeId = emp.Manager\n" +
                "GROUP by Employee.Name";
        ResultSet rs = statement.executeQuery(sql);

        print("\nManager Name\n---------------");

        while( rs.next()) {
            print(rs.getString("Name"));
        }
    }

    public static void query8(String repo) throws SQLException { //all commits on given repository
        String sql = String.format("SELECT Commits.CommitId\n" +
                "FROM Commits\n" +
                "INNER JOIN ProductionBranch ON Commits.BranchId = ProductionBranch.branchId\n" +
                "INNER JOIN Repository ON ProductionBranch.RepositoryName = '%s' AND ProductionBranch.RepositoryName = Repository.Name", repo);
        ResultSet rs = statement.executeQuery(sql);
        print("\nCommit Id\n-------------");

        while( rs.next()) {
            print(rs.getString("CommitId"));
        }
    }

    public static void query9() throws SQLException { //most popular language
        String sql = "SELECT MAX(cnt) as cnt, Language\n" +
                "FROM\n" +
                "(SELECT Repository.Language, count(Repository.Language) as cnt\n" +
                "FROM Repository \n" +
                "GROUP by Language)";
        ResultSet rs = statement.executeQuery(sql);
        print("\nLanguage\t\tCount\n--------------------");

        while( rs.next()) {
            print(rs.getString("Language") + "\t\t" + rs.getString("cnt"));
        }
    }

    public static void query10(String platform) throws SQLException { //applications running on given platform
        String sql = String.format("SELECT Application.Name\n" +
                "FROM Application\n" +
                "INNER JOIN Platform ON Application.PlatformId = Platform.pId AND Platform.Name = '%s'", platform);
        ResultSet rs = statement.executeQuery(sql);

        print("\nApplication\n-------------");

        while( rs.next()) {
            print(rs.getString("Name"));
        }
    }

    public static void connect() {
        Connection conn = null;
        try {
            // Move databaseProject file to specified path!!
            String url = "jdbc:sqlite:./databaseProject.db";
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showMenu() {
        System.out.println("\n\n");
        System.out.println("------------------ Menu -------------------");
        System.out.println("1. List applications worked on by a team");
        System.out.println("2. List employees that contributed to a repository");
        System.out.println("3. Find most popular platform");
        System.out.println("4. Find commits by given employee in time range");
        System.out.println("5. Find applications deployed in range");
        System.out.println("6. Find applications created off of unprotected branches");
        System.out.println("7. Find managers that are also developers");
        System.out.println("8. Find all commits on given repository");
        System.out.println("9. Find most popular language");
        System.out.println("10. List all applications running on given platform");
        System.out.println("11. Exit\n");
        System.out.println("Please enter selection ...");
    }

    public static void main(String[] args) throws SQLException {
        Scanner s = new Scanner(System.in);
        connect();
        while(true) {
            showMenu();
            String in = s.nextLine();

            switch (Integer.parseInt(in)) {
                case 1:
                    print("Enter team name");
                    String team = s.nextLine();
                    query1(team);
                    break;
                case 2:
                    print("Enter repo name");
                    String repo = s.nextLine();
                    query2(repo);
                    break;
                case 3:
                    query3();
                    break;
                case 4:
                    print("Enter Employee ID");
                    String empId = s.nextLine();
                    print("Enter Start date (YYYY-MM-DD)");
                    String start = s.nextLine();
                    print("Enter end date (YYYY-MM-DD)");
                    String end = s.nextLine();
                    query4(empId, start, end);
                    break;
                case 5:
                    print("Enter Start date (YYYY-MM-DD)");
                    start = s.nextLine();
                    print("Enter end date (YYYY-MM-DD)");
                    end = s.nextLine();
                    query5(start, end);
                    break;
                case 6:
                    query6();
                    break;
                case 7:
                    query7();
                    break;
                case 8:
                    print("Enter repo name");
                    repo = s.nextLine();
                    query8(repo);
                    break;
                case 9:
                    query9();
                    break;
                case 10:
                    print("Enter platfrom");
                    String platform = s.nextLine();
                    query10(platform);
                    break;
                case 11:
                    exit(0);
                    break;
                default:
                    System.out.println("Invalid, try again");
            }
        }

    }

    public static void print(String p) {
        System.out.println(p);
    }

}
