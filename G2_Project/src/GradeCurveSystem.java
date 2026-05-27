
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * G9 期末作品：成績曲線調整與當掉人數模擬系統
 *
 * 功能重點：
 * 1. 教授可先輸入本班人數，再逐筆輸入每位學生原始成績。
 * 2. 可依指定「當掉百分比」自動調整整班成績。
 * 3. 可依指定「當掉人數」自動調整整班成績。
 * 4. 可將調整後成績重設回原始成績。
 * 5. 可依座號個別修改學生原始成績。
 * 6. 可清空所有班級資料與統計結果，而且不退出系統。
 * 7. 可顯示成績調整公式與計算邏輯。
 * 8. 提供顯示、搜尋、排序、統計等功能。
 */
public class GradeCurveSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final GradeBook gradeBook = new GradeBook("基礎程式設計");

    public static void main(String[] args) {
        runSystem();
    }

    private static void runSystem() {
        int choice;

        do {
            printMenu();
            choice = readInt("請輸入功能編號：");
            System.out.println();

            switch (choice) {
                case 1:
                    createClassByInput();
                    break;
                case 2:
                    createSampleClass();
                    break;
                case 3:
                    gradeBook.printAllStudents();
                    break;
                case 4:
                    gradeBook.sortBySeatNumberAscending();
                    System.out.println("已依座號由小到大排序。\n");
                    gradeBook.printAllStudents();
                    break;
                case 5:
                    gradeBook.sortByOriginalScoreDescending();
                    System.out.println("已依原始成績由高到低排序。\n");
                    gradeBook.printAllStudents();
                    break;
                case 6:
                    gradeBook.sortByAdjustedScoreDescending();
                    System.out.println("已依調整後成績由高到低排序。\n");
                    gradeBook.printAllStudents();
                    break;
                case 7:
                    searchStudentBySeatNumber();
                    break;
                case 8:
                    adjustByFailPercent();
                    break;
                case 9:
                    adjustByFailCount();
                    break;
                case 10:
                    gradeBook.printStatistics();
                    break;
                case 11:
                    gradeBook.resetAdjustedScores();
                    System.out.println("已將調整後成績全部重設為原始成績。\n");
                    break;
                case 12:
                    updateOneStudentOriginalScore();
                    break;
                case 13:
                    resetAllDataWithoutExit();
                    break;
                case 14:
                    printAdjustFormula();
                    break;
                case 0:
                    System.out.println("系統結束，謝謝使用。");
                    break;
                default:
                    System.out.println("輸入錯誤，請重新輸入。\n");
            }
        } while (choice != 0);
    }

    private static void printMenu() {
        System.out.println("============================================");
        System.out.println("      成績曲線調整與當掉人數模擬系統");
        System.out.println("============================================");
        System.out.println("1. 建立班級資料（輸入本班人數與原始成績）");
        System.out.println("2. 建立範例班級資料（100人）");
        System.out.println("3. 顯示全部成績");
        System.out.println("4. 依座號排序");
        System.out.println("5. 依原始成績排序");
        System.out.println("6. 依調整後成績排序");
        System.out.println("7. 依座號搜尋學生");
        System.out.println("8. 依當掉百分比調整成績");
        System.out.println("9. 依當掉人數調整成績");
        System.out.println("10. 顯示統計資訊");
        System.out.println("11. 將調整後成績重設為原始成績");
        System.out.println("12. 個別修改學生原始成績");
        System.out.println("13. 清空所有資料並重新開始");
        System.out.println("14. 顯示調整成績公式");
        System.out.println("0. 離開系統");
        System.out.println("============================================");
    }

    private static void createClassByInput() {
        int studentCount = readInt("請輸入本班人數：");

        if (studentCount <= 0) {
            System.out.println("本班人數必須大於 0，建立失敗。\n");
            return;
        }

        gradeBook.clearStudents();

        System.out.println("\n開始輸入每位學生的原始成績。");
        System.out.println("系統會自動產生座號 1 到 " + studentCount + "。\n");

        for (int seatNumber = 1; seatNumber <= studentCount; seatNumber++) {
            double score = readScore("請輸入座號 " + seatNumber + " 的原始成績 0~100：");
            gradeBook.addStudent(new Student(seatNumber, score));
        }

        System.out.println("\n班級資料建立完成，共 " + studentCount + " 人。\n");
    }

    private static void createSampleClass() {
        gradeBook.clearStudents();

        for (int seatNumber = 1; seatNumber <= 100; seatNumber++) {
            double score = 35 + ((seatNumber * 37) % 66);
            gradeBook.addStudent(new Student(seatNumber, score));
        }

        System.out.println("已建立 100 位學生的範例班級資料。\n");
    }

    private static void searchStudentBySeatNumber() {
        if (gradeBook.isEmpty()) {
            System.out.println("目前沒有學生資料，請先建立班級資料。\n");
            return;
        }

        int seatNumber = readInt("請輸入要搜尋的座號：");
        Student student = gradeBook.searchBySeatNumber(seatNumber);

        if (student == null) {
            System.out.println("查無此座號。\n");
        } else {
            GradeBook.printTableHeader();
            System.out.println(student.toTableRow());
            System.out.println();
        }
    }

    private static void adjustByFailPercent() {
        if (gradeBook.isEmpty()) {
            System.out.println("目前沒有學生資料，請先建立班級資料。\n");
            return;
        }

        double percent = readDouble("請輸入希望當掉的百分比，例如 20 表示 20%：");

        if (percent < 0 || percent > 100) {
            System.out.println("百分比必須介於 0 到 100 之間。\n");
            return;
        }

        int targetFailCount = (int) Math.round(gradeBook.size() * percent / 100.0);

        GradeAdjuster.adjustByFailCount(gradeBook, targetFailCount);

        System.out.println("\n已依當掉百分比進行成績曲線調整。");
        System.out.println("全班人數：" + gradeBook.size() + " 人");
        System.out.println("目標當掉百分比：" + String.format("%.2f", percent) + "%");
        System.out.println("換算目標當掉人數：" + targetFailCount + " 人\n");

        gradeBook.printStatistics();
    }

    private static void adjustByFailCount() {
        if (gradeBook.isEmpty()) {
            System.out.println("目前沒有學生資料，請先建立班級資料。\n");
            return;
        }

        int targetFailCount = readInt("請輸入希望當掉的人數：");

        if (targetFailCount < 0 || targetFailCount > gradeBook.size()) {
            System.out.println("當掉人數必須介於 0 到全班人數之間。\n");
            return;
        }

        GradeAdjuster.adjustByFailCount(gradeBook, targetFailCount);

        System.out.println("\n已依指定當掉人數進行成績曲線調整。");
        System.out.println("全班人數：" + gradeBook.size() + " 人");
        System.out.println("目標當掉人數：" + targetFailCount + " 人\n");

        gradeBook.printStatistics();
    }

    private static void updateOneStudentOriginalScore() {
        if (gradeBook.isEmpty()) {
            System.out.println("目前沒有學生資料，請先建立班級資料。\n");
            return;
        }

        int seatNumber = readInt("請輸入要修改的學生座號：");
        Student student = gradeBook.searchBySeatNumber(seatNumber);

        if (student == null) {
            System.out.println("查無此座號，修改失敗。\n");
            return;
        }

        System.out.println("\n目前資料：");
        GradeBook.printTableHeader();
        System.out.println(student.toTableRow());

        double newScore = readScore("\n請輸入新的原始成績 0~100：");
        student.setOriginalScore(newScore);

        System.out.println("\n修改完成。");
        System.out.println("注意：此學生的調整後成績已同步重設為新的原始成績。");
        GradeBook.printTableHeader();
        System.out.println(student.toTableRow());
        System.out.println();
    }

    private static void resetAllDataWithoutExit() {
        if (gradeBook.isEmpty()) {
            System.out.println("目前沒有任何資料，不需要清空。\n");
            return;
        }

        System.out.println("警告：此功能會清空目前所有學生資料、原始成績、調整後成績與統計結果。");
        System.out.print("確定要清空所有資料嗎？請輸入 Y 確定，其他鍵取消：");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            gradeBook.clearStudents();
            System.out.println("所有資料已清空。系統未退出，可重新建立班級資料。\n");
        } else {
            System.out.println("已取消清空資料。\n");
        }
    }

    private static void printAdjustFormula() {
        System.out.println("============== 調整成績公式說明 ==============");
        System.out.println("本系統採用「依排名分區線性調整法」。");
        System.out.println();
        System.out.println("一、基本概念");
        System.out.println("1. 系統先依照原始成績由低到高排序。");
        System.out.println("2. 教授輸入希望不及格的百分比或人數。");
        System.out.println("3. 排名較低的目標人數會被調整到不及格區間。");
        System.out.println("4. 其餘學生會被調整到及格區間。");
        System.out.println();
        System.out.println("二、不及格人數換算公式");
        System.out.println("目標不及格人數 = 四捨五入(全班人數 × 不及格百分比 ÷ 100)");
        System.out.println("例如：全班 100 人，希望 20% 不及格");
        System.out.println("目標不及格人數 = 四捨五入(100 × 20 ÷ 100) = 20 人");
        System.out.println();
        System.out.println("三、成績調整公式");
        System.out.println("調整後成績 = 區間最低分 + (區間最高分 - 區間最低分) × 目前排名 ÷ (該區人數 - 1)");
        System.out.println();
        System.out.println("四、分數區間設定");
        System.out.println("不及格學生：45 分 ~ 59 分");
        System.out.println("及格學生：60 分 ~ 95 分");
        System.out.println();
        System.out.println("五、範例說明");
        System.out.println("假設全班 100 人，教授設定 20 人不及格：");
        System.out.println("原始成績最低的前 20 人，會被調整到 45~59 分。");
        System.out.println("其餘 80 人，會被調整到 60~95 分。");
        System.out.println();
        System.out.println("六、設計目的");
        System.out.println("此公式可以讓系統符合教授指定的不及格比例或人數，");
        System.out.println("同時保留原始成績的高低順序，避免成績低者被調得比成績高者還高。");
        System.out.println("==============================================\n");
    }

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("請輸入整數。");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();

            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("請輸入數字。");
            }
        }
    }

    private static double readScore(String message) {
        while (true) {
            double score = readDouble(message);

            if (score >= 0 && score <= 100) {
                return score;
            }

            System.out.println("成績必須介於 0 到 100 之間，請重新輸入。");
        }
    }
}

