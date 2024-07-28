package de.siramac.hexomato.backend.entity;

import de.siramac.hexomato.domain.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GameEntity extends AbstractEntity {

    private String namePlayer1;
    private String namePlayer2;
    @Enumerated(EnumType.STRING)
    private Player turn;
    @Enumerated(EnumType.STRING)
    private Player winner;
    private Instant createdOn;
    @Setter
    @OneToMany(mappedBy = "nodeIdEntity.gameEntity")
    private List<NodeEntity> nodeEntityList;

}
