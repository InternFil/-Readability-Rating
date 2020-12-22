package readability;

import java.util.HashMap;

public interface CalculateIndex {
    double perform(HashMap<String, Integer> counts);
}

class CalculateIndexChooser {
    CalculateIndex alg;

    void setAlgorithm (CalculateIndex alg) {
        this.alg = alg;
    }

    double calculate (HashMap<String, Integer> counts) {
        return this.alg.perform(counts);
    }
}

class AutomatedReadabilityIndex implements CalculateIndex {

    public double perform (HashMap<String, Integer> counts) {
        int words = counts.get("words");
        int characters = counts.get("characters");
        int sentences = counts.get("sentences");
        double result = 4.71 * ((double)characters / words) + 0.5 * ((double)words / sentences) - 21.43;
        return result;
    }
}

class FleschKincaidIndex implements CalculateIndex {

    public double perform (HashMap<String, Integer> counts) {
        int words = counts.get("words");
        int sentences = counts.get("sentences");
        int syllables = counts.get("syllables");
        double result = 0.39 * ((double)words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
        return result;
    }
}

class SMOGIndex implements CalculateIndex {

    @Override
    public double perform(HashMap<String, Integer> counts) {
        int sentences = counts.get("sentences");
        int polysyllables = counts.get("polysyllables");
        double result = 1.043 * Math.sqrt((double) polysyllables * (30 / (double)sentences)) + 3.1291;
        return result;
    }
}

class ColemanLiauIndex implements CalculateIndex {

    @Override
    public double perform(HashMap<String, Integer> counts) {
        int words = counts.get("words");
        int characters = counts.get("characters");
        int sentences = counts.get("sentences");
        double L = ((double) characters / words) * 100;
        double S = ((double)sentences / words) * 100;
        double result = 0.0588 * L - 0.296 * S - 15.8;
        return result;
    }
}
