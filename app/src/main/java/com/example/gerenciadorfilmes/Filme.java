package com.example.gerenciadorfilmes;

public class Filme {
    private String id;
    private String nome;
    private String tipo;
    private String genero;
    private String ano;
    private String nota;
    private boolean assistido;

    public Filme() {}

    public Filme(String nome, String tipo, String genero, String ano, String nota, boolean assistido) {
        this.nome = nome;
        this.tipo = tipo;
        this.genero = genero;
        this.ano = ano;
        this.nota = nota;
        this.assistido = assistido;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getAno() { return ano; }
    public void setAno(String ano) { this.ano = ano; }
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
    public boolean isAssistido() { return assistido; }
    public void setAssistido(boolean assistido) { this.assistido = assistido; }
}