import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) {
        ExpenseManager expenseManager = new ExpenseManager();

        expenseManager.addUser(new User("u1", "sanghmitr", "sanghmitr@gmail.com", "9876543210"));
        expenseManager.addUser(new User("u2", "naman", "naman@gmail.com", "9876543210"));
        expenseManager.addUser(new User("u3", "shadrul", "shadrul@gmail.com", "9876543210"));
        expenseManager.addUser(new User("u4", "ishan", "ishan@gmail.com", "9876543210"));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            String[] commands = command.split(" ");
            String commandType = commands[0];

            switch (commandType) {
                case "SHOW":
                    if (commands.length == 1) {
                        expenseManager.showBalances();
//                        expenseManager.printBalanceSheet();
//                        expenseManager.simplify();
                    } else {
                        expenseManager.showBalance(commands[1]);
//                        expenseManager.printBalanceSheet();
//                        expenseManager.simplify();
                    }
                    break;
                case "EXPENSE":
                    String paidBy = commands[1];
                    Double amount = Double.parseDouble(commands[2]);
                    int noOfUsers = Integer.parseInt(commands[3]);
                    String expenseType = commands[4 + noOfUsers];
                    List<Split> splits = new ArrayList<>();
                    switch (expenseType) {
                        case "EQUAL":
                            for (int i = 0; i < noOfUsers; i++) {
                                splits.add(new EqualSplit(expenseManager.userMap.get(commands[4 + i])));
                            }
                            expenseManager.addExpense(ExpenseType.EQUAL, amount, paidBy, splits, null);
                            break;
                        case "EXACT":
                            for (int i = 0; i < noOfUsers; i++) {
                                splits.add(new ExactSplit(expenseManager.userMap.get(commands[4 + i]), Double.parseDouble(commands[5 + noOfUsers + i])));
                            }
                            expenseManager.addExpense(ExpenseType.EXACT, amount, paidBy, splits, null);
                            break;
                        case "PERCENT":
                            for (int i = 0; i < noOfUsers; i++) {
                                splits.add(new PercentSplit(expenseManager.userMap.get(commands[4 + i]), Double.parseDouble(commands[5 + noOfUsers + i])));
                            }
                            expenseManager.addExpense(ExpenseType.PERCENT, amount, paidBy, splits, null);
                            break;
                    }
                    break;

                case "SIMPLIFY":
                    expenseManager.simplify();
                    break;
                case "ADD_MEMBER":
                    String uid = commands[1], uname = commands[2], email = commands[3], phone = commands[4];

                    Map<String, User> userMap = expenseManager.getUsersList();
                    if(!userMap.containsKey(uid)) {
                        expenseManager.addUser(new User(uid, uname, email, phone));
                        System.out.println("User added successfully");
                    }
                    else{
                        System.out.println("UserId already exists");
                    }
                    break;
            }
        }
    }
}

/*

SHOW
SHOW u1
EXPENSE u1 1000 4 u1 u2 u3 u4 EQUAL
SHOW u4
SHOW u1
EXPENSE u1 1250 2 u2 u3 EXACT 370 880
SHOW
EXPENSE u4 1200 4 u1 u2 u3 u4 PERCENT 40 20 20 20
SHOW u1
SHOW


*/