import 'package:flutter/material.dart';
import 'package:hexomato_app/game_item.dart';
import 'package:hexomato_app/models/game.dart';

class GamesView extends StatelessWidget {
  const GamesView({super.key, required this.games, required this.onRemoveGame});

  final List<Game> games;
  final void Function(Game game) onRemoveGame;

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: games.length,
      itemBuilder: (ctx, index) => Dismissible(
        key: ValueKey(games[index]),
        background: Container(
          color: Theme.of(context).colorScheme.error.withValues(alpha: 0.75),
          margin: EdgeInsets.symmetric(horizontal: 16),
        ),
        onDismissed: (direction) {
          onRemoveGame(games[index]);
        },
        child: GameItem(games[index]),
      ),
    );
  }
}
