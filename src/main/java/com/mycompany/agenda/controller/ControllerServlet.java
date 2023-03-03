package com.mycompany.agenda.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.agenda.model.DAO;
import com.mycompany.agenda.model.JavaBeans;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ControllerServlet", urlPatterns = {"/Controller", "/main", "/insert", "/select", "/update", "/delete", "/report"})
public class ControllerServlet extends HttpServlet {

    JavaBeans contato = new JavaBeans();
    DAO dao = new DAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        switch (action) {
            case "/main":
                contatos(request, response);
                break;
            case "/insert":
                adicionarContato(request, response);
                break;
            case "/select":
                listarContato(request, response);
                break;
            case "/update":
                editarContato(request, response);
                break;
            case "/delete":
                removerContato(request, response);
                break;
            case "/report":
                gerarRelatorio(request, response);
                break;
            default:
                response.sendRedirect("index.html");
                break;
        }

    }

    private void contatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<JavaBeans> lista = dao.listarContatos();
        request.setAttribute("contatos", lista);
        RequestDispatcher rd = request.getRequestDispatcher("agenda.jsp");
        rd.forward(request, response);
    }

    private void adicionarContato(HttpServletRequest request, HttpServletResponse response) throws IOException {
        contato.setNome(request.getParameter("nome"));
        contato.setFone(request.getParameter("fone"));
        contato.setEmail(request.getParameter("email"));
        dao.inserirContato(contato);
        response.sendRedirect("main");
    }

    private void listarContato(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        contato.setIdcon(request.getParameter("idcon"));
        dao.selecionarContato(contato);
        request.setAttribute("idcon", contato.getIdcon());
        request.setAttribute("nome", contato.getNome());
        request.setAttribute("fone", contato.getFone());
        request.setAttribute("email", contato.getEmail());
        RequestDispatcher rd = request.getRequestDispatcher("editar.jsp");
        rd.forward(request, response);
    }

    private void editarContato(HttpServletRequest request, HttpServletResponse response) throws IOException {
        contato.setIdcon(request.getParameter("idcon"));
        contato.setNome(request.getParameter("nome"));
        contato.setFone(request.getParameter("fone"));
        contato.setEmail(request.getParameter("email"));
        dao.alterarContato(contato);
        response.sendRedirect("main");
    }

    private void removerContato(HttpServletRequest request, HttpServletResponse response) throws IOException {
        contato.setIdcon(request.getParameter("idcon"));
        dao.deletarContato(contato);
        response.sendRedirect("main");
    }

    private void gerarRelatorio(HttpServletRequest request, HttpServletResponse response) {
        Document documento = new Document();
        try {
            response.setContentType("apllication/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + "contatos.pdf");
            PdfWriter.getInstance(documento, response.getOutputStream());
            documento.open();
            documento.add(new Paragraph("Lista de contatos:"));
            documento.add(new Paragraph(" "));
            PdfPTable tabela = new PdfPTable(3);
            PdfPCell col1 = new PdfPCell(new Paragraph("Nome"));
            PdfPCell col2 = new PdfPCell(new Paragraph("Fone"));
            PdfPCell col3 = new PdfPCell(new Paragraph("E-mail"));
            tabela.addCell(col1);
            tabela.addCell(col2);
            tabela.addCell(col3);
            ArrayList<JavaBeans> lista = dao.listarContatos();
            for (int i = 0; i < lista.size(); i++) {
                tabela.addCell(lista.get(i).getNome());
                tabela.addCell(lista.get(i).getFone());
                tabela.addCell(lista.get(i).getEmail());
            }
            documento.add(tabela);
            documento.close();
        } catch (Exception e) {
            System.out.println(e);
            documento.close();
        }
    }

}
