package de.siramac.hexomato.backend.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NodeIdEntity {

    @ManyToOne
    @JoinColumn(name = "game_id")
    GameEntity gameEntity;

    private int boardRow;
    private int boardCol;

}
