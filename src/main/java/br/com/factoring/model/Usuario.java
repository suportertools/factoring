package br.com.factoring.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "seg_usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_login", length = 255)
    private String login;
    @Column(name = "ds_senha", length = 255)
    private String senha;
    @Column(name = "ds_nome", length = 500)
    private String nome;

    public Usuario() {
        this.id = -1;
        this.senha = "";
        this.nome = "";
    }

    public Usuario(int id, String login, String senha, String nome) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
