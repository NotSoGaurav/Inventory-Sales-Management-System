package com.inventory.util;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for FileUtil low-level persistence helpers.
 */
public class FileUtilTest {

    public static int runTests() {
        System.out.println("\n--- Running FileUtil Tests ---");
        int passed = 0;
        String testFile = "data/test_file_util_unit.txt";
        FileUtil.deleteFile(testFile);

        // 1. Missing file creation
        if (FileUtil.ensureFileExists(testFile) && FileUtil.fileExists(testFile)) {
            passed++;
            System.out.println("  [PASS] File creation succeeds");
        }

        // 2. Append line
        if (FileUtil.appendLine(testFile, "Line 1") && FileUtil.appendLine(testFile, "Line 2")) {
            passed++;
            System.out.println("  [PASS] Appending lines succeeds");
        }

        // 3. Read lines
        List<String> read = FileUtil.readAllLines(testFile);
        if (read.size() == 2 && read.get(0).equals("Line 1") && read.get(1).equals("Line 2")) {
            passed++;
            System.out.println("  [PASS] Reading lines succeeds");
        }

        // 4. Overwrite
        if (FileUtil.writeAllLines(testFile, Arrays.asList("Alpha", "Beta", "Gamma"))) {
            List<String> updated = FileUtil.readAllLines(testFile);
            if (updated.size() == 3 && updated.get(0).equals("Alpha")) {
                passed++;
                System.out.println("  [PASS] Overwrite file succeeds");
            }
        }

        // 5. Missing file safety
        List<String> missing = FileUtil.readAllLines("data/non_existent_path.txt");
        if (missing != null && missing.isEmpty()) {
            passed++;
            System.out.println("  [PASS] Missing file returns empty list safely");
        }

        FileUtil.deleteFile(testFile);
        return passed;
    }
}
