package com.pxmart.permission.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.pxmart.permission.entity.PermissionApplication;
import com.pxmart.permission.entity.PermissionRow;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private static final DeviceRgb HEADER_BG = new DeviceRgb(220, 230, 245);
    private static final DeviceRgb SUBHEADER_BG = new DeviceRgb(235, 240, 250);

    public byte[] generatePdf(PermissionApplication app) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4.rotate());
            document.setMargins(30, 30, 30, 30);

            PdfFont font = loadFont();
            PdfFont bold = loadBoldFont();

            // 標題
            document.add(new Paragraph("權限申請表格")
                    .setFont(bold).setFontSize(14)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(8));

            // 基本資訊表
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{15, 85}))
                    .useAllAvailableWidth().setMarginBottom(10);
            addInfoRow(infoTable, "申請日期", app.getCreatedAt() != null ? app.getCreatedAt().format(DATE_FMT) : "", font, bold);
            addInfoRow(infoTable, "申請人", nvl(app.getApplicantName()), font, bold);
            addInfoRow(infoTable, "申請項目", nvl(app.getRequestItem()), font, bold);
            addInfoRow(infoTable, "需求說明", nvl(app.getDescription()), font, bold);
            addInfoRow(infoTable, "申請帳號", nvl(app.getRequestAccounts()), font, bold);
            document.add(infoTable);

            // 權限明細表 - 12 欄
            // 本機(IP+DB) 遠端(IP+DB) Object(Type+Name) 權限(×6)
            float[] colWidths = {65, 48, 75, 48, 38, 160, 42, 42, 42, 42, 50, 38};
            Table permTable = new Table(UnitValue.createPointArray(colWidths))
                    .useAllAvailableWidth().setMarginBottom(10);

            // 第一層表頭（合併欄）
            permTable.addHeaderCell(groupHeader("本機伺服器", 2, bold));
            permTable.addHeaderCell(groupHeader("遠端伺服器", 2, bold));
            permTable.addHeaderCell(groupHeader("Object", 2, bold));
            permTable.addHeaderCell(groupHeader("權限", 6, bold));

            // 第二層表頭
            String[] subs = {"IP", "DB", "IP", "DB", "Type", "Name", "Select", "Insert", "Update", "Delete", "Execute", "Alter"};
            for (String s : subs) {
                permTable.addHeaderCell(subHeader(s, font));
            }

            // 資料列
            List<PermissionRow> rows = app.getRows();
            int dataRows = rows != null ? rows.size() : 0;
            if (rows != null) {
                for (PermissionRow row : rows) {
                    permTable.addCell(dataCell(row.getLocalIp(), font));
                    permTable.addCell(dataCell(row.getLocalDb(), font));
                    permTable.addCell(dataCell(row.getRemoteIp(), font));
                    permTable.addCell(dataCell(row.getRemoteDb(), font));
                    permTable.addCell(dataCell(row.getObjectType(), font));
                    permTable.addCell(dataCell(row.getObjectName(), font));
                    permTable.addCell(checkCell(row.isCanSelect(), font));
                    permTable.addCell(checkCell(row.isCanInsert(), font));
                    permTable.addCell(checkCell(row.isCanUpdate(), font));
                    permTable.addCell(checkCell(row.isCanDelete(), font));
                    permTable.addCell(checkCell(row.isCanExecute(), font));
                    permTable.addCell(checkCell(row.isCanAlter(), font));
                }
            }
            // 補空白列至少 15 列
            int emptyRows = Math.max(15 - dataRows, 0);
            for (int i = 0; i < emptyRows; i++) {
                for (int j = 0; j < 12; j++) {
                    permTable.addCell(emptyCell());
                }
            }
            document.add(permTable);

            // 審核結果（若已審核）
            if (app.getStatus() != PermissionApplication.ApplicationStatus.PENDING) {
                String statusText = app.getStatus() == PermissionApplication.ApplicationStatus.APPROVED ? "已核准" : "已拒絕";
                document.add(new Paragraph("審核結果：" + statusText +
                        (app.getReviewedAt() != null ? "　　審核時間：" + app.getReviewedAt().format(DATETIME_FMT) : ""))
                        .setFont(font).setFontSize(10).setMarginBottom(4));
                if (app.getReviewerComment() != null && !app.getReviewerComment().isBlank()) {
                    document.add(new Paragraph("審核意見：" + app.getReviewerComment())
                            .setFont(font).setFontSize(10));
                }
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 產生失敗", e);
        }
    }

    private PdfFont loadFont() {
        String[] candidates = {
            "C:/Windows/Fonts/msjh.ttc,0",
            "C:/Windows/Fonts/mingliu.ttc,0",
            "C:/Windows/Fonts/simsun.ttc,0"
        };
        for (String path : candidates) {
            try {
                return PdfFontFactory.createFont(path, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (Exception ignored) {}
        }
        try { return PdfFontFactory.createFont("Helvetica"); } catch (Exception e) { throw new RuntimeException(e); }
    }

    private PdfFont loadBoldFont() {
        String[] candidates = {
            "C:/Windows/Fonts/msjhbd.ttc,0",
            "C:/Windows/Fonts/msjh.ttc,0",
            "C:/Windows/Fonts/mingliu.ttc,0"
        };
        for (String path : candidates) {
            try {
                return PdfFontFactory.createFont(path, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (Exception ignored) {}
        }
        try { return PdfFontFactory.createFont("Helvetica-Bold"); } catch (Exception e) { throw new RuntimeException(e); }
    }

    private void addInfoRow(Table t, String label, String value, PdfFont font, PdfFont bold) {
        t.addCell(new Cell().add(new Paragraph(label).setFont(bold).setFontSize(10))
                .setBackgroundColor(HEADER_BG)
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f))
                .setPadding(5).setVerticalAlignment(VerticalAlignment.MIDDLE));
        t.addCell(new Cell().add(new Paragraph(value).setFont(font).setFontSize(10))
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f))
                .setPadding(5));
    }

    private Cell groupHeader(String text, int colspan, PdfFont bold) {
        return new Cell(1, colspan)
                .add(new Paragraph(text).setFont(bold).setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(HEADER_BG)
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f))
                .setPadding(4).setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell subHeader(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9)
                        .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(SUBHEADER_BG)
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f))
                .setPadding(3).setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell dataCell(String value, PdfFont font) {
        return new Cell()
                .add(new Paragraph(nvl(value)).setFont(font).setFontSize(9))
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f))
                .setPadding(3).setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell checkCell(boolean checked, PdfFont font) {
        return new Cell()
                .add(new Paragraph(checked ? "V" : "").setFont(font).setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER))
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f))
                .setPadding(3).setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell emptyCell() {
        return new Cell()
                .add(new Paragraph(" ").setFontSize(9))
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f))
                .setHeight(18);
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }
}
