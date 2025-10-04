package com.example;

import com.example.api.ElpriserAPI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ElpriserAPI elpriserAPI = new ElpriserAPI();
        String zone = "";
        String date = LocalDate.now().toString();
        String charging = "";
        boolean sorted = false;
        Scanner scan = new Scanner(System.in);

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--zone" -> {
                    zone = args[i + 1];
                    i++;
                }
                case "--date" -> {
                    date = args[i + 1];
                    i++;
                }
                case "--charging" -> {
                    charging = args[i + 1];
                    i++;
                }
                case "--sorted" -> sorted = true;
                case "--help" -> {
                    System.out.println("""
                            \nUsage: java Main --zone <SE1|SE2|SE3|SE4> [--date YYYY-MM-DD] [--sorted] [--charging 2h|4h|8h] [--help]
                            \n Expected Command-Line Arguments:
                            * --zone SE1|SE2|SE3|SE4 (required)
                            * --date YYYY-MM-DD (optional, defaults to current date)
                            * --sorted (optional, to display prices in descending order)
                            * --charging 2h|4h|8h (optional, to find optimal charging windows)
                            * --help (optional, to display usage information)
                            \nExample: java -cp target/classes com.example.Main --zone SE3 --date 2025-09-04
                            """);
                    System.exit(0);
                }
                default -> {
                    System.out.println("Okänd kommandoargument");
                    System.exit(64);
                }
            }
        }
        ElpriserAPI.Prisklass prisklass = null;
        if (!zone.isEmpty()) {
            prisklass = parsePrisklass(zone);
        }
        if (prisklass == null) {
            System.out.print("Ange zone (SE1-SE4): ");
            zone = scan.nextLine();
            prisklass = parsePrisklass(zone);
            if (prisklass == null) {
                System.out.println("Okänd prisklass");
            }
        }
        List<ElpriserAPI.Elpris> allaElPriser = elpriserAPI.getPriser(date, prisklass);
        if (LocalDateTime.now().getHour() >= 13  /* LocalDate.parse(date).toEpochDay() < LocalDate.now().toEpochDay()*/) {
            LocalDate tomorrow = LocalDate.parse(date).plusDays(1);
            List<ElpriserAPI.Elpris> morgondagensElpriser = elpriserAPI.getPriser(tomorrow, prisklass);
            allaElPriser.addAll(morgondagensElpriser);
        }
        double mean = allaElPriser.stream().mapToDouble(ElpriserAPI.Elpris::sekPerKWh).average().orElse(0);
        if (allaElPriser.isEmpty()) {
            System.out.println("Inga priser tillgängliga.");
            return;
        } else {
            ElpriserAPI.Elpris maxElpris = allaElPriser.stream().max(Comparator.comparingDouble(ElpriserAPI.Elpris::sekPerKWh)).get();
            ElpriserAPI.Elpris minElpris = allaElPriser.stream().min(Comparator.comparingDouble(ElpriserAPI.Elpris::sekPerKWh)).get();
            System.out.println("Dagens medelpris är: " + String.format("%.2f", mean * 100) + " öre");
            System.out.println("Dagens maxpris är: " + String.format("%.2f", maxElpris.sekPerKWh() * 100) + " öre vid klockan " + maxElpris.timeStart());
            System.out.println("Dagens minpris är: " + String.format("%.2f", minElpris.sekPerKWh() * 100) + " öre vid klockan " + minElpris.timeStart());
        }
        System.out.println(allaElPriser);
    }

    private static ElpriserAPI.Prisklass parsePrisklass(String s) {
        return switch (s) {
            case "SE1" -> ElpriserAPI.Prisklass.SE1;
            case "SE2" -> ElpriserAPI.Prisklass.SE2;
            case "SE3" -> ElpriserAPI.Prisklass.SE3;
            case "SE4" -> ElpriserAPI.Prisklass.SE4;
            default -> null;
        };
    }
}
