package com.nisum.mytime.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.EmpLoginData;
import com.nisum.mytime.service.EmployeeDataService;

@Component
public class PdfReportGenerator {

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	private EmployeeDataService employeeDataBaseService;

	public String generateEmployeeReport(long employeeId, String startDate, String endDate) throws MyTimeException {
		String fileName = employeeId + "_" + startDate + "_" + endDate + ".pdf";
		List<EmpLoginData> empLoginDetails = getEmployeeData(employeeId, startDate, endDate);
		if (empLoginDetails.isEmpty()) {
			return "No data available";
		} else {
			return createPDF(fileName, empLoginDetails, employeeId);
		}
	}

	private List<EmpLoginData> getEmployeeData(long employeeId, String fromDate, String toDate) throws MyTimeException {

		return employeeDataBaseService.fetchEmployeeLoginsBasedOnDates(employeeId, fromDate, toDate);
	}

	private String createPDF(String pdfFilename, List<EmpLoginData> empLoginDatas, long employeeId)
			throws MyTimeException {
		Document doc = null;
		PdfWriter docWriter = null;
		try {
			doc = new Document();
			File file = resourceLoader.getResource("/WEB-INF/reports/" + pdfFilename).getFile();
			docWriter = PdfWriter.getInstance(doc, new FileOutputStream(file.getPath()));
			setPdfDocumentProperties(doc);
			doc.open();
			preparePdfDocument(doc, empLoginDatas, employeeId);
		} catch (Exception dex) {
			MyTimeLogger.getInstance()
					.error("DocumentException while generating {} " + pdfFilename + "" , dex);
			throw new MyTimeException(dex.getMessage());
		} finally {
			if (doc != null) {
				doc.close();
			}
			if (docWriter != null) {
				docWriter.close();
			}
		}
		return pdfFilename;
	}

	private void setPdfDocumentProperties(Document doc) {
		doc.addAuthor("Nisum Consulting Pvt. Ltd.");
		doc.addCreationDate();
		doc.addProducer();
		doc.addCreator("MyTime");
		doc.addTitle("Nisum MyTime Employee Report");
		doc.setPageSize(PageSize.A4);
	}

	private void preparePdfDocument(Document doc, List<EmpLoginData> empLoginDatas, long employeeId) throws DocumentException {
		boolean isFirst = true;
		Paragraph paragraph = new Paragraph();
		Paragraph paragraph1 = new Paragraph();
		float[] columnWidths = null;
		PdfPTable table = null;
		if(employeeId == 0){
			columnWidths = new float[]{ 1f, 1f, 1f, 1f, 1f, 1f  };
			table = new PdfPTable(columnWidths);
			table.setWidthPercentage(100f);
			prepareTableHeader(table, "All");
		}else{
			columnWidths = new float[]{ 1f, 1f, 1f, 1f};
			table = new PdfPTable(columnWidths);
			table.setWidthPercentage(100f);
			prepareTableHeader(table, "Single");
		}

		for (EmpLoginData data : empLoginDatas) {
			if (isFirst && employeeId != 0) {
				Anchor anchorTarget = new Anchor(
						"Employee Id : " + data.getEmployeeId() + "\nEmployee Name : " + data.getEmployeeName());
				isFirst = false;
				paragraph1.add(anchorTarget);
			}

			if(employeeId == 0) prepareTableRow(table, data, "All");
			else prepareTableRow(table, data, "Single");
		}

		paragraph.add(table);
		doc.add(paragraph1);
		paragraph.setSpacingBefore(1);
		doc.add(paragraph);
	}

	private void prepareTableHeader(PdfPTable table, String tableFor) {

		Font bfBold12 = new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
		if ("All".equals(tableFor)) {
			insertCell(table, "ID ", Element.ALIGN_CENTER, 1, bfBold12);
			insertCell(table, "Name ", Element.ALIGN_CENTER, 1, bfBold12);
		}
		insertCell(table, "Date ", Element.ALIGN_CENTER, 1, bfBold12);
		insertCell(table, "Login Time", Element.ALIGN_CENTER, 1, bfBold12);
		insertCell(table, "Logout Time", Element.ALIGN_CENTER, 1, bfBold12);
		insertCell(table, "Total Hours", Element.ALIGN_CENTER, 1, bfBold12);
		table.setHeaderRows(1);

	}

	private void prepareTableRow(PdfPTable table, EmpLoginData data, String tableFor) {
		Font bf12 = new Font(FontFamily.TIMES_ROMAN, 12);
		if ("All".equals(tableFor)) {
			insertCell(table, (data.getEmployeeId() == null? "" : data.getEmployeeId()), Element.ALIGN_CENTER, 1, bf12);
			insertCell(table, (data.getEmployeeName() == null? "" : data.getEmployeeName()), Element.ALIGN_CENTER, 1, bf12);
		}
		insertCell(table, (data.getDateOfLogin() == null? "" : data.getDateOfLogin()), Element.ALIGN_CENTER, 1, bf12);
		insertCell(table, (data.getFirstLogin() == null? "" : data.getFirstLogin()), Element.ALIGN_CENTER, 1, bf12);
		insertCell(table, (data.getLastLogout() == null? "" : data.getLastLogout()), Element.ALIGN_CENTER, 1, bf12);
		insertCell(table, (data.getTotalLoginTime() == null? "" : data.getTotalLoginTime()), Element.ALIGN_CENTER, 1, bf12);
	}

	private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		cell.setHorizontalAlignment(align);
		cell.setColspan(colspan);
		if ("".equalsIgnoreCase(text.trim())) {
			cell.setMinimumHeight(10f);
		}
		table.addCell(cell);

	}

}
