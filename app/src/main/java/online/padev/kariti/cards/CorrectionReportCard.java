package online.padev.kariti.cards;

import android.graphics.pdf.PdfDocument;

import online.padev.kariti.BancoDados;
import online.padev.kariti.dao.Prova;

public class CorrectionReportCard {

    public CorrectionReportCard(Prova prova, BancoDados bancoDados) {
    }

    public void generateCorrectionReport(){
        //Criar documento PDF
        PdfDocument pdfDocument = new PdfDocument();

    }
}
