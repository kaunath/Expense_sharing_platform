import java.util.*;


class Pair{
    double amount;
    String userId;

    public Pair(double amount, String userId) {
        this.amount = amount;
        this.userId = userId;
    }

}

public class ExpenseManager {
    List<Expense> expenses;
    Map<String, User> userMap;
    Map<String, Map<String, Double> > balanceSheet;

    public ExpenseManager() {
        expenses = new ArrayList<Expense>();
        userMap = new HashMap<String, User>();
        balanceSheet = new HashMap<String, Map<String, Double>>();
    }

    public void addUser(User user) {
        userMap.put(user.getId(), user);
        balanceSheet.put(user.getId(), new HashMap<String, Double>());
    }

    public Map<String, User> getUsersList()
    {
        return userMap;
    }
    public void addExpense(ExpenseType expenseType, double amount, String paidBy, List<Split> splits, ExpenseMetadata expenseMetadata) {
        Expense expense = ExpenseService.createExpense(expenseType, amount, userMap.get(paidBy), splits, expenseMetadata);
        expenses.add(expense);
        for (Split split : expense.getSplits()) {
            String paidTo = split.getUser().getId();
            Map<String, Double> balances = balanceSheet.get(paidBy);
            if (!balances.containsKey(paidTo)) {
                balances.put(paidTo, 0.0);
            }
            balances.put(paidTo, balances.get(paidTo) + split.getAmount());

            balances = balanceSheet.get(paidTo);
            if (!balances.containsKey(paidBy)) {
                balances.put(paidBy, 0.0);
            }
            balances.put(paidBy, balances.get(paidBy) - split.getAmount());

            //System.out.println(balances);
        }
    }

    public void showBalance(String userId) {
        boolean isEmpty = true;
        for (Map.Entry<String, Double> userBalance : balanceSheet.get(userId).entrySet()) {
            if (userBalance.getValue() != 0) {
                isEmpty = false;
                printBalance(userId, userBalance.getKey(), userBalance.getValue());
            }
        }

        if (isEmpty) {
            System.out.println("No balances");
        }
    }

    public void showBalances() {
        boolean isEmpty = true;
        for (Map.Entry<String, Map<String, Double>> allBalances : balanceSheet.entrySet()) {
            for (Map.Entry<String, Double> userBalance : allBalances.getValue().entrySet()) {
                if (userBalance.getValue() > 0) {
                    isEmpty = false;
                    printBalance(allBalances.getKey(), userBalance.getKey(), userBalance.getValue());
                }
            }
        }

        if (isEmpty) {
            System.out.println("No balances");
        }
    }

    private void printBalance(String user1, String user2, double amount) {
        String user1Name = userMap.get(user1).getName();
        String user2Name = userMap.get(user2).getName();
        if (amount < 0) {
            System.out.println(user1Name + " owes " + user2Name + ": " + amount);
        } else if (amount > 0) {
            System.out.println(user2Name + " owes " + user1Name + ": " + amount);
        }
    }

    public void printBalanceSheet()
    {
        System.out.println(this.balanceSheet);
    }

    public void simplify()
    {
        PriorityQueue<Pair> positive = new PriorityQueue<Pair>(new Comparator<Pair>() {
            @Override
            public int compare(Pair p1, Pair p2) {
                if(p1.amount < p2.amount)
                    return 1;
                else if(p1.amount > p2.amount)
                    return -1;
                else
                    return 0;
            }
        });
        PriorityQueue<Pair> negative = new PriorityQueue<Pair>(new Comparator<Pair>() {
            @Override
            public int compare(Pair p1, Pair p2) {
                if(p1.amount < p2.amount)
                    return 1;
                else if(p1.amount > p2.amount)
                    return -1;
                else
                    return 0;
            }
        });

        HashMap<String, Double>cur_balance = new HashMap<String, Double>();
        // O(E)
        for (Map.Entry<String, Map<String, Double>> usermap : balanceSheet.entrySet()) {
            String user = usermap.getKey();
            // ..
            for (Map.Entry<String, Double> nameEntry : usermap.getValue().entrySet()) {
                String name = nameEntry.getKey();
                Double amount = nameEntry.getValue();

                cur_balance.put(name, cur_balance.getOrDefault(name, 0.0) + amount);
                // ...
            }
        }
        //O(N) Build max-heap
        for(Map.Entry<String, Double> p : cur_balance.entrySet())
        {
            String name = p.getKey();
            Double amount = p.getValue();

            if(amount > 0.0)
            {
                positive.add(new Pair(amount, name));
            }
            else
            {
                negative.add(new Pair(-1*amount, name));
            }
        }

        //Negative Heap contains all receivers pairs
        //Positive Heap contains all sender pairs

        // O(N) * O(log N) = O(N log N) Worst case
        while(positive.size() > 0 && negative.size() > 0)
        {
            Pair sender = positive.poll();
            Pair receiver = negative.poll();

            String payee = userMap.get(sender.userId).getName();
            String payer = userMap.get(receiver.userId).getName();
            System.out.println( payee + " --> "+ Math.min(sender.amount, receiver.amount)+" -->" + payer);

            if(sender.amount > receiver.amount)
                positive.add(new Pair(sender.amount - receiver.amount, sender.userId));
            else if(receiver.amount > sender.amount)
                negative.add(new Pair(receiver.amount - sender.amount, receiver.userId));
        }
    }
}