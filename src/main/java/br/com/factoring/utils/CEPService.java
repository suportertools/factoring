package br.com.factoring.utils;

import br.com.factoring.connections.Dao;
import br.com.factoring.dao.EnderecoDao;
import br.com.factoring.model.Cidade;
import br.com.factoring.model.Endereco;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

public class CEPService {

    public static Endereco procurar(String cep) {
        if (cep.isEmpty()) {
            return null;
        }
        EnderecoDao enderecoDao = new EnderecoDao();

        cep = cep.replace("-", "");
        String urlString = "http://cep.republicavirtual.com.br/web_cep.php?cep=" + cep + "&formato=query_string";
        // os parametros a serem enviados
        Properties parameters = new Properties();
        parameters.setProperty("cep", cep);
        parameters.setProperty("formato", "xml");
        Iterator i = parameters.keySet().iterator();
        int counter = 0;

        while (i.hasNext()) {
            String name = (String) i.next();
            String value = parameters.getProperty(name);
            urlString += (++counter == 1 ? "?" : "&") + name + "=" + value;
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Request-Method", "GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            
            StringBuilder newData;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                newData = new StringBuilder();
                String s = "";
                while (null != ((s = br.readLine()))) {
                    newData.append(s);
                }
            }

//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            StringBuilder newData = new StringBuilder();
//            String s = "";
//            while (null != ((s = br.readLine()))) {
//                newData.append(s);
//            }
//            br.close();
            XStream xstream = new XStream(new DomDriver());
            Annotations.configureAliases(xstream, CepAlias.class);
            xstream.alias("webservicecep", CepAlias.class);
            CepAlias cepAlias = (CepAlias) xstream.fromXML(newData.toString());

            Cidade cidade = enderecoDao.pesquisaCidadeUF(cepAlias.getUf(), cepAlias.getCidade());
            if (cidade == null) {
                return null;
            }

            Endereco endereco = new Endereco();

            endereco.setBairro(cepAlias.getBairro());
            endereco.setCep(cep);
            endereco.setCidade(cidade);
            endereco.setEndereco(cepAlias.getLogradouro());
            endereco.setLogradouro(cepAlias.getTipo_logradouro());
            return endereco;
        } catch (IOException e) {
            e.getMessage();
        }
        return null;
    }
}
