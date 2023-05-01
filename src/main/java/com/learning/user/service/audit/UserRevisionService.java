package com.learning.user.service.audit;

import com.learning.user.model.User;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class UserRevisionService extends RevisionService{

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> getUserPasswordRevisions(Long userId){
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        AuditQuery auditQuery = auditReader.createQuery()
                .forRevisionsOfEntity(User.class,true,true)
                .add(AuditEntity.id().eq(userId))
                .add(AuditEntity.revisionType().eq(RevisionType.MOD))
                .add(AuditEntity.property("password").hasChanged());

        List<User> result = auditQuery.getResultList();
        entityManager.close();
        return result;
    }
}