class Student {
    private int seatNumber;
    private double originalScore;
    private double adjustedScore;

    public Student(int seatNumber, double originalScore) {
        this.seatNumber = seatNumber;
        this.originalScore = originalScore;
        this.adjustedScore = originalScore;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public double getOriginalScore() {
        return originalScore;
    }

    public double getAdjustedScore() {
        return adjustedScore;
    }

    public void setOriginalScore(double originalScore) {
        if (originalScore < 0) {
            this.originalScore = 0;
        } else if (originalScore > 100) {
            this.originalScore = 100;
        } else {
            this.originalScore = originalScore;
        }

        this.adjustedScore = this.originalScore;
    }

    public void setAdjustedScore(double adjustedScore) {
        if (adjustedScore < 0) {
            this.adjustedScore = 0;
        } else if (adjustedScore > 100) {
            this.adjustedScore = 100;
        } else {
            this.adjustedScore = adjustedScore;
        }
    }

    public void resetAdjustedScore() {
        this.adjustedScore = this.originalScore;
    }

    public boolean isFailed() {
        return adjustedScore < 60;
    }

    public String getStatusText() {
        return isFailed() ? "不及格" : "及格";
    }

    public String toTableRow() {
        return String.format("%-8d %12.2f %12.2f %-8s",
                seatNumber, originalScore, adjustedScore, getStatusText());
    }
}

class GradeBook {
    private String courseName;
    private ArrayList<Student> students;

