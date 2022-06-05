import java.sql.*;
import java.util.*;

public class BankingApplication
{
    boolean exit = false;

    private void printHeader()
    {
        System.out.println("+-------------------------------+");
        System.out.println("|        Welcome to our         |");
        System.out.println("|       Bank Application        |");
        System.out.println("+-------------------------------+\n");
    }

    private void printMenu()
    {
        System.out.println("\nPlease make a selection: ");
        System.out.println("(1) Create a New Account");
        System.out.println("(2) Check Account Balance");
        System.out.println("(3) Deposit");
        System.out.println("(4) Withdraw");
        System.out.println("(5) Transfer");
        System.out.println("(0) Exit\n");
    }

    private int getInput()
    {
        Scanner kb = new Scanner(System.in);
        int choice = -1;
        while(choice < 0 || choice > 5)
        {
            try
            {
                System.out.print("Enter your choice: ");
                choice = Integer.parseInt(kb.nextLine());
            }
            catch(NumberFormatException e)
            {
                System.out.println("Invalid selection. Please try again");
            }
        }

        return choice;
    }

    private void runMenu()
    {
        printHeader();

        while(!exit)
        {
            printMenu();

            int choice = getInput();
            performAction(choice);
        }
    }

    private void performAction(int c)
    {
        switch(c)
        {
            case 0:
                exit = true;
                System.out.println("Thank you for using our bank application");
                break;
            case 1:
                createAccount();
                break;
            case 2:
                accInfo();
                break;
            case 3:
                deposit();
                break;
            case 4:
                withdraw();
                break;
            case 5:
                transfer();
                break;
            default:
                System.out.println("Unknown error has occurred");
        }
    }

    private void createAccount()
    {
        Scanner n = new Scanner(System.in);
        System.out.print("Please enter your full name: ");
        String name = n.nextLine();

        Scanner acc = new Scanner(System.in);
        System.out.print("Please enter your starting balance: ");
        float accBal = Float.parseFloat(acc.nextLine());

        try(
                // Connection to SQL, adjust accordingly
                Connection conn = DriverManager.getConnection
                        ("jdbc:mysql://localhost:3306", "root", "");

                Statement stmt = conn.createStatement()
        )

        {
            String insertInfo = "insert into account(name_on_account, balance) values ('" + name + "'," + accBal + ");";
            stmt.executeUpdate(insertInfo);
            String success = "---Account successfully created---";
            System.out.println(success);

            String strSelect = "select * from account where name_on_account = '" + name + "' and balance = " + accBal + ";";
            ResultSet rs = stmt.executeQuery(strSelect);
            rs.next();

            int accNo = Integer.parseInt(rs.getString("account_no"));
            String nm = rs.getString("name_on_account");
            float bal = Float.parseFloat(rs.getString("balance"));
            String date = rs.getString("account_open_date");

            System.out.println("Account Number: " + accNo);
            System.out.println("Name: " + nm);
            System.out.println("Current Balance: " + bal);
            System.out.println("Date Opened: " + date);
            System.out.println("----------------------------------");
        }
        catch (SQLException ex)
        {
            System.out.println("Error has been made in creating your account");
            System.exit(-1);
        }
    }

    private int accInfo()
    {
        Scanner in = new Scanner(System.in);
        System.out.print("\nPlease enter your account number: ");
        int infoEntered = Integer.parseInt(in.nextLine());

        try(
                // Connection to SQL, adjust accordingly
                Connection conn = DriverManager.getConnection
                        ("jdbc:mysql://localhost:3306", "root", "");

                Statement stmt = conn.createStatement()
        )

        {
            String strSelect = "select * from account where account_no = " + infoEntered + ";";
            ResultSet rs = stmt.executeQuery(strSelect);
            rs.next();

            int accNo = Integer.parseInt(rs.getString("account_no"));
            String name = rs.getString("name_on_account");
            float bal = Float.parseFloat(rs.getString("balance"));
            String date = rs.getString("account_open_date");

            System.out.println("---Checking Account Balance---\n");
            System.out.println("Account Number: " + accNo);
            System.out.println("Name: " + name);
            System.out.println("Current Balance: " + bal);
            System.out.println("Date Opened: " + date);
            System.out.println("------------------------------");
        }
        catch (SQLException ex)
        {
            System.out.println("Invalid Account Number Entered");
            System.exit(-1);
        }

        return infoEntered;
    }

    private void accInfo(int num)
    {
        try (
                Connection conn = DriverManager.getConnection
                        ("jdbc:mysql://localhost:3306", "root", "");

                // Connection to SQL, adjust accordingly
                Statement stmt = conn.createStatement()
        )
        {
            String strSelect = "select * from account where account_no = " + num + ";";
            ResultSet rs = stmt.executeQuery(strSelect);
            rs.next();

            int accNo = Integer.parseInt(rs.getString("account_no"));
            String name = rs.getString("name_on_account");
            float bal = Float.parseFloat(rs.getString("balance"));
            String date = rs.getString("account_open_date");

            System.out.println("---Checking Account Balance---\n");
            System.out.println("Account Number: " + accNo);
            System.out.println("Name: " + name);
            System.out.println("Current Balance: " + bal);
            System.out.println("Date Opened: " + date);
            System.out.println("------------------------------");

            rs.close();
        }
        catch (Exception ex)
        {
            System.out.println("Invalid Account Number Entered");
            System.exit(-1);
        }
    }

