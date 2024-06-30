package com.future.demo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "balance")
@Setter
@Getter
public class Balance {
    @Id
    private long id;
    private double amount;
}
