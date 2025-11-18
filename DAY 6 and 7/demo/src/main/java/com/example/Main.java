package com.example;


import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
    // initializing the activeuser as -1
    static int activeUserId = -1;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        while (true) {
            printMenu();
            // get choice as input from user, and access the specified features. InputmismatchException is also executed.
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a valid number.");
                scanner.nextLine();
                System.out.println();
                continue;
            }
            switch (choice) {
                case 1:
                    selectUser(scanner);
                    break;
                case 2:
                    createUser(scanner);
                    break;
                case 3:
                    createGroup(scanner);
                    break;
                case 4:
                    addUserToGroup(scanner);
                    break;
                case 5:
                    addExpense(scanner);
                    break;
                case 6:
                    viewMyGroups(scanner);
                    break;
                case 7:
                    viewGroupBalances(scanner);
                    break;
                case 8:
                    settleUp(scanner);
                    break;
                case 9:
                    exitUserFromGroup(scanner);
                    break;
                default:
                    System.out.println("Invalid choice, try again. Thank you!!");
                    return;
            }
        }
    }


    static void printMenu() {
        System.out.println();
        System.out.println("Expense Sharing App:");
        System.out.println("1. Select User");
        System.out.println("2. Create User");
        System.out.println("3. Create Group");
        System.out.println("4. Add User to Group");
        System.out.println("5. Add Expense");
        System.out.println("6. View My Groups");
        System.out.println("7. View Group Balances");
        System.out.println("8. Settle Up");
        System.out.println("9. Exit From Group");
        System.out.println();
        System.out.print("Choose an option: ");
    }


    // Select User
    static void selectUser(Scanner sc) {
        try (Connection conn = DBUtil.getConnection()) {
            // printing thelist of users
            try (PreparedStatement ps = conn.prepareStatement("SELECT id, username, name FROM users");
                    ResultSet rs = ps.executeQuery()) {
                System.out.println();
                System.out.println("List of users:");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String name = rs.getString("name");
                    System.out.println(id + ": " + username + " (" + name + ")");
                }
            }
            // getting the user id to select
            while (true) {
                System.out.println();
                System.out.print("Enter user ID to select: ");
                int selectedId;
                try {
                    selectedId = sc.nextInt();
                    sc.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input, please enter a valid number.");
                    sc.nextLine();
                    continue;
                }


                if (selectedId == activeUserId) {
                    System.out.println("User already selected. Please select a different user.");
                    continue;
                }


                try (PreparedStatement check = conn.prepareStatement("SELECT name FROM users WHERE id = ?")) {
                    check.setInt(1, selectedId);
                    try (ResultSet rsCheck = check.executeQuery()) {
                        if (rsCheck.next()) {
                            String userName = rsCheck.getString("name");
                            activeUserId = selectedId;
                            System.out.println("User " + userName + " selected successfully.");
                            break;
                        } else {
                            System.out.println("Invalid user ID. Try again.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Create User
    static void createUser(Scanner sc) {
        // get name and check the validations
        String name;
        while (true) {
            System.out.print("Enter name: ");
            name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty. Please enter again.");
                continue;
            } else if (name.matches("[a-zA-Z ]+")) {
                break;
            } else {
                System.out.println("Invalid input. Name must be only in alphabets");
                continue;
            }
        }
        // get mobile number with validations
        String mobile;
        while (true) {
            System.out.print("Enter mobile number: ");
            mobile = sc.nextLine().trim();
            if (mobile.isEmpty()) {
                System.out.println("Mobile number cannot be empty. Please enter again.");
                continue;
            }
            if (!mobile.matches("\\d{10}")) {
                System.out.println("Mobile number must be 10 digits.");
                continue;
            }
            break;
        }
        // get username with validations
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = sc.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please enter again.");
                continue;
            }
            if (username.length() > 20) {
                System.out.println("Username cannot exceed 20 characters. Please enter a shorter username.");
                continue;
            }
            if (!username.matches("[A-Za-z0-9_@]{1,20}")) {
                System.out.println(
                        "Username must be up to 20 characters and can include only letters, digits, and underscores.");
                continue;
            }
            try (Connection conn = DBUtil.getConnection();
                    PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {
                check.setString(1, username);
                ResultSet rs = check.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    System.out.println("Username not available. Please type a different username.");
                } else
                    break;
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }
        // insert into DB
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement insert = conn
                        .prepareStatement("INSERT INTO users(name, mobile, username) VALUES (?, ?, ?)")) {
            insert.setString(1, name);
            insert.setString(2, mobile);
            insert.setString(3, username);
            insert.executeUpdate();
            System.out.println("User " + name + "created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Create Group
    static void createGroup(Scanner sc) {
        if (activeUserId == -1) {
            System.out.println("Select the user from the below list.");
            selectUser(sc);
        }
        // get grpname from user as input,with validations
        String groupName;
        while (true) {
            System.out.print("Enter group name: ");
            groupName = sc.nextLine().trim();
            if (groupName.isEmpty()) {
                System.out.println("Group name cannot be empty.");
                continue;
            }
            if (groupName.length() > 30) {
                System.out.println("Group name cannot exceed 30 characters. Please enter a shorter name.");
                continue;
            }
            break;
        }
        // insert the grp name into db
        String insertSQL = "INSERT INTO `groups` (name, created_by) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, groupName);
            ps.setInt(2, activeUserId);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    System.out.println("Group created successfully with Name: " + groupName);
                } else {
                    System.out.println("Group created but cannot retrieve group ID.");
                }
            } else {
                System.out.println("Group creation failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Add User To Group
    static void addUserToGroup(Scanner sc) {
        // initialize grpud as -1
        int groupId = -1;
        viewMyGroups(sc);
        // get grpid from user as input and give validations
        while (true) {
            System.out.print("Enter Group ID to add user to: ");
            try {
                groupId = sc.nextInt();
                sc.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer for Group ID.");
                sc.nextLine();
            }
        }
        // check if the grp exists
        try (Connection conn = DBUtil.getConnection()) {
            try (PreparedStatement checkGroup = conn.prepareStatement("SELECT COUNT(*) FROM `groups` WHERE id = ?")) {
                checkGroup.setInt(1, groupId);
                try (ResultSet rsCheck = checkGroup.executeQuery()) {
                    rsCheck.next();
                    if (rsCheck.getInt(1) == 0) {
                        System.out.println("Invalid Group ID. Group does not exist.");
                        return;
                    }
                }
            }
            // get the list of users not in the grp from the db
            String sql = "SELECT id, username, name FROM users WHERE id NOT IN (SELECT user_id FROM group_members WHERE group_id = ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, groupId);
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("Users NOT in group " + groupId + ":");
                    while (rs.next()) {
                        int userId = rs.getInt("id");
                        String username = rs.getString("username");
                        String name = rs.getString("name");
                        System.out.println(userId + ": " + username + " (" + name + ")");
                    }
                }
            }
            // inistialize userid as -1 and then get it from user as input and check for
            // validatiosn
            int userIdToAdd = -1;
            while (true) {
                System.out.print("Enter User ID to add to group: ");
                try {
                    userIdToAdd = sc.nextInt();
                    sc.nextLine();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer for User ID.");
                    sc.nextLine();
                }
            }


            // check if such user exists in db
            try (PreparedStatement checkUserExists = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE id = ?")) {
                checkUserExists.setInt(1, userIdToAdd);
                try (ResultSet rsUser = checkUserExists.executeQuery()) {
                    rsUser.next();
                    if (rsUser.getInt(1) == 0) {
                        System.out.println("Invalid user ID.");
                        return;
                    }
                }
            }


            // check if user already exists in group
            try (PreparedStatement check = conn.prepareStatement(
                    "SELECT COUNT(*) FROM group_members WHERE group_id = ? AND user_id = ?")) {
                check.setInt(1, groupId);
                check.setInt(2, userIdToAdd);
                try (ResultSet checkRs = check.executeQuery()) {
                    checkRs.next();
                    if (checkRs.getInt(1) > 0) {
                        System.out.println("User already exists in the group.");
                        return;
                    }
                }
            }


            // insert user into group
            try (PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO group_members(group_id, user_id) VALUES (?, ?)")) {
                insert.setInt(1, groupId);
                insert.setInt(2, userIdToAdd);
                int rows = insert.executeUpdate();
                if (rows == 1)
                    System.out.println("User added to group successfully.");
                else
                    System.out.println("Failed to add user to group.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Add Expense
    static void addExpense(Scanner sc) {
        if (activeUserId == -1) {
            System.out.println("Select the user from the below list.");
            selectUser(sc);
        }
        // view the activeuser's grp
        viewMyGroups(sc);
        // enter the grpid to add expense
        try (Connection conn = DBUtil.getConnection()) {
            System.out.print("Enter group ID to add expense: ");
            int groupId = sc.nextInt();
            sc.nextLine();
            // check if the active user is there in the grp
            try (PreparedStatement checkGroup = conn.prepareStatement(
                    "SELECT COUNT(*) FROM group_members WHERE group_id = ? AND user_id = ?")) {
                checkGroup.setInt(1, groupId);
                checkGroup.setInt(2, activeUserId);
                ResultSet rs = checkGroup.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    System.out.println("You are not a member of this group.");
                    return;
                }
            }
            // enter the exoense amount and check for validations
            double totalAmount = 0.0;
            while (true) {
                System.out.print("Enter total expense amount: ");
                String input = sc.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Total expense amount cannot be empty. Please enter a valid number.");
                    continue;
                }
                try {
                    totalAmount = Double.parseDouble(input);
                    if (totalAmount <= 0) {
                        System.out.println("Amount must be greater than zero. Please enter again.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number for total expense.");
                }
            }
            // enter the description if the expense amount
            System.out.print("Enter expense description: ");
            String description = sc.nextLine();
            // insert the xpense amount into the db
            String insertExpenseSQL = "INSERT INTO expenses (group_id, paid_by, amount, description) VALUES (?, ?, ?, ?)";
            int expenseId;
            try (PreparedStatement ps = conn.prepareStatement(insertExpenseSQL,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, groupId);
                ps.setInt(2, activeUserId);
                ps.setDouble(3, totalAmount);
                ps.setString(4, description);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) {
                    System.out.println("Failed to create expense.");
                    return;
                }
                expenseId = keys.getInt(1);
            }
            // get the input like how to split the amt
            System.out.println("Select split type: 1. EQUAL  2. EXACT  3. PERCENT");
            int splitType = sc.nextInt();
            sc.nextLine();


            try (PreparedStatement psGroupMembers = conn.prepareStatement(
                    "SELECT user_id FROM group_members WHERE group_id = ?")) {
                psGroupMembers.setInt(1, groupId);
                ResultSet membersRs = psGroupMembers.executeQuery();
                // store the grp member id into a list and fetch their names in a hashmap
                List<Integer> memberIds = new ArrayList<>();
                while (membersRs.next())
                    memberIds.add(membersRs.getInt("user_id"));


                Map<Integer, String> userNames = new HashMap<>();
                if (!memberIds.isEmpty()) {
                    String inClause = memberIds.stream().map(id -> "?").collect(Collectors.joining(","));
                    String sqlGetNames = "SELECT id, name FROM users WHERE id IN (" + inClause + ")";
                    try (PreparedStatement psNames = conn.prepareStatement(sqlGetNames)) {
                        for (int i = 0; i < memberIds.size(); i++) {
                            psNames.setInt(i + 1, memberIds.get(i));
                        }
                        try (ResultSet rsNames = psNames.executeQuery()) {
                            while (rsNames.next()) {
                                userNames.put(rsNames.getInt("id"), rsNames.getString("name"));
                            }
                        }
                    }
                }
                // insert the values into db
                String insertSplitSQL = "INSERT INTO expense_splits (expense_id, user_id, share) VALUES (?, ?, ?)";
                try (PreparedStatement insertSplitStmt = conn.prepareStatement(insertSplitSQL)) {
                    double totalSplitAmount = 0.0;
                    List<Double> shares = new ArrayList<>();
                    // split using the given choice
                    for (int userId : memberIds) {
                        double shareAmount = 0.0;
                        String userName = userNames.getOrDefault(userId, "Unknown User");
                        if (splitType == 1) {
                            shareAmount = totalAmount / memberIds.size();
                            System.out.printf("%s's share: %.2f%n", userName, shareAmount);
                        } else if (splitType == 2) {
                            System.out.printf("Enter exact share for %s: ", userName);
                            shareAmount = sc.nextDouble();
                            sc.nextLine();
                        } else if (splitType == 3) {
                            System.out.printf("Enter percent share for %s: ", userName);
                            double percent = sc.nextDouble();
                            sc.nextLine();
                            shareAmount = (percent / 100.0) * totalAmount;
                        }
                        totalSplitAmount += shareAmount;
                        insertSplitStmt.setInt(1, expenseId);
                        insertSplitStmt.setInt(2, userId);
                        insertSplitStmt.setDouble(3, shareAmount);
                        insertSplitStmt.addBatch();
                        shares.add(shareAmount);
                    }


                    if (Math.abs(totalSplitAmount - totalAmount) > 0.01) {
                        System.out.println("Error: The total splits do not match total amount.");
                        return;
                    }
                    insertSplitStmt.executeBatch();
                    updateBalances(conn, groupId, activeUserId, memberIds, shares);
                }
            }
            System.out.println("Expense added and splits recorded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Update Balance
    static void updateBalances(Connection conn, int groupId, int paidByUserId,
            List<Integer> memberIds, List<Double> shares) throws SQLException {
        String selectBalanceSQL = "SELECT amount FROM balances WHERE group_id = ? AND user_id = ? AND owes_to = ?";
        String insertBalanceSQL = "INSERT INTO balances (group_id, user_id, owes_to, amount) VALUES (?, ?, ?, ?)";
        String updateBalanceSQL = "UPDATE balances SET amount = amount + ? WHERE group_id = ? AND user_id = ? AND owes_to = ?";
        // check if userid and paying person are same, if yes no updation done
        for (int i = 0; i < memberIds.size(); i++) {
            int userId = memberIds.get(i);
            double shareAmount = shares.get(i);
            if (userId == paidByUserId)
                continue;
            // if no select and update the amount
            try (PreparedStatement selectStmt = conn.prepareStatement(selectBalanceSQL)) {
                selectStmt.setInt(1, groupId);
                selectStmt.setInt(2, userId);
                selectStmt.setInt(3, paidByUserId);
                ResultSet rs = selectStmt.executeQuery();


                if (rs.next()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSQL)) {
                        updateStmt.setDouble(1, shareAmount);
                        updateStmt.setInt(2, groupId);
                        updateStmt.setInt(3, userId);
                        updateStmt.setInt(4, paidByUserId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertBalanceSQL)) {
                        insertStmt.setInt(1, groupId);
                        insertStmt.setInt(2, userId);
                        insertStmt.setInt(3, paidByUserId);
                        insertStmt.setDouble(4, shareAmount);
                        insertStmt.executeUpdate();
                    }
                }
            }
            netMutualBalances(conn, groupId, userId, paidByUserId);
        }
    }


    // Remove mutual balance
    static void netMutualBalances(Connection conn, int groupId, int userA, int userB) throws SQLException {
        String sql = "SELECT amount FROM balances WHERE group_id=? AND user_id=? AND owes_to=?";
        double ab = 0.0, ba = 0.0;
        boolean abExists = false, baExists = false;
        // get the details how much a has to pay b
        try (PreparedStatement ps1 = conn.prepareStatement(sql)) {
            ps1.setInt(1, groupId);
            ps1.setInt(2, userA);
            ps1.setInt(3, userB);
            try (ResultSet rsa = ps1.executeQuery()) {
                if (rsa.next()) {
                    ab = rsa.getDouble(1);
                    abExists = true;
                }
            }
        }
        // get the details how much b has to pay a
        try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
            ps2.setInt(1, groupId);
            ps2.setInt(2, userB);
            ps2.setInt(3, userA);
            try (ResultSet rsb = ps2.executeQuery()) {
                if (rsb.next()) {
                    ba = rsb.getDouble(1);
                    baExists = true;
                }
            }
        }
        // if only one user had to pay he other, the updation not done
        if ((abExists && !baExists && ba == 0.0) || (!abExists && baExists && ab == 0.0)) {
            return;
        }
        // if both has topay some amt to each other, then both the initial records are
        // deleted
        try (PreparedStatement psDelete = conn.prepareStatement(
                "DELETE FROM balances WHERE group_id=? AND ((user_id=? AND owes_to=?) OR (user_id=? AND owes_to=?))")) {
            psDelete.setInt(1, groupId);
            psDelete.setInt(2, userA);
            psDelete.setInt(3, userB);
            psDelete.setInt(4, userB);
            psDelete.setInt(5, userA);
            psDelete.executeUpdate();
        }
        // find the net amt and user
        double net = ba - ab;
        if (Math.abs(net) >= 0.01) {
            int netUser, netOwesTo;
            if (net > 0) {
                netUser = userB;
                netOwesTo = userA;
            } else {
                netUser = userA;
                netOwesTo = userB;
                net = -net;
            }
            // insert the data into db
            try (PreparedStatement psInsert = conn
                    .prepareStatement("INSERT INTO balances(group_id, user_id, owes_to, amount) VALUES (?, ?, ?, ?)")) {
                psInsert.setInt(1, groupId);
                psInsert.setInt(2, netUser);
                psInsert.setInt(3, netOwesTo);
                psInsert.setDouble(4, net);
                psInsert.executeUpdate();
            }
        }
    }


    // View my groups
    static void viewMyGroups(Scanner sc) {
        if (activeUserId == -1) {
            System.out.println("Select the user from the below list.");
            selectUser(sc);
        }
        // get the grps created by active users, grps in ehich activeuser is there and
        // those grp was created by usernames
        String sql = "SELECT g.id, g.name, u.username AS creator FROM `groups` g JOIN users u ON g.created_by = u.id WHERE g.created_by = ? "
                +
                "UNION SELECT g.id, g.name, u.username AS creator FROM `groups` g " +
                "JOIN group_members gm ON g.id = gm.group_id JOIN users u ON g.created_by = u.id WHERE gm.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activeUserId);
            ps.setInt(2, activeUserId);
            ResultSet rs = ps.executeQuery();
            // print the grps
            System.out.println("Your Groups:");
            boolean hasGroups = false;
            while (rs.next()) {
                hasGroups = true;
                int groupId = rs.getInt("id");
                String groupName = rs.getString("name");
                String creator = rs.getString("creator");
                System.out.println(groupId + ": " + groupName + " (Created by: " + creator + ")");
            }
            if (!hasGroups)
                System.out.println("You do not belong to any groups yet.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // view group balance
    static void viewGroupBalances(Scanner sc) {
        viewMyGroups(sc);
        System.out.print("Enter group ID to view balances: ");
        int groupId = sc.nextInt();
        sc.nextLine();
        // get the details fromthe specified grp like, which user owes howmuch amount to
        // whom
        String sql = "SELECT u1.username AS user, u2.username AS owes_to, b.amount " +
                "FROM balances b JOIN users u1 ON b.user_id = u1.id " +
                "JOIN users u2 ON b.owes_to = u2.id WHERE b.group_id = ? AND b.amount > 0";
        boolean hasDues = false;
        // print the balance amt, if none no pending
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            System.out.println("Group Balances:");
            while (rs.next()) {
                hasDues = true;
                String user = rs.getString("user");
                String owesTo = rs.getString("owes_to");
                double amount = rs.getDouble("amount");
                System.out.printf("%s owes %s: %.2f%n", user, owesTo, amount);
            }
            if (!hasDues)
                System.out.println("No pending dues.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // settle amt
    static void settleUp(Scanner sc) {
        if (activeUserId == -1) {
            System.out.println("Select the user from the below list.");
            selectUser(sc);
        }
        viewMyGroups(sc);
        System.out.print("Enter Group ID: ");
        int groupId = sc.nextInt();
        sc.nextLine();
        // fetch the details of the amt to be paid by the active user and to whom to pay
        try (Connection conn = DBUtil.getConnection()) {
            String fetchOwesSQL = "SELECT u.id, u.username, b.amount FROM balances b " +
                    "JOIN users u ON b.owes_to = u.id " +
                    "WHERE b.group_id = ? AND b.user_id = ? AND b.amount > 0";
            try (PreparedStatement ps = conn.prepareStatement(fetchOwesSQL)) {
                ps.setInt(1, groupId);
                ps.setInt(2, activeUserId);
                ResultSet rs = ps.executeQuery();
                // check if the cursor is before the first row
                if (!rs.isBeforeFirst()) {
                    System.out.println("You have no pending dues in this group.");
                    return;
                }
                // store the user details in list, who to pay and to whom to pay
                System.out.println("Users you owe money to:");
                List<Integer> owesToIds = new ArrayList<>();
                List<Double> owesToAmounts = new ArrayList<>();
                // print the amt and the user details to pay
                while (rs.next()) {
                    int userId = rs.getInt("id");
                    String username = rs.getString("username");
                    double amount = rs.getDouble("amount");
                    System.out.printf("%d: %s - Owes: %.2f%n", userId, username, amount);
                    owesToIds.add(userId);
                    owesToAmounts.add(amount);
                }
                
                System.out.print("Enter the User ID to settle with: ");
                int settleUserId = sc.nextInt();
                sc.nextLine();


                int index = owesToIds.indexOf(settleUserId);
                if (index == -1) {
                    System.out.println("Invalid User ID or no pending dues to this user.");
                    return;
                }
                // enter the amt to settle and check the validations
                double maxAmount = owesToAmounts.get(index);
                System.out.printf("Enter amount to settle (max %.2f): ", maxAmount);
                double settleAmount = sc.nextDouble();
                sc.nextLine();


                if (settleAmount <= 0) {
                    System.out.println("Amount must be greater than zero.");
                    return;
                } else if (settleAmount > maxAmount) {
                    System.out.println("Cannot settle more than pending amount.");
                    return;
                } else {
                    // update the amt settled in the db
                    String updateBalanceSQL = "UPDATE balances SET amount = amount - ? WHERE group_id = ? AND user_id = ? AND owes_to = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSQL)) {
                        updateStmt.setDouble(1, settleAmount);
                        updateStmt.setInt(2, groupId);
                        updateStmt.setInt(3, activeUserId);
                        updateStmt.setInt(4, settleUserId);
                        int rowsUpdated = updateStmt.executeUpdate();
                        if (rowsUpdated == 1)
                            System.out.println("Settlement successful.");
                        else
                            System.out.println("Settlement failed.");
                    }
                }
                netMutualBalances(conn, groupId, activeUserId, settleUserId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // exit user
    static void exitUserFromGroup(Scanner sc) {
        if (activeUserId == -1) {
            System.out.println("Select the user from the below list.");
            selectUser(sc);
        }
        viewMyGroups(sc);
        int groupId;
        while(true){
            System.out.print("Enter Group Id to exit:");
            try {
                groupId = sc.nextInt();
                sc.nextLine();
                break;
            }catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer for Group ID.");
                sc.nextLine();
            }
        }
        // check if user has pending amt to settle in the grp
        try (Connection conn = DBUtil.getConnection()) {
            boolean hasBalance = true;
            while (hasBalance == true) {
                //from the balance table, the amt to be paid by the activeuser is checked if it is > 0.
                String pendingBalanceSQL = "SELECT amount FROM balances WHERE group_id = ? AND user_id = ? AND amount > 0";
                try (PreparedStatement ps = conn.prepareStatement(pendingBalanceSQL)) {
                    ps.setInt(1, groupId);
                    ps.setInt(2, activeUserId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("You have pending balances in this group. Please settle them before exiting.");
                            System.out.println("To view your balance Enter your Group ID");
                            settleUp(sc);
                            continue;


                        } else {
                            hasBalance = false;
                        }
                    }
                }
            }
            // check if anyone in grp need to pay to active user
            //from the balance table, the amt to be paid by the activeuser is checked if it is > 0.
            String owedToUserSQL = "SELECT amount FROM balances WHERE group_id = ? AND owes_to = ? AND amount > 0";
            try (PreparedStatement ps2 = conn.prepareStatement(owedToUserSQL)) {
                ps2.setInt(1, groupId);
                ps2.setInt(2, activeUserId);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) {
                        System.out.println("Others owe you money in this group. Please collect it or adjust balances before exiting.");
                        return;
                    }
                }
            }
            // delete the user from the grp i.e., delete from grpmembers table the userid using the grpid
            String deleteMemberSQL = "DELETE FROM group_members WHERE group_id = ? AND user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteMemberSQL)) {
                ps.setInt(1, groupId);
                ps.setInt(2, activeUserId);
                int rowsDeleted = ps.executeUpdate();
                if (rowsDeleted == 1) {
                    System.out.println("You have exited the group successfully.");
                } else {
                    System.out.println("You are not a member of this group or nothing to delete.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
