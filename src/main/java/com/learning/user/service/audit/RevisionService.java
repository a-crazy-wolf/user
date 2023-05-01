package com.learning.user.service.audit;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class RevisionService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<?> getRevisions(Class<?> entity, Long entityId, boolean fetchChanges){
        AuditQuery auditQuery = null;
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        if(fetchChanges){
            auditQuery = auditReader.createQuery().forRevisionsOfEntityWithChanges(entity,true);
        }else{
            auditQuery = auditReader.createQuery().forRevisionsOfEntity(entity,true, true);
        }
        auditQuery.add(AuditEntity.id().eq(entityId));

        List<?> result = auditQuery.getResultList();
        entityManager.close();
        return result;
    }
}
