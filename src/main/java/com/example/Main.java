package com.example;

import com.example.api.ElpriserAPI;

import java.time.LocalDate;
import java.util.List;
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
                    System.out.println("Unknown command argument");
                    System.exit(64);
                }
            }
        }
        if (zone.isEmpty()) {
            System.out.print("Enter zone (SE1-SE4): ");
            zone = scan.nextLine();
        }
        System.out.println(zone);
        System.out.println(date);
        System.out.println(charging);
        ElpriserAPI.Prisklass prisklass = parsePrisklass(zone);
        List<ElpriserAPI.Elpris> dagensElPriser = elpriserAPI.getPriser(date, prisklass);
        double mean = dagensElPriser.stream().mapToDouble(ElpriserAPI.Elpris::sekPerKWh).average().orElse(0);
        String formatted = String.format("%.2f", mean * 100);
        System.out.println("Dagens medelpris är : " + formatted + " öre");
        System.out.println(dagensElPriser);
    }

    private static ElpriserAPI.Prisklass parsePrisklass (String s) {
        return switch (s){
            case "SE1" -> ElpriserAPI.Prisklass.SE1;
            case "SE2" -> ElpriserAPI.Prisklass.SE2;
            case "SE3" -> ElpriserAPI.Prisklass.SE3;
            case "SE4" -> ElpriserAPI.Prisklass.SE4;
            default -> throw new RuntimeException("Invalid zone: use SE1, SE2, SE3, or SE4. "+ s + " is unknown prisklass");
        };
    }
}
