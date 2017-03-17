/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

/**
 *
 * @author Claudemir Rtools
 */
public class Jasper {

    public static void printReports(String nome_jasper, String nome_arquivo, Collection listCollections, Map parameters) throws SecurityException, IllegalArgumentException {
        parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));

        JRBeanCollectionDataSource dtSource;

        try {
            String path_jasper = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/jasper/" + nome_jasper + ".jasper");
            JasperReport jasper = (JasperReport) JRLoader.loadObject(new File(path_jasper));

            String path_file_download = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/relatorios/");

            List<JasperPrint> listJasper = new ArrayList();
            dtSource = new JRBeanCollectionDataSource(listCollections);
            JasperPrint print = JasperFillManager.fillReport(jasper, parameters, dtSource);
            listJasper.add(print);

            String downloadName = nome_arquivo + ".pdf";
            File file = new File(path_file_download + "/" + downloadName);
            String mimeType = "application/pdf";

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(SimpleExporterInput.getInstance(listJasper));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file.getPath()));
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setCreatingBatchModeBookmarks(true);
            exporter.setConfiguration(configuration);
            exporter.exportReport();

            Download download = new Download(downloadName, path_file_download, mimeType, FacesContext.getCurrentInstance());
            download.baixar();

        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static void printListReports(String nome_arquivo, List<JasperPrint> list_jasper, Map parameters) throws SecurityException, IllegalArgumentException {
        parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));
        try {
            String path_file_download = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/relatorios/");

            String downloadName = nome_arquivo + ".pdf";
            File file = new File(path_file_download + "/" + downloadName);
            String mimeType = "application/pdf";

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(SimpleExporterInput.getInstance(list_jasper));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file.getPath()));
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setCreatingBatchModeBookmarks(true);
            exporter.setConfiguration(configuration);
            exporter.exportReport();

            Download download = new Download(downloadName, path_file_download, mimeType, FacesContext.getCurrentInstance());
            download.baixar();

        } catch (Exception e) {
            e.getMessage();
        }

    }
}
