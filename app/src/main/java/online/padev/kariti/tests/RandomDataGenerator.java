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
            "Escola Sol Nascente",
            "Colégio Monte Azul",
            "Instituto Esperança Viva",
            "Centro Educacional Arco-Íris",
            "Escola Horizonte Aberto",
            "Centro Educacional Estrela Guia",
            "Instituto Caminhos do Saber",
            "Escola São João Batista",
            "Centro Educacional Vida Nova",
            "Escola Ponto de Partida",
            "Centro Educacional Aliança",
            "Centro Educacional Terra Azul",
            "Instituto Esperança Jovem",
            "Escola Planalto Norte",
            "Centro Educacional Brilho Solar",
            "Academia Saber em Movimento",
            "Escola Mar Azul"
    );

    static List<Student> students = Arrays.asList(
            new Student("Ana Beatriz Silva", "ana.silva@example.com"),
            new Student("Carlos Eduardo Souza", null),
            new Student("Fernanda Oliveira", "fernanda.oliveira@example.com"),
            new Student("Marcos Vinícius Rocha", null),
            new Student("Juliana Costa", "juliana.costa@example.com"),
            new Student("Pedro Henrique Lima", "pedro.h.lima@example.com"),
            new Student("Larissa Martins", null),
            new Student("Rafael Gonçalves", "rafael.goncalves@example.com"),
            new Student("Letícia Barros", "leticia.barros@example.com"),
            new Student("Rodrigo Souza", "rodrigo.souza@example.com"),
            new Student("Vanessa Andrade", null),
            new Student("Eduardo Lima", "eduardo.lima@example.com")
    );


    static List<String> namesClass = Arrays.asList(
            "1º Ano A",
            "1º Ano B",
            "2º Ano A",
            "2º Ano B",
            "7º Ano A",
            "7º Ano B",
            "8º Ano A",
            "1ª Série B - Ensino Médio",
            "3ª Série B - Ensino Médio",
            "Turma Alfa",
            "Turma Beta",
            "Turma Geração 2025",
            "Turma Estelar",
            "Turma Horizonte",
            "Turma Fênix"
    );

    static List<Exam> listExams = Arrays.asList(
            new Exam("Prova Final de Matemática", "2023-03-12", 17, 5),
            new Exam("Teste Rápido 2", "2022-11-25", 11, 3),
            new Exam("Simulado Geral - Ciências", "2021-05-04", 9, 4),
            new Exam("Exame Prático de Física", "2025-07-19", 14, 6),
            new Exam("Prova 5", "2024-01-30", 8, 2),
            new Exam("Avaliação Diagnóstica", "2020-08-07", 19, 5),
            new Exam("Prova de História Antiga", "2023-09-16", 13, 3),
            new Exam("Prova de Lógica e Raciocínio", "2021-02-12", 15, 3),
            new Exam("Simulado de Linguagens", "2024-03-31", 14, 6),
            new Exam("Simulado Final de Filosofia", "2023-10-22", 5, 2),
            new Exam("Prova de Redação Argumentativa", "2022-09-01", 11, 6),
            new Exam("Teste Diagnóstico - Física", "2021-06-25", 16, 4),
            new Exam("Prova Simples 48", "2025-03-30", 9, 3),
            new Exam("Exame Geral - Matemática Aplicada", "2020-07-10", 18, 6),
            new Exam("Simulado Nacional Enem", "2022-11-11", 20, 5)
    );
}
