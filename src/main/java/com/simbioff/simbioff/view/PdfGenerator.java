package com.simbioff.simbioff.view;


import com.lowagie.text.*;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.simbioff.simbioff.dto.UserListDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class PdfGenerator {

    public void generate(List<UserListDto> userListDtoList, HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        Paragraph p = new Paragraph("Simbiose - Lista de Colaboradores", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {3.0f, 2.0f, 2.0f, 1.0f});
        table.setSpacingBefore(10);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(CMYKColor.WHITE);
        cell.setPadding(5);
        cell.setPhrase(new Phrase("Nome", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("Celular", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("Chave Pix", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("Ativo", font));

        table.addCell(cell);


        for (UserListDto userListDto : userListDtoList) {
            table.addCell(userListDto.getFullName());
            table.addCell(userListDto.getPhone());
            table.addCell(userListDto.getPixKey());
            table.addCell(String.valueOf(userListDto.getEnabled()));

        }
        document.add(table);
        document.close();
    }



}