package com.learning.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = AuditableEntity.class, isAudited = true)
public class User extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String username;

    private String firstName;

    private String lastName;

    private String emailId;

    private String password;

    private Integer userType;

    private Boolean enabled;

    private Boolean accountNonExpired;

    private Boolean credentialsNonExpired;

    private Boolean accountNonLocked;

    private String mfaSecret;

    private int failedAttempt;

    @NotAudited
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="role_user" ,
            joinColumns = {@JoinColumn(name = "user_id" ,referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id",referencedColumnName = "id")})
    private List<Role> roles;
}
