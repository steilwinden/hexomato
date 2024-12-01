package de.siramac.hexomato.backend.entity;

import de.siramac.hexomato.domain.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameEntity extends AbstractEntity {

    private String namePlayer1;
    private boolean humanPlayer1;
    private String namePlayer2;
    private boolean humanPlayer2;
    @Enumerated(EnumType.STRING)
    private Player turn;
    @Enumerated(EnumType.STRING)
    private Player winner;
    private Instant createdOn;
    @Setter
    @OneToMany(mappedBy = "gameEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NodeEntity> nodeEntityList;

}
