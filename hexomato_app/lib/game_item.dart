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
            Row(
              children: [
                _PlayerBox(name: game.namePlayer1, isHuman: game.humanPlayer1),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16.0),
                  child: Text(
                    'VS',
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                  ),
                ),
                _PlayerBox(name: game.namePlayer2, isHuman: game.humanPlayer2),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _PlayerBox extends StatelessWidget {
  const _PlayerBox({required this.name, required this.isHuman});

  final String name;
  final bool isHuman;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(8.0),
        decoration: BoxDecoration(
          border: Border.all(color: Colors.grey.shade300),
          borderRadius: BorderRadius.circular(8.0),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              name,
              style: Theme.of(context).textTheme.titleMedium,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            Icon(isHuman ? Icons.person : Icons.computer, size: 32),
          ],
        ),
      ),
    );
  }
}