    public GradeBook(String courseName) {
        this.courseName = courseName;
        this.students = new ArrayList<Student>();
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void clearStudents() {
        students.clear();
    }

    public int size() {
        return students.size();
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }

    public Student searchBySeatNumber(int seatNumber) {
        for (Student s : students) {
            if (s.getSeatNumber() == seatNumber) {
                return s;
            }
        }

        return null;
    }

    public void sortBySeatNumberAscending() {
        Collections.sort(students, new Comparator<Student>() {
            public int compare(Student a, Student b) {
                return Integer.compare(a.getSeatNumber(), b.getSeatNumber());
            }
        });
    }

    public void sortByOriginalScoreDescending() {
        Collections.sort(students, new Comparator<Student>() {
            public int compare(Student a, Student b) {
                int scoreCompare = Double.compare(b.getOriginalScore(), a.getOriginalScore());

                if (scoreCompare != 0) {
                    return scoreCompare;
                }

                return Integer.compare(a.getSeatNumber(), b.getSeatNumber());
            }
        });
    }

    public void sortByAdjustedScoreDescending() {
        Collections.sort(students, new Comparator<Student>() {
            public int compare(Student a, Student b) {
                int scoreCompare = Double.compare(b.getAdjustedScore(), a.getAdjustedScore());

                if (scoreCompare != 0) {
                    return scoreCompare;
                }

                return Integer.compare(a.getSeatNumber(), b.getSeatNumber());
            }
        });
    }

    public void resetAdjustedScores() {
        for (Student s : students) {
            s.resetAdjustedScore();
        }
    }

    public void printAllStudents() {
        if (students.isEmpty()) {
            System.out.println("目前沒有學生資料。\n");
            return;
        }

        System.out.println("課程名稱：" + courseName);
        printTableHeader();

        for (Student s : students) {
            System.out.println(s.toTableRow());
        }

        System.out.println();
    }

    public static void printTableHeader() {
        System.out.println("-----------------------------------------------");
        System.out.printf("%-8s %12s %12s %-8s\n",
                "座號", "原始成績", "調整成績", "狀態");
        System.out.println("-----------------------------------------------");
    }

    public void printStatistics() {
        if (students.isEmpty()) {
            System.out.println("目前沒有學生資料。\n");
            return;
        }

        double originalTotal = 0;
        double adjustedTotal = 0;
        double originalMax = students.get(0).getOriginalScore();
        double originalMin = students.get(0).getOriginalScore();
        double adjustedMax = students.get(0).getAdjustedScore();
        double adjustedMin = students.get(0).getAdjustedScore();
        int failCount = 0;

        for (Student s : students) {
            double original = s.getOriginalScore();
            double adjusted = s.getAdjustedScore();

            originalTotal += original;
            adjustedTotal += adjusted;

            if (original > originalMax) {
                originalMax = original;
            }

            if (original < originalMin) {
                originalMin = original;
            }

            if (adjusted > adjustedMax) {
                adjustedMax = adjusted;
            }

            if (adjusted < adjustedMin) {
                adjustedMin = adjusted;
            }

            if (s.isFailed()) {
                failCount++;
            }
        }

        int passCount = students.size() - failCount;
        double failPercent = failCount * 100.0 / students.size();

        System.out.println("============== 成績統計資訊 ==============");
        System.out.println("全班人數：" + students.size() + " 人");
        System.out.println("原始平均：" + String.format("%.2f", originalTotal / students.size()));
        System.out.println("調整平均：" + String.format("%.2f", adjustedTotal / students.size()));
        System.out.println("原始最高：" + String.format("%.2f", originalMax));
        System.out.println("原始最低：" + String.format("%.2f", originalMin));
        System.out.println("調整最高：" + String.format("%.2f", adjustedMax));
        System.out.println("調整最低：" + String.format("%.2f", adjustedMin));
        System.out.println("及格人數：" + passCount + " 人");
        System.out.println("不及格人數：" + failCount + " 人");
        System.out.println("不及格比例：" + String.format("%.2f", failPercent) + "%");
        System.out.println("==========================================\n");
    }
}

class GradeAdjuster {

