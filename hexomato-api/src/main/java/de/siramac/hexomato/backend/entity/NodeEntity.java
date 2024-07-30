package de.siramac.hexomato.backend.entity;

import de.siramac.hexomato.domain.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NodeEntity extends AbstractEntity {

    private int boardRow;
    private int boardCol;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameEntity gameEntity;
    private boolean lastMove;
    private boolean partOfWinnerPath;
    @Enumerated(EnumType.STRING)
    private Player player;

}
