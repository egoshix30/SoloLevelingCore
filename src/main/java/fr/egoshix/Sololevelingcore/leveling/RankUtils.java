package fr.egoshix.Sololevelingcore.leveling;

public class RankUtils {
    public static String getRankFromLevel(int level) {
        if (level <= 10) return "E";
        else if (level <= 20) return "D";
        else if (level <= 30) return "C";
        else if (level <= 40) return "B";
        else if (level <= 50) return "A";
        else if (level <= 70) return "S";
        else if (level <= 90) return "SS";
        else return "SSS";
    }
}
