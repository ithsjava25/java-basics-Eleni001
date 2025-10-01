package com.example;

import com.example.api.ElpriserAPI;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ElpriserAPI elpriserAPI = new ElpriserAPI();
        String zone = "";
        String date = "";

        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            if (args[i] .equals("--zone")) {
                zone = args[i + 1];
                i++;
            } else if (args[i] .equals("--date")) {
                date = args[i + 1];
                i++;
            }
        }
        System.out.println(zone);
        System.out.println(date);
        ElpriserAPI.Prisklass prisklass = parsePrisklass(zone);
        List<ElpriserAPI.Elpris> dagensElPriser = elpriserAPI.getPriser(date, prisklass);
        System.out.println(dagensElPriser);
    }

    private static ElpriserAPI.Prisklass parsePrisklass (String s) {
        return switch (s){
            case "SE1" -> ElpriserAPI.Prisklass.SE1;
            case "SE2" -> ElpriserAPI.Prisklass.SE2;
            case "SE3" -> ElpriserAPI.Prisklass.SE3;
            case "SE4" -> ElpriserAPI.Prisklass.SE4;
            default -> throw new RuntimeException("Unknown Prisklass " + s);
        };
    }
}
