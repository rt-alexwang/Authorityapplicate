package com.pxmart.permission.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.pxmart.permission.entity.PermissionApplication;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 82, 163);
    private static final DeviceRgb SECTION_COLOR = new DeviceRgb(236, 242, 255);

    public byte[] generatePdf(PermissionApplication app) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(40, 50, 40, 50);

            PdfFont font = PdfFontFactory.createFont("Helvetica");
            PdfFont bold = PdfFontFactory.createFont("Helvetica-Bold");

            // Title
            document.add(new Paragraph("System Access Permission Application")
                    .setFont(bold).setFontSize(18).setFontColor(HEADER_COLOR)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(4));
            document.add(new Paragraph("系統存取權限申請單")
                    .setFont(font).setFontSize(12).setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

            // Meta row
            Table metaTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth().setMarginBottom(20);
            metaTable.addCell(infoCell("申請編號 / Application No.", "APP-" + String.format("%06d", app.getId()), bold, font));
            metaTable.addCell(infoCell("申請日期 / Date", app.getCreatedAt().format(DATETIME_FMT), bold, font));
            document.add(metaTable);

            // Section 1 - Applicant
            document.add(sectionHeader("1. 申請人資訊 Applicant Information", bold));
            Table t1 = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth().setMarginBottom(16);
            t1.addCell(infoCell("姓名 Name", app.getApplicantName(), bold, font));
            t1.addCell(infoCell("信箱 Email", app.getApplicantEmail(), bold, font));
            t1.addCell(infoCell("部門 Department", app.getDepartment(), bold, font));
            t1.addCell(new Cell().setBorder(null));
            document.add(t1);

            // Section 2 - Details
            document.add(sectionHeader("2. 申請內容 Access Details", bold));
            Table t2 = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth().setMarginBottom(8);
            t2.addCell(infoCell("申請系統 Target System", app.getTargetSystem(), bold, font));
            t2.addCell(infoCell("存取範圍 Access Scope", app.getAccessScope(), bold, font));
            t2.addCell(infoCell("預計開始 Start Date", app.getExpectedStartDate().format(DATE_FMT), bold, font));
            t2.addCell(infoCell("預計結束 End Date",
                    app.getExpectedEndDate() != null ? app.getExpectedEndDate().format(DATE_FMT) : "—", bold, font));
            document.add(t2);
            document.add(new Paragraph("申請原因 Reason").setFont(bold).setFontSize(9)
                    .setFontColor(ColorConstants.DARK_GRAY).setMarginBottom(2));
            document.add(new Paragraph(app.getAccessReason()).setFont(font).setFontSize(10)
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                    .setPadding(8).setMarginBottom(16)
                    .setBackgroundColor(new DeviceRgb(250, 250, 250)));

            // Section 3 - Review
            document.add(sectionHeader("3. 審核結果 Review Result", bold));
            String statusText = switch (app.getStatus()) {
                case PENDING -> "待審核 Pending";
                case APPROVED -> "核准 Approved";
                case REJECTED -> "拒絕 Rejected";
            };
            DeviceRgb statusColor = switch (app.getStatus()) {
                case APPROVED -> new DeviceRgb(39, 174, 96);
                case REJECTED -> new DeviceRgb(192, 57, 43);
                default -> new DeviceRgb(100, 100, 100);
            };
            Table t3 = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth().setMarginBottom(8);
            t3.addCell(new Cell()
                    .add(new Paragraph("狀態 Status").setFont(bold).setFontSize(9).setFontColor(ColorConstants.DARK_GRAY))
                    .add(new Paragraph(statusText).setFont(bold).setFontSize(11).setFontColor(statusColor))
                    .setBorder(null).setBackgroundColor(SECTION_COLOR).setPadding(8));
            t3.addCell(infoCell("審核時間 Reviewed At",
                    app.getReviewedAt() != null ? app.getReviewedAt().format(DATETIME_FMT) : "—", bold, font));
            document.add(t3);

            if (app.getReviewerComment() != null && !app.getReviewerComment().isBlank()) {
                document.add(new Paragraph("審核意見 Comment").setFont(bold).setFontSize(9)
                        .setFontColor(ColorConstants.DARK_GRAY).setMarginBottom(2));
                document.add(new Paragraph(app.getReviewerComment()).setFont(font).setFontSize(10)
                        .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                        .setPadding(8).setMarginBottom(16)
                        .setBackgroundColor(new DeviceRgb(250, 250, 250)));
            }

            // Footer
            document.add(new Paragraph("本文件由系統自動產生 / This document is system-generated  |  PXMart Permission System")
                    .setFont(font).setFontSize(8).setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(30)
                    .setBorderTop(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)).setPaddingTop(8));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 產生失敗", e);
        }
    }

    private Paragraph sectionHeader(String text, PdfFont bold) {
        return new Paragraph(text).setFont(bold).setFontSize(11).setFontColor(HEADER_COLOR)
                .setBorderBottom(new SolidBorder(HEADER_COLOR, 1.5f))
                .setMarginBottom(8).setPaddingBottom(4);
    }

    private Cell infoCell(String label, String value, PdfFont bold, PdfFont regular) {
        return new Cell()
                .add(new Paragraph(label).setFont(bold).setFontSize(9).setFontColor(ColorConstants.DARK_GRAY))
                .add(new Paragraph(value != null ? value : "—").setFont(regular).setFontSize(10))
                .setBorder(null).setBackgroundColor(SECTION_COLOR).setPadding(8).setMarginBottom(4);
    }
}
