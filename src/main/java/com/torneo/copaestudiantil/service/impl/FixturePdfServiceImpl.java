package com.torneo.copaestudiantil.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.torneo.copaestudiantil.entity.Fixture;
import com.torneo.copaestudiantil.entity.Partido;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.FixtureRepository;
import com.torneo.copaestudiantil.repository.PartidoRepository;
import com.torneo.copaestudiantil.service.FixturePdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FixturePdfServiceImpl implements FixturePdfService {

    private final FixtureRepository fixtureRepository;
    private final PartidoRepository partidoRepository;

    // ── Colores corporativos ──────────────────────────────────────────────────
    private static final Color COLOR_HEADER  = new Color(0, 82, 155);
    private static final Color COLOR_SUBHDR  = new Color(30, 144, 255);
    private static final Color COLOR_ROW_ALT = new Color(240, 248, 255);
    private static final Color COLOR_WHITE   = Color.WHITE;
    private static final Color COLOR_BLACK   = Color.BLACK;
    private static final Color COLOR_BORDER  = new Color(180, 200, 220);
    private static final Color COLOR_GRAY    = new Color(130, 130, 130);

    @Override
    public byte[] generarPdf(Long fixtureId) {
        Fixture fixture = fixtureRepository.findById(fixtureId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixture no encontrado"));

        List<Partido> partidos = partidoRepository.findByFixtureId(fixtureId);
        if (partidos.isEmpty())
            throw new BadRequestException(
                    "El fixture no tiene partidos. Genera los partidos primero.");

        partidos.sort(Comparator
                .comparing(Partido::getFechaHora)
                .thenComparing(p -> p.getCancha() != null ? p.getCancha() : ""));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            agregarEncabezado(doc, fixture);
            doc.add(new Paragraph(" "));
            agregarTablaPartidos(doc, partidos);
            agregarPiePagina(doc);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new BadRequestException("Error al generar el PDF: " + e.getMessage());
        }
    }

    // ── Línea separadora usando PdfPTable (compatible con todas las versiones) ──
    private void agregarLineaSeparadora(Document doc, Color color) throws DocumentException {
        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);
        linea.setSpacingBefore(4f);
        linea.setSpacingAfter(4f);
        PdfPCell celda = new PdfPCell();
        celda.setBorderWidthTop(2f);
        celda.setBorderColorTop(color);
        celda.setBorderWidthBottom(0);
        celda.setBorderWidthLeft(0);
        celda.setBorderWidthRight(0);
        celda.setPadding(0);
        celda.setFixedHeight(2f);
        linea.addCell(celda);
        doc.add(linea);
    }

    // ── Encabezado ────────────────────────────────────────────────────────────

    private void agregarEncabezado(Document doc, Fixture fixture) throws DocumentException {
        Font fTitulo = new Font(Font.HELVETICA, 20, Font.BOLD, COLOR_HEADER);
        Paragraph titulo = new Paragraph("ESTUDIANTIL CUP CALLAO", fTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Font fSub = new Font(Font.HELVETICA, 14, Font.BOLD, COLOR_SUBHDR);
        Paragraph sub = new Paragraph(fixture.getEdicion().getNombre(), fSub);
        sub.setAlignment(Element.ALIGN_CENTER);
        doc.add(sub);

        String nombreDia = fixture.getFechaTorneo()
                .format(DateTimeFormatter.ofPattern(
                        "EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "PE")));
        String nombreDiaCap = nombreDia.substring(0, 1).toUpperCase() + nombreDia.substring(1);

        Font fFecha = new Font(Font.HELVETICA, 13, Font.BOLD, COLOR_BLACK);
        Paragraph fecha = new Paragraph(
                "Fecha " + fixture.getNumeroFecha() + " — " + nombreDiaCap, fFecha);
        fecha.setAlignment(Element.ALIGN_CENTER);
        doc.add(fecha);

        Font fSede = new Font(Font.HELVETICA, 12, Font.NORMAL, COLOR_BLACK);
        Paragraph sede = new Paragraph(fixture.getSede().getNombre(), fSede);
        sede.setAlignment(Element.ALIGN_CENTER);
        doc.add(sede);

        if (fixture.getSede().getDireccion() != null) {
            Font fDir = new Font(Font.HELVETICA, 10, Font.ITALIC, COLOR_GRAY);
            Paragraph dir = new Paragraph(fixture.getSede().getDireccion(), fDir);
            dir.setAlignment(Element.ALIGN_CENTER);
            doc.add(dir);
        }

        if (fixture.getCategoria() != null) {
            Font fCat = new Font(Font.HELVETICA, 11, Font.BOLD, COLOR_SUBHDR);
            Paragraph cat = new Paragraph(
                    fixture.getCategoria().getModalidad().name().replace("_", " ")
                            + " — " + fixture.getCategoria().getAnioNacimiento(), fCat);
            cat.setAlignment(Element.ALIGN_CENTER);
            doc.add(cat);
        }

        agregarLineaSeparadora(doc, COLOR_HEADER);
    }

    // ── Tabla de partidos ─────────────────────────────────────────────────────

    private void agregarTablaPartidos(Document doc, List<Partido> partidos)
            throws DocumentException {

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{15f, 20f, 65f});
        tabla.setSpacingBefore(8f);

        Font fHeader = new Font(Font.HELVETICA, 11, Font.BOLD, COLOR_WHITE);
        agregarCeldaHeader(tabla, "HORA",    fHeader);
        agregarCeldaHeader(tabla, "CAMPO",   fHeader);
        agregarCeldaHeader(tabla, "PARTIDO", fHeader);

        DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern(
                "hh:mm a", new Locale("es", "PE"));
        Font fNormal = new Font(Font.HELVETICA, 10, Font.NORMAL, COLOR_BLACK);
        Font fBold   = new Font(Font.HELVETICA, 10, Font.BOLD,   COLOR_BLACK);

        boolean alt = false;
        String ultimaHora = "";

        for (Partido p : partidos) {
            String horaStr = p.getFechaHora() != null
                    ? p.getFechaHora().toLocalTime().format(horaFmt).toLowerCase() : "—";
            String campo   = p.getCancha()    != null ? p.getCancha()    : "—";
            String partido = p.getEquipoLocal().getNombre()
                    + "  vs  " + p.getEquipoVisitante().getNombre();

            Color bg       = alt ? COLOR_ROW_ALT : COLOR_WHITE;
            boolean nueva  = !horaStr.equals(ultimaHora);
            ultimaHora     = horaStr;

            agregarCeldaFila(tabla, horaStr,  nueva ? fBold : fNormal, bg, Element.ALIGN_CENTER);
            agregarCeldaFila(tabla, campo,    fNormal,                  bg, Element.ALIGN_CENTER);
            agregarCeldaFila(tabla, partido,  fBold,                    bg, Element.ALIGN_LEFT);
            alt = !alt;
        }

        doc.add(tabla);

        Font fTotal = new Font(Font.HELVETICA, 10, Font.ITALIC, COLOR_GRAY);
        Paragraph total = new Paragraph("Total: " + partidos.size() + " partidos", fTotal);
        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingBefore(4f);
        doc.add(total);
    }

    private void agregarCeldaHeader(PdfPTable tabla, String texto, Font fuente) {
        PdfPCell c = new PdfPCell(new Phrase(texto, fuente));
        c.setBackgroundColor(COLOR_HEADER);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setPadding(8f);
        c.setBorderColor(COLOR_WHITE);
        c.setBorderWidth(1f);
        tabla.addCell(c);
    }

    private void agregarCeldaFila(PdfPTable tabla, String texto, Font fuente,
                                  Color bg, int alineacion) {
        PdfPCell c = new PdfPCell(new Phrase(texto, fuente));
        c.setBackgroundColor(bg);
        c.setHorizontalAlignment(alineacion);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setPadding(6f);
        c.setBorderColor(COLOR_BORDER);
        c.setBorderWidth(0.5f);
        tabla.addCell(c);
    }

    // ── Pie de página ─────────────────────────────────────────────────────────

    private void agregarPiePagina(Document doc) throws DocumentException {
        agregarLineaSeparadora(doc, COLOR_BORDER);
        Font fPie = new Font(Font.HELVETICA, 8, Font.ITALIC, COLOR_GRAY);
        Paragraph pie = new Paragraph(
                "Estudiantil Cup Callao — Documento generado automáticamente\n"
                        + "Tolerancia máxima 5 minutos (Art. 31). "
                        + "Los equipos deben presentarse con 10 minutos de anticipación (Art. 26).",
                fPie);
        pie.setAlignment(Element.ALIGN_CENTER);
        pie.setSpacingBefore(4f);
        doc.add(pie);
    }
}
