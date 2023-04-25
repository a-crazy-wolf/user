package com.learning.user.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="permission_role" ,
            joinColumns = {@JoinColumn(name = "permission_id" ,referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id",referencedColumnName = "id")})
    private List<Role> roles;

}
