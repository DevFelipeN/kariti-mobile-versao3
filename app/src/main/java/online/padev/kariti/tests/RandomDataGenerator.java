package online.padev.kariti.tests;

import android.content.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.entity.Exam;
import online.padev.kariti.entity.Student;

public class RandomDataGenerator {

    public static List<String> nameSchools = Arrays.asList(
            "Rising Sun School",
            "Guiding Star Educational Center",
            "Paths of Knowledge Institute",
            "Saint John the Baptist School",
            "New Life Educational Center",
            "Solar Shine Educational Center",
            "Learning in Motion Academy"
    );

    static List<Student> students = Arrays.asList(
            new Student("Ana Beatriz Silva", "ana.silva@example.com"),
            new Student("Rafael Gonçalves", "rafael.goncalves@example.com"),
            new Student("Letícia Barros", "leticia.barros@example.com"),
            new Student("Rodrigo Souza", "rodrigo.souza@example.com"),
            new Student("Vanessa Andrade", null),
            new Student("Eduardo Lima", "eduardo.lima@example.com")
    );


    static List<String> namesClass = Arrays.asList(
            "1º Ano A",
            "1º Ano B",
            "1ª Série B - Ensino Médio",
            "3ª Série B - Ensino Médio",
            "Turma Alfa",
            "Turma Horizonte",
            "Turma Fênix"
    );

    static List<Exam> listExams = Arrays.asList(
            new Exam("Prova Final de Matemática", "2023-03-12", 17, 5),
            new Exam("Exame Prático de Física", "2025-07-19", 14, 6),
            new Exam("Prova Simples 48", "2025-03-30", 9, 3),
            new Exam("Exame Geral - Matemática Aplicada", "2020-07-10", 18, 6),
            new Exam("Simulado Nacional Enem", "2022-11-11", 20, 5)
    );
}
