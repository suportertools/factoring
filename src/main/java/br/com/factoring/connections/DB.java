/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.connections;

import br.com.factoring.utils.Sessao;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.eclipse.persistence.config.CacheType;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Claudemir Rtools
 */
public class DB {

    private EntityManager entityManager;

    protected EntityManager getEntityManager() {

        if (entityManager == null) {
            if (!Sessao.exist("conexao")) {
                String cliente = (String) Sessao.get("cliente");
                if (cliente == null){
                    return null;
                }
                
                try {
                    Map properties = new HashMap();
                    properties.put(PersistenceUnitProperties.CACHE_TYPE_DEFAULT, CacheType.SoftWeak);
                    properties.put(PersistenceUnitProperties.JDBC_USER, "postgres");
                    properties.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
                    properties.put(PersistenceUnitProperties.JDBC_DRIVER, "org.postgresql.Driver");
                    properties.put(PersistenceUnitProperties.JDBC_PASSWORD, "989899");
                    properties.put(PersistenceUnitProperties.JDBC_URL, "jdbc:postgresql://192.168.1.102:5432/" + cliente);
                    EntityManagerFactory emf = Persistence.createEntityManagerFactory(cliente, properties);
                    //String createTable = GenericaString.converterNullToString(GenericaRequisicao.getParametro("createTable"));
                    //if (createTable.equals("criar")) {
                    //    properties.put(EntityManagerFactoryProvider.DDL_GENERATION, EntityManagerFactoryProvider.CREATE_ONLY);
                    //}
                    entityManager = emf.createEntityManager();
                    Sessao.put("conexao", emf);
                } catch (Exception e) {
                    return null;
                }
            } else {
                try {
                    EntityManagerFactory emf = (EntityManagerFactory) Sessao.get("conexao");
                    entityManager = emf.createEntityManager();
                } catch (Exception e) {
                    return null;
                }
            }
        }

//        try {
//            if (entityManager == null) {
//                EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistence");
//                entityManager = emf.createEntityManager();
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
        return entityManager;
    }

    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
