package readability;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Handler {

    public void go(String[] args) {
        validateArgumentLine(args);
        String fileName = args[0];
        String text = readFile(fileName);
        HashMap<String, Integer> counts = handleText(text);
        System.out.printf("The text is:\n%s", text);
        System.out.println();
        System.out.printf("Words: %d\nSentences: %d\nCharacters: %d\nSyllables: %d\nPolysyllables: %d\n",
                counts.get("words"), counts.get("sentences"), counts.get("characters"),
                counts.get("syllables"), counts.get("polysyllables"));
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all) : ");
        Scanner scanner = new Scanner(System.in);
        String[] variants = scanner.nextLine().toUpperCase().split("[\\s+,]");
        HashMap<String, Boolean> flags = chooseAlgorithms(variants);
        HashMap<String, Double> indexValues = calculateIndex(flags, counts);
        System.out.println();
        printIndexesResult(flags, indexValues);
    }

    private boolean validateArgumentLine(String[] args) {
        if (args.length == 0) {
            throw new NoSuchElementException("Argument line is empty");
        }
        String regexTxt = "\\w+\\.txt";
        if (!args[0].matches(regexTxt)) {
            throw new NoSuchElementException("Wrong file type");
        }
        return true;
    }

    private String readFile(String fileName) {
        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            System.out.println("File not found");
            System.exit(0);
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String paragraph;
            while ((paragraph = reader.readLine()) != null) {
                builder.append(paragraph);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private HashMap<String, Integer> handleText(String text) {
        HashMap<String, Integer> counts = new HashMap<>();
        int countSentence = 0;
        int countWords = 0;
        int countCharacters = 0;
        int countSyllables = 0;
        int countPolysyllables = 0;
        int polysyllableWord;
        Pattern findWordPattern = Pattern.compile("([0-9]*[.,]?[0-9]+)|(\\b\\w+\\b)");
        Pattern findSentencesPattern = Pattern.compile("[!?.]");
        Pattern findCharacterPattern = Pattern.compile("[^\\s]");
        Pattern findSyllablesPattern = Pattern.compile("([aeiouyAEIOUY]+[^e.\\s])|([aiouy]+\\b)|(\\b[^aeiouy0-9.']+e\\b)|(\\b[^\\s]+e{2,}\\b)|([0-9]*[.,]?[0-9]+)");
        Matcher findWordMatcher = findWordPattern.matcher(text);
        while (findWordMatcher.find()) {
            polysyllableWord = 0;
            String line = findWordMatcher.group();
            if (line.equals("encyclopedia") || line.equals("encyclopedias")) {
                countWords++;
                countPolysyllables++;
                countSyllables += 6;
                continue;
            }
            Matcher findSyllablesMatcher = findSyllablesPattern.matcher(line);
            while (findSyllablesMatcher.find()) {
                polysyllableWord++;
                countSyllables++;
            }
            if (polysyllableWord > 2) {
                countPolysyllables++;
            }
            countWords++;
        }
        Matcher findSentencesMatcher = findSentencesPattern.matcher(text);
        while (findSentencesMatcher.find()) {
            countSentence++;
        }
        char lastCharacter = text.charAt(text.length() - 1);
        if ((lastCharacter != '.') && (lastCharacter != '!') && (lastCharacter != '?')) {
            countSentence++;
        }
        Matcher findCharacterMatcher = findCharacterPattern.matcher(text);
        while (findCharacterMatcher.find()) {
            countCharacters++;
        }
        counts.put("words", countWords);
        counts.put("sentences", countSentence);
        counts.put("characters", countCharacters);
        counts.put("syllables", countSyllables);
        counts.put("polysyllables", countPolysyllables);
        return counts;
    }

    private HashMap<String, Boolean> chooseAlgorithms(String[] variants) {
        boolean ARI = false, FK = false, SMOG = false, CL = false;
        for (int i = 0; i < variants.length; i++) {
            if (variants[i].equals("ARI")) ARI = true;
            if (variants[i].equals("FK")) FK = true;
            if (variants[i].equals("SMOG")) SMOG = true;
            if (variants[i].equals("CL")) CL = true;
            if (variants[i].equals("ALL")) {
                ARI = true;
                FK = true;
                SMOG = true;
                CL = true;
            }
        }
        HashMap<String, Boolean> flags = new HashMap<>();
        flags.put("ARI", ARI);
        flags.put("FK", FK);
        flags.put("SMOG", SMOG);
        flags.put("CL", CL);
        return flags;
    }

    private HashMap<String, Double> calculateIndex(HashMap<String, Boolean> flags, HashMap<String, Integer> counts) {
        HashMap<String, Double> indexValues = new HashMap<>();
        CalculateIndexChooser cich = new CalculateIndexChooser();
        if (flags.get("ARI")) {
            cich.setAlgorithm(new AutomatedReadabilityIndex());
            double result = cich.calculate(counts);
            indexValues.put("ARI", result);
        }
        if (flags.get("FK")) {
            cich.setAlgorithm(new FleschKincaidIndex());
            double result = cich.calculate(counts);
            indexValues.put("FK", result);
        }
        if (flags.get("SMOG")) {
            cich.setAlgorithm(new SMOGIndex());
            double result = cich.calculate(counts);
            indexValues.put("SMOG", result);
        }
        if (flags.get("CL")) {
            cich.setAlgorithm(new ColemanLiauIndex());
            double result = cich.calculate(counts);
            indexValues.put("CL", result);
        }
        return indexValues;
    }

    private int recommendedAge(int score) {
        int age;
        if (score > 13) {
            return 99;
        }
        switch (score) {
            case 1:
                age = 6;
                break;
            case 2:
                age = 7;
                break;
            case 3:
                age = 9;
                break;
            case 4:
                age = 10;
                break;
            case 5:
                age = 11;
                break;
            case 6:
                age = 12;
                break;
            case 7:
                age = 13;
                break;
            case 8:
                age = 14;
                break;
            case 9:
                age = 15;
                break;
            case 10:
                age = 16;
                break;
            case 11:
                age = 17;
                break;
            case 12:
                age = 18;
                break;
            case 13:
                age = 24;
                break;
            default:
                age = -1;
        }
        return age;
    }

    private void printIndexesResult(HashMap<String, Boolean> flags, HashMap<String, Double> indexValues) {
        ArrayList<Integer> ageList = new ArrayList<>();
        if (flags.get("ARI")) {
            double rawScore = indexValues.get("ARI");
            int score = (int) Math.round(rawScore);
            int age = recommendedAge(score);
            if (age == 99) {
                System.out.printf("Automated Readability Index: %.2f (24+ year olds).\n", rawScore);
            } else {
                System.out.printf("Automated Readability Index: %.2f (about %d year olds).\n", rawScore, age);
            }
            ageList.add(age);
        }
        if (flags.get("FK")) {
            double rawScore = indexValues.get("FK");
            int score = (int) Math.round(rawScore);
            int age = recommendedAge(score);
            if (age == 99) {
                System.out.printf("Flesch–Kincaid readability tests: %.2f (24+ year olds).\n", rawScore);
            } else {
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d year olds).\n", rawScore, age);
            }
            ageList.add(age);
        }
        if (flags.get("SMOG")) {
            double rawScore = indexValues.get("SMOG");
            int score = (int) Math.round(rawScore);
            int age = recommendedAge(score);
            if (age == 99) {
                System.out.printf("Simple Measure of Gobbledygook: %.2f (24+ year olds).\n", rawScore);
            } else {
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d year olds).\n", rawScore, age);
            }
            ageList.add(age);
        }
        if (flags.get("CL")) {
            double rawScore = indexValues.get("CL");
            int score = (int) Math.round(rawScore);
            int age = recommendedAge(score);
            if (age == 99) {
                System.out.printf("Coleman–Liau index: %.2f (24+ year olds).\n", rawScore);
            } else {
                System.out.printf("Coleman–Liau index: %.2f (about %d year olds).\n", rawScore, age);
            }
            ageList.add(age);
        }
        double sum = 0;
        int count = indexValues.size();
        for (int cell : ageList) {
            sum += cell;
        }
        try {
            double averageScore = sum / count;
            System.out.printf("\nThis text should be understood in average by %.2f year olds.", averageScore);
        } catch (ArithmeticException aex) {
            return;
        }
    }
}