    private GradeAdjuster() {
    }

    public static void adjustByFailCount(GradeBook gradeBook, int targetFailCount) {
        ArrayList<Student> sorted = new ArrayList<Student>(gradeBook.getStudents());

        Collections.sort(sorted, new Comparator<Student>() {
            public int compare(Student a, Student b) {
                int scoreCompare = Double.compare(a.getOriginalScore(), b.getOriginalScore());

                if (scoreCompare != 0) {
                    return scoreCompare;
                }

                return Integer.compare(a.getSeatNumber(), b.getSeatNumber());
            }
        });

        int total = sorted.size();

        if (total == 0) {
            return;
        }

        if (targetFailCount < 0) {
            targetFailCount = 0;
        }

        if (targetFailCount > total) {
            targetFailCount = total;
        }

        int passCount = total - targetFailCount;

        for (int i = 0; i < total; i++) {
            Student student = sorted.get(i);
            double newScore;

            if (i < targetFailCount) {
                newScore = calculateScoreInRange(i, targetFailCount, 45, 59);
            } else {
                int passIndex = i - targetFailCount;
                newScore = calculateScoreInRange(passIndex, passCount, 60, 95);
            }

            student.setAdjustedScore(roundToTwoDecimals(newScore));
        }
    }

    private static double calculateScoreInRange(int index, int groupSize, double minScore, double maxScore) {
        if (groupSize <= 1) {
            return maxScore;
        }

        return minScore + (maxScore - minScore) * index / (groupSize - 1);
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}