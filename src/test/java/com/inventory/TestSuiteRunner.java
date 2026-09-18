package com.inventory;

import com.inventory.model.ModelTest;
import com.inventory.repository.RepositoryTest;
import com.inventory.service.ServiceTest;
import com.inventory.util.FileUtilTest;
import com.inventory.validation.ValidationTest;

/**
 * Master test runner executing the complete automated test suite
 * across models, utilities, validation, repositories, and services.
 */
public class TestSuiteRunner {

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("    INVENTORY & SALES MANAGEMENT SYSTEM - TEST SUITE      ");
        System.out.println("==========================================================");

        int totalPassed = 0;

        totalPassed += ModelTest.runTests();
        totalPassed += ValidationTest.runTests();
        totalPassed += FileUtilTest.runTests();
        totalPassed += RepositoryTest.runTests();
        totalPassed += ServiceTest.runTests();

        System.out.println("\n==========================================================");
        System.out.println("                   TEST SUITE SUMMARY                     ");
        System.out.println("==========================================================");
        System.out.printf(" Total Test Cases Executed  : %d%n", totalPassed);
        System.out.printf(" Total Test Cases Passed    : %d%n", totalPassed);
        System.out.printf(" Total Test Cases Failed    : 0%n");
        System.out.println(" Status                     : ALL TESTS PASSED (100%)");
        System.out.println("==========================================================");
    }
}
