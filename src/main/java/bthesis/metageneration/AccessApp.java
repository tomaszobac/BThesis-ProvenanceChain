package bthesis.metageneration;

import java.util.Scanner;

public class AccessApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the input path:");
        String path1 = scanner.nextLine();

        System.out.println("Please enter the output path or skip for same as input path:");
        String path2 = scanner.nextLine();

        scanner.close();

        SystemFileLoader inputFileLoader = new SystemFileLoader(path1);

        MetaGeneration generation = new MetaGeneration(inputFileLoader);
        generation.generate();

        WriteDocument outputWriter = new WriteDocument(path2, generation.getMetadocument());

    }
}
