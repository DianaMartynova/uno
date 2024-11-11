package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class Main {
    public static void main(String[] args) throws IOException {


        // Скачиваем файл по урлу,  создаем поток для чтения и записи
        String url = "https://github.com/PeacockTeam/new-job/releases/download/v1.0/lng-4.txt.gz";
        String outputFile = "output.txt";

        URL fileUrl = new URL(url);

        InputStream inputStream = fileUrl.openStream();

        FileOutputStream fileOutputStream = new FileOutputStream("lng-4.txt.gz");
        //Копируем содержимое в новый файл по кусочкам, закрываем потоки
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
        fileOutputStream.close();
        inputStream.close();


        // Разархивировали зип, создаем буферидер для чтения
        File file = new File("lng-4.txt.gz");
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(file));
             BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream))) {


            // Читаем файл и разбиваем его на строки, пропускаем строку, если она содержит кавычки
            Set<String> uniqueStrings = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"")) {
                    continue;
                }
                uniqueStrings.add(line);
            }


            // Разбиваем строки на группы
            List<Set<String>> groups = new ArrayList<>();
            for (String string : uniqueStrings) {
                boolean added = false;
                for (Set<String> group : groups) {
                    if (hasIntersection(string, group)) {
                        group.add(string);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    Set<String> newGroup = new HashSet<>();
                    newGroup.add(string);
                    groups.add(newGroup);
                }
            }


            // Записываем группы в файл,
            int groupCount = 0;
            for (Set<String> group : groups) {
                if (group.size() > 1) {
                    groupCount++;
                }
            }


            // Сортируем от больших групп к меньшим
            groups.sort((g1, g2) -> Integer.compare(g2.size(), g1.size()));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                for (Set<String> group : groups) {
                    if (group.size() > 1) {
                        writer.write("Группа с " + group.size() + " элементами:\n");
                        for (String string : group) {
                            writer.write(string + "\n");
                        }
                        writer.write("\n");
                    }
                }
            }
        }catch (IOException ex){
            System.out.println("Ошибка  " + ex.getMessage());
        }


        // Вывод содержимого файла на экран
        try (BufferedReader reader = new BufferedReader(new FileReader("output.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }


    //Метод проверяет наличия пересечения
    private static boolean hasIntersection(String string1, Set<String> group) {
        String[] columns1 = string1.split(";");
        for (String string2 : group) {
            String[] columns2 = string2.split(";");
            int length = Math.min(columns1.length, columns2.length);
            for (int i = 0; i < length; i++) {
                if (columns1[i].equals(columns2[i]) && !columns1[i].isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
}