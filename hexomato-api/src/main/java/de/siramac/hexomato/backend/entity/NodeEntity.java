package de.siramac.hexomato.backend.entity;

import de.siramac.hexomato.domain.Player;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class NodeEntity {

    @EmbeddedId
    private NodeIdEntity nodeIdEntity;

    private boolean lastMove;
    private boolean partOfWinnerPath;
    @Enumerated(EnumType.STRING)
    private Player player;

}
