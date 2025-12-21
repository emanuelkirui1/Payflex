package com.payflow.payrollsystem.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.payflow.payrollsystem.model.Payslip;
import com.payflow.payrollsystem.repository.PayslipRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

@Service
public class PayslipPdfService {

    private final PayslipRepository payslipRepository;

    public PayslipPdfService(PayslipRepository payslipRepository) {
        this.payslipRepository = payslipRepository;
    }

    public byte[] generatePayslipPdf(Long payslipId) {
        Payslip payslip = payslipRepository.findById(payslipId).orElseThrow();

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Payslip", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Employee Details
            document.add(new Paragraph("Employee ID: " + payslip.getEmployee().getId()));
            document.add(new Paragraph("Employee Name: " + payslip.getEmployee().getFirstName() + " " + payslip.getEmployee().getLastName()));
            document.add(new Paragraph("Salary: " + payslip.getEmployee().getSalaries().get(0).getBasicSalary()));
            document.add(new Paragraph("Net Pay: " + payslip.getNetPay()));
            document.add(new Paragraph("Payment Date: " + payslip.getGeneratedAt().toLocalDate()));

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}