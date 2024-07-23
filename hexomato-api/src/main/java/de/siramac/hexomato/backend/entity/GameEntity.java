package de.siramac.hexomato.backend.entity;

import de.siramac.hexomato.domain.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GameEntity extends AbstractEntity {

    private String namePlayer1;
    private String namePlayer2;
    private Player turn;
    private Player winner;
    @OneToMany(mappedBy="nodeIdEntity.gameEntity")
    private List<NodeEntity> nodeEntityList;

}
