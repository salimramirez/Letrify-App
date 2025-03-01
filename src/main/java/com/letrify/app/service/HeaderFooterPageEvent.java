package com.letrify.app.service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Document;

public class HeaderFooterPageEvent extends PdfPageEventHelper {
    private Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable footer = new PdfPTable(1);
        footer.setTotalWidth(527); // Ajustar ancho al documento
        footer.setLockedWidth(true);
        footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        
        // 📌 Número de página
        footer.addCell(new Phrase("Página " + writer.getPageNumber(), footerFont));
        
        // Posicionar el pie de página en la parte inferior
        PdfContentByte canvas = writer.getDirectContent();
        footer.writeSelectedRows(0, -1, 34, 30, canvas);
    }
}