    private void deposit()
    {
        try(
                Connection conn = DriverManager.getConnection
                        ("jdbc:mysql://localhost:3306", "root", "");

                // Connection to SQL, adjust accordingly
                Statement stmt = conn.createStatement()
        )
        {
            conn.setAutoCommit(false);

            Scanner in = new Scanner(System.in);
            System.out.print("\nPlease enter your account number: ");
            int accNo = Integer.parseInt(in.nextLine());

            String strUpdate = "select balance from account where account_no = " + accNo + " for update;";
            stmt.execute(strUpdate);

            accInfo(accNo);

            Scanner input = new Scanner(System.in);
            System.out.print("\nPlease Enter Deposit Amount: ");
            float deposit = Float.parseFloat(input.nextLine());

            if(deposit > 0)
            {
                String update = "update account set balance = balance + " + deposit + " where account_no = " + accNo + ";";
                stmt.executeUpdate(update);

                System.out.println("-----Deposit Successful-----\n");

                String strSelect = "select balance from account where account_no = " + accNo + ";";
                ResultSet rs = stmt.executeQuery(strSelect);
                rs.next();

                float bal = Float.parseFloat(rs.getString("balance"));
                System.out.println("Your new balance is " + bal);
                conn.commit();
            }
            else
            {
                System.out.println("Invalid Amount Entered");
            }
        }
        catch (Exception ex)
        {
            System.out.println("Invalid Account Number Entered");
            System.exit(-1);
        }
    }

    private void withdraw()
    {
        try(
                Connection conn = DriverManager.getConnection
                        ("jdbc:mysql://localhost:3306", "root", "");

                Statement stmt = conn.createStatement();
                Statement stmt2 = conn.createStatement();
        )
        {
            conn.setAutoCommit(false);

            Scanner in = new Scanner(System.in);
            System.out.print("\nPlease enter your account number: ");
            int accNo = Integer.parseInt(in.nextLine());

            String strUpdate = "select balance from account where account_no = " + accNo + " for update;";
            stmt.execute(strUpdate);

            accInfo(accNo);

            Scanner input = new Scanner(System.in);
            System.out.print("\nPlease Enter Withdrawal Amount: ");
            float withdrawal = Float.parseFloat(input.nextLine());

            String strCheck = "select balance from account where account_no = " + accNo + " for share;";
            ResultSet rs = stmt.executeQuery(strCheck);
            rs.next();

            float checkBal = Float.parseFloat(rs.getString("balance"));

            if(withdrawal > 0 && checkBal - withdrawal >= 0)
            {
                String update = "update account set balance = balance - " + withdrawal + " where account_no = " + accNo + ";";
                stmt.executeUpdate(update);

                System.out.println("-----Withdrawal Successful-----\n");

                ResultSet rs1 = stmt2.executeQuery(strCheck);
                rs1.next();

                float bal = Float.parseFloat(rs1.getString("balance"));

                System.out.println("Your new balance is " + bal);
                conn.commit();
            }
            else
            {
                System.out.println("Invalid Amount");
            }
        }
        catch (Exception ex)
        {
            System.out.println("Invalid Account Number Entered");
            System.exit(-1);
        }
    }

    private void transfer()
    {
        try (
                Connection conn = DriverManager.getConnection
                        ("jdbc:mysql://localhost:3306", "root", "");

                // Connection to SQL, adjust accordingly
                Statement stmt = conn.createStatement();
                Statement stmt1 = conn.createStatement();
                Statement stmt2 = conn.createStatement()
        )
        {
            conn.setAutoCommit(false);

            Scanner src = new Scanner(System.in);
            System.out.print("\nPlease Enter Source Account Number: ");
            int srcAcc = Integer.parseInt(src.nextLine());

            String srcUpdate = "select * from account where account_no = " + srcAcc + " for update;";
            stmt.execute(srcUpdate);
            accInfo(srcAcc);

            String strSelect = "select balance from account where account_no = " + srcAcc + " for share;";
            ResultSet rs = stmt.executeQuery(strSelect);
            rs.next();

            float srcBal = Float.parseFloat(rs.getString("balance"));

            Scanner dest = new Scanner(System.in);
            System.out.print("\nPlease Enter Target Account Number: ");
            int destAcc = Integer.parseInt(dest.nextLine());

            String destSelect = "select * from account where account_no = " + destAcc + " for update;";
            stmt.execute(destSelect);
            accInfo(destAcc);

            Scanner in = new Scanner(System.in);
            System.out.print("\nPlease Enter Transfer Amount: ");
            float amt = Float.parseFloat(in.nextLine());

            if(amt > srcBal)
            {
                System.out.println("Insufficient Funds");
            }
            else
            {
                String withdraw = "update account set balance = balance - " + amt + " where account_no = " + srcAcc + ";";
                stmt.executeUpdate(withdraw);
                Thread.sleep(10000);

                String deposit = "update account set balance = balance + " + amt + " where account_no = " + destAcc + ";";
                stmt.executeUpdate(deposit);
                Thread.sleep(10000);

                conn.commit();
                System.out.println("\nConfirmation: Funds Successfully Transferred\n");

                strSelect = "select balance from account where account_no = " + srcAcc + ";";
                ResultSet rs1 = stmt1.executeQuery(strSelect);
                rs1.next();

                destSelect = "select balance from account where account_no = " + destAcc + ";";
                ResultSet rs2 = stmt2.executeQuery(destSelect);
                rs2.next();

                srcBal = Float.parseFloat(rs1.getString("balance"));
                float destBal = Float.parseFloat(rs2.getString("balance"));

                System.out.println("Src Account Bal: " + srcBal);
                System.out.println("Dest Account Bal: " + destBal);
            }
        }
        catch (Exception ex)
        {
            System.out.println("Invalid Account Number Entered");
            return;
        }
    }

    public static void main(String[] args)
    {
        BankingApplication menu = new BankingApplication();
        menu.runMenu();
    }
}
