package br.com.factoring.utils;

import br.com.factoring.dao.EnderecoDao;
import br.com.factoring.model.Cidade;
import br.com.factoring.model.Endereco;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.http.ParseException;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

public class CEPService {

    public static Endereco procurar(String cep) {
        if (cep.isEmpty()) {
            return null;
        }
        cep = cep.replace("-", "").replace(".", "");

        CepJson to_return = getCep(cep);

        if (to_return.getRetorno() != 0) {
            EnderecoDao enderecoDao = new EnderecoDao();
            Cidade cidade = enderecoDao.pesquisaCidadeUF(to_return.getUf(), to_return.getCidade());
            if (cidade == null) {
                System.err.println("Cidade Null");
                return null;
            }

            Endereco endereco = new Endereco();

            endereco.setBairro(to_return.getBairro());
            endereco.setCep(cep);
            endereco.setCidade(cidade);
            endereco.setEndereco(to_return.getLogradouro());
            endereco.setLogradouro(to_return.getTipo_logradouro());

            return endereco;
        }
        return null;
    }

    public static CepJson getCep(String cep) {
        try {
            URL url;
            Charset charset = Charset.forName("UTF8");

            url = new URL("http://cep.republicavirtual.com.br/web_cep.php?cep=" + cep + "&formato=json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
            con.setRequestMethod("GET");
            con.connect();

            CepJson cep_return;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset))) {
                String retorno = in.readLine();
                JSONObject jSONObject = new JSONObject(retorno);

                if (jSONObject.getInt("resultado") == 0) {
                    cep_return = new CepJson(jSONObject.getInt("resultado"), jSONObject.getString("resultado_txt"));
                } else {
                    cep_return = new CepJson(
                            jSONObject.getInt("resultado"),
                            jSONObject.getString("resultado_txt"),
                            jSONObject.getString("uf"),
                            jSONObject.getString("cidade"),
                            jSONObject.getString("bairro"),
                            jSONObject.getString("tipo_logradouro"),
                            jSONObject.getString("logradouro")
                    );
                }

                in.close();
            } catch (Exception e) {
                cep_return = new CepJson(0, e.getMessage());
            }
            con.disconnect();
            return cep_return;
        } catch (IOException | ParseException | JSONException e) {
            return new CepJson(0, e.getMessage());
        }
    }

    public static class CepJson {

        /*
        {"resultado":"1","resultado_txt":"sucesso - cep completo","uf":"RS","cidade":"Porto Alegre","bairro":"Passo D'Areia","tipo_logradouro":"Avenida","logradouro":"Assis Brasil"}
         */
        private Integer retorno;
        private String mensagem;
        private String uf;
        private String cidade;
        private String bairro;
        private String tipo_logradouro;
        private String logradouro;

        public CepJson(Integer retorno, String mensagem) {
            this.retorno = retorno;
            this.mensagem = mensagem;
        }

        public CepJson(Integer retorno, String mensagem, String uf, String cidade, String bairro, String tipo_logradouro, String logradouro) {
            this.retorno = retorno;
            this.mensagem = mensagem;
            this.uf = uf;
            this.cidade = cidade;
            this.bairro = bairro;
            this.tipo_logradouro = tipo_logradouro;
            this.logradouro = logradouro;
        }

        public Integer getRetorno() {
            return retorno;
        }

        public void setRetorno(Integer retorno) {
            this.retorno = retorno;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }

        public String getCidade() {
            return cidade;
        }

        public void setCidade(String cidade) {
            this.cidade = cidade;
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getTipo_logradouro() {
            return tipo_logradouro;
        }

        public void setTipo_logradouro(String tipo_logradouro) {
            this.tipo_logradouro = tipo_logradouro;
        }

        public String getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(String logradouro) {
            this.logradouro = logradouro;
        }

    }
}
