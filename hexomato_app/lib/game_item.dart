import 'package:flutter/material.dart';
import 'package:hexomato_app/models/game.dart';

class GameItem extends StatelessWidget {
  const GameItem(this.game, {super.key});

  final Game game;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              game.id.toString(),
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 4),
            Row(
              children: [
                Text(game.namePlayer1 ?? ''),
                const Spacer(),
                Text(game.namePlayer2 ?? ''),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
