package de.siramac.hexomato.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HexNode {

    private Long id;
    private boolean lastMove;
    private boolean startBlinking;
    private Player player;
}
