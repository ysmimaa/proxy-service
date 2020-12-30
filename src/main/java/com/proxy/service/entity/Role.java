package com.proxy.service.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "t_role")
public class Role extends IdEntity {

    @Column(name = "AUTHORITY")
    private String authority;
}
