package com.example.splitwise.entities;

import lombok.*;

import javax.persistence.*;

import  com.example.splitwise.entities.SuperEntity;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "bill")
@Entity
public class Bill extends SuperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "amount")
    private Double billAmount;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "gang_id")
    private Group gang;

    @Column(name = "status")
    private Byte status;

    public Bill(String name, Double billAmount, Group gang) {
        this.name = name;
        this.billAmount = billAmount;
        this.gang = gang;
    }

    public Bill(String name, Double billAmount) {
        this.name = name;
        this.billAmount = billAmount;
    }
}
