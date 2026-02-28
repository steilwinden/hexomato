import 'package:flutter/material.dart';
import 'package:hexomato_app/models/game.dart';
import 'package:hexomato_app/widgets/game_item.dart';

class GamesView extends StatelessWidget {
  const GamesView({
    super.key,
    required this.games,
    required this.onRemoveGame,
    this.uuidLocalPlayer,
  });

  final List<Game> games;
  final void Function(Game game) onRemoveGame;
  final String? uuidLocalPlayer;

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: games.length,
      itemBuilder: (ctx, index) => Dismissible(
        key: ValueKey(games[index]),
        direction: DismissDirection.endToStart,
        background: Container(
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.error,
            borderRadius: BorderRadius.circular(20),
          ),
          margin: const EdgeInsets.symmetric(horizontal: 24, vertical: 8),
          alignment: Alignment.centerRight,
          padding: const EdgeInsets.only(right: 24),
          child: const Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.delete_rounded, color: Colors.white, size: 28),
              SizedBox(height: 4),
              Text(
                'DELETE',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 11,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 1.2,
                ),
              ),
            ],
          ),
        ),
        onDismissed: (direction) {
          onRemoveGame(games[index]);
        },
        child: GameItem(game: games[index], uuidLocalPlayer: uuidLocalPlayer),
      ),
    );
  }
}
