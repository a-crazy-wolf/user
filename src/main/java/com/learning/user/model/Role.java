package com.learning.user.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private Integer type;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="permission_role" ,
            joinColumns = {@JoinColumn(name = "role_id" ,referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id",referencedColumnName = "id")})
    private List<Permission> permissions;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="role_user" ,
            joinColumns = {@JoinColumn(name = "role_id" ,referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id",referencedColumnName = "id")})
    private List<User> users;

}
