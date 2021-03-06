/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.cefetmg.inf.organizer.model.dao.impl;

import br.cefetmg.inf.organizer.model.dao.GenericDAO;
import br.cefetmg.inf.organizer.model.dao.IItemTagDAO;
import br.cefetmg.inf.organizer.model.dao.impl.jpa.GenericDAOImpl;
import br.cefetmg.inf.organizer.model.domain.ItemTag;
import br.cefetmg.inf.organizer.model.domain.Tag;
import br.cefetmg.inf.organizer.model.domain.jpa.ItemJPA;
import br.cefetmg.inf.organizer.model.domain.jpa.ItemTagJPA;
import br.cefetmg.inf.organizer.model.domain.jpa.ItemTagPKJPA;
import br.cefetmg.inf.organizer.model.domain.jpa.UserJPA;
import br.cefetmg.inf.util.db.ConnectionManager;
import br.cefetmg.inf.util.db.JPAUtil;
import br.cefetmg.inf.util.exception.PersistenceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aline
 */
public class ItemTagDAO implements IItemTagDAO{
    
    private GenericDAO genericDAO;

    public ItemTagDAO() {
        if (JPAUtil.usingJPA) {
            genericDAO = new GenericDAOImpl();
        }
    }

    @Override
    public boolean createTagInItem(ItemTag itemTag) throws PersistenceException{
                
        if (!JPAUtil.usingJPA) {
            try {
                Connection connection = ConnectionManager.getInstance().getConnection();
                String sql = "INSERT INTO Item_Tag VALUES(?,?)";

                for (int i = 0; i < itemTag.getListTags().size(); i++) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setLong(1, itemTag.getItem().getSeqItem());
                        preparedStatement.setLong(2, itemTag.getListTags().get(i).getSeqTag());

                        preparedStatement.execute();
                    }
                }

                connection.close();

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new PersistenceException(ex.getMessage(), ex);
            }
        } else {
            System.out.println("Using JPA");
            ItemTagJPA itemTagJPA = new ItemTagJPA();
            ItemTagPKJPA itemTagPK = new ItemTagPKJPA();
            
            for (int i = 0; i < itemTag.getListTags().size(); i++) {
                
                itemTagPK.setSeqTag(itemTag.getItem().getSeqItem().intValue());
                itemTagPK.setSeqTag(itemTag.getListTags().get(i).getSeqTag().intValue());
                itemTagJPA.setItemTagPK(itemTagPK);
               
                genericDAO.save(itemTagJPA);
            }
            
            return true;
        }

    }

    @Override
    public boolean deleteTagInItem(ArrayList<Tag> itemTag, Long id) throws PersistenceException{
        
        if (!JPAUtil.usingJPA) {
            try {
                Connection connection = ConnectionManager.getInstance().getConnection();
                String sql = "DELETE FROM Item_Tag WHERE seq_tag=? and seq_item=?";

                for(Tag t : itemTag){
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setLong(1, t.getSeqTag());
                        preparedStatement.setLong(2, id);

                        preparedStatement.execute();
                    }            
                }

                connection.close();

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new PersistenceException(ex.getMessage(), ex);
            }
        } else {
            System.out.println("Using JPA");
            Map<String, Object> namedParams = new HashMap<>();
            
            for(Tag t : itemTag){
                
                namedParams.put("seqItem", id.intValue());
                namedParams.put("seqTag", id.intValue());
                genericDAO.findByNamedQuery("ItemTagJPA.deleteTagByItemId", namedParams);
                                       
            }
           
            return true;
        }

    }

    @Override
    public ArrayList<Tag> listAllTagInItem(Long seqItem) throws PersistenceException{
        
        try {
            Connection connection = ConnectionManager.getInstance().getConnection();
            String sql = "SELECT A.seq_tag, B.nom_tag FROM item_tag A JOIN tag B ON A.seq_tag = B.seq_tag WHERE A.seq_item=?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, seqItem);
            
            ResultSet result = preparedStatement.executeQuery();
            
            ArrayList<Tag> listAllTag = null;
            
            if (result.next()) {
                listAllTag = new ArrayList<>();
                do {
                    Tag tag = new Tag();
 
                    tag.setSeqTag(result.getLong("seq_tag"));
                    tag.setTagName(result.getString("nom_tag"));
        
                    listAllTag.add(tag);
                } while (result.next());
            }

            result.close();
            preparedStatement.close();
            connection.close();
                       
            return listAllTag;
        } catch (Exception ex) {
           throw new PersistenceException(ex.getMessage()); 
        }
    }

    @Override
    public boolean deleteTagByItemId(Long idItem) throws PersistenceException {
        
        if (!JPAUtil.usingJPA) {
            try {
                Connection connection = ConnectionManager.getInstance().getConnection();
                String sql = "DELETE FROM item_tag WHERE seq_item=?";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setLong(1, idItem);

                preparedStatement.execute();
                preparedStatement.close();
                connection.close();

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new PersistenceException(ex.getMessage(), ex);
            }
        } else {
            System.out.println("Using JPA");
                        
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put("seqItem", idItem);
            
            genericDAO.findByNamedQuery("ItemTagJPA.deleteTagByItemId", namedParams);
            
            return true;
        }


    }

        
}
