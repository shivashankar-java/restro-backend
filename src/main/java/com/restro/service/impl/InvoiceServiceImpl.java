package com.restro.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.restro.entity.Order;
import com.restro.entity.OrderItem;
import com.restro.repository.OrderRepository;
import com.restro.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final OrderRepository orderRepository;

    public InvoiceServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public byte[] generateInvoice(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font bold = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);

            //  HEADER (LOGO + RESTAURANT)
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);

            // Logo
            try {
                Image logo = Image.getInstance("src/main/resources/static/logo.png");
                logo.scaleToFit(40, 80);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(0);
                header.addCell(logoCell);
            } catch (Exception e) {
                header.addCell(new PdfPCell(new Phrase("")));
            }

            // Restaurant Info
            PdfPCell restCell = new PdfPCell();
            restCell.setBorder(0);
            restCell.addElement(new Paragraph("RESTRO", bold));
            restCell.addElement(new Paragraph("Jangamreddigudem, Andhra Pradesh", normal));
            restCell.addElement(new Paragraph("GSTIN: 22AAAAA0000A1Z5", normal));
            header.addCell(restCell);

            document.add(header);

            document.add(new Paragraph(" "));

            //  INVOICE TITLE
            Paragraph title = new Paragraph("INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));


            //  ORDER + CUSTOMER DETAILS
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);

            infoTable.addCell(getCell("Invoice No: INV-" + order.getId(), normal));
            infoTable.addCell(getCell("Date: " + order.getCreatedAt(), normal));

            infoTable.addCell(getCell("Order ID: " + order.getOrderNumber(), normal));
            infoTable.addCell(getCell("Payment: " +
                    (order.getPayment() != null ? order.getPayment().getPaymentStatus() : "PENDING"), normal));

            infoTable.addCell(getCell("Customer: " + order.getUser().getName(), normal));
            infoTable.addCell(getCell(" ", normal));

            infoTable.addCell(getCell("Address: " +
                    (order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "N/A"), normal));
            infoTable.addCell(getCell(" ", normal));

            document.add(infoTable);

            document.add(new Paragraph(" "));

            // ITEM TABLE
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            table.setWidths(new float[]{2, 1, 2, 2, 2});

            table.addCell(getHeaderCell("Item"));
            table.addCell(getHeaderCell("Qty"));
            table.addCell(getHeaderCell("Rate"));
            table.addCell(getHeaderCell("GST"));
            table.addCell(getHeaderCell("Total"));

            BigDecimal subTotal = BigDecimal.ZERO;
            BigDecimal gstTotal = BigDecimal.ZERO;

            for (OrderItem item : order.getOrderItems()) {

                BigDecimal price = item.getPricePerUnit();
                int qty = item.getQuantity();

                BigDecimal total = price.multiply(BigDecimal.valueOf(qty));

                BigDecimal gst = total.multiply(BigDecimal.valueOf(0.05));
                BigDecimal finalAmount = total.add(gst);

                subTotal = subTotal.add(total);
                gstTotal = gstTotal.add(gst);

                table.addCell(getCell(item.getMenuItem().getName(), normal));
                table.addCell(getCell(String.valueOf(qty), normal));
                table.addCell(getCell("₹" + price, normal));
                table.addCell(getCell("₹" + gst.setScale(2, RoundingMode.HALF_UP), normal));
                table.addCell(getCell("₹" + finalAmount.setScale(2, RoundingMode.HALF_UP), normal));
            }

            document.add(table);

            document.add(new Paragraph(" "));

            //  TOTAL SECTION
            BigDecimal cgst = gstTotal.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            BigDecimal sgst = gstTotal.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

            BigDecimal delivery = order.getDeliveryFee() != null ? order.getDeliveryFee() : BigDecimal.ZERO;

            BigDecimal grandTotal = subTotal.add(gstTotal).add(delivery);

            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(40);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            totalTable.addCell(getCell("Sub Total", normal));
            totalTable.addCell(getCell("₹" + subTotal, normal));

            totalTable.addCell(getCell("CGST (2.5%)", normal));
            totalTable.addCell(getCell("₹" + cgst, normal));

            totalTable.addCell(getCell("SGST (2.5%)", normal));
            totalTable.addCell(getCell("₹" + sgst, normal));

            totalTable.addCell(getCell("Delivery Fee", normal));
            totalTable.addCell(getCell("₹" + delivery, normal));

            totalTable.addCell(getCell("Grand Total", bold));
            totalTable.addCell(getCell("₹" + grandTotal.setScale(2, RoundingMode.HALF_UP), bold));

            document.add(totalTable);

            document.add(new Paragraph(" "));

            //  FOOTER
            document.add(new Paragraph("Thank you for ordering with us!", bold));
            document.add(new Paragraph("This is a system generated invoice.", normal));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating invoice", e);
        }
    }

    private PdfPCell getCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(0);
        return cell;
    }

    private PdfPCell getCenterCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell getRightCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell getHeaderCell(String text) {
        Font bold = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, bold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
