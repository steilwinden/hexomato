import 'package:flutter/material.dart';
import 'package:hexomato_app/board_view.dart';
import 'package:hexomato_app/models/game.dart';

class GameItem extends StatelessWidget {
  const GameItem({super.key, required this.game, this.uuidLocalPlayer});

  final Game game;
  final String? uuidLocalPlayer;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return InkWell(
      onTap: () {
        Navigator.of(
          context,
        ).push(MaterialPageRoute(builder: (context) => BoardView(game: game)));
      },
      borderRadius: BorderRadius.circular(20),
      child: Container(
        decoration: BoxDecoration(
          color: colorScheme.surface,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(
            color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.5),
          ),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withValues(alpha: 0.2),
              blurRadius: 10,
              offset: const Offset(0, 4),
            ),
          ],
        ),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 20),
          child: Row(
            children: [
              _PlayerBox(
                name: game.namePlayer1,
                isHuman: game.humanPlayer1,
                isLocal:
                    uuidLocalPlayer != null &&
                    uuidLocalPlayer == game.uuidPlayer1,
                color: colorScheme.primary,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16.0),
                child: const Text(
                  'VS',
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.w900,
                    color: Colors.white54,
                  ),
                ),
              ),
              _PlayerBox(
                name: game.namePlayer2,
                isHuman: game.humanPlayer2,
                isLocal:
                    uuidLocalPlayer != null &&
                    uuidLocalPlayer == game.uuidPlayer2,
                color: colorScheme.secondary,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _PlayerBox extends StatelessWidget {
  const _PlayerBox({
    required this.name,
    required this.isHuman,
    required this.isLocal,
    required this.color,
  });

  final String? name;
  final bool isHuman;
  final bool isLocal;
  final Color color;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 8),
        decoration: BoxDecoration(
          color: theme.scaffoldBackgroundColor.withValues(alpha: 0.5),
          border: Border.all(
            color: isLocal ? color : colorScheme.surfaceContainerHighest,
            width: isLocal ? 2 : 1,
          ),
          borderRadius: BorderRadius.circular(16.0),
          boxShadow: isLocal
              ? [
                  BoxShadow(
                    color: color.withValues(alpha: 0.1),
                    blurRadius: 8,
                    spreadRadius: 1,
                  ),
                ]
              : null,
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (isLocal)
              Padding(
                padding: const EdgeInsets.only(bottom: 4),
                child: Text(
                  'YOU',
                  style: TextStyle(
                    fontSize: 10,
                    fontWeight: FontWeight.w900,
                    color: color,
                    letterSpacing: 1.2,
                  ),
                ),
              ),
            Container(
              width: 48,
              height: 48,
              decoration: BoxDecoration(
                color: colorScheme.surfaceContainerHighest,
                shape: BoxShape.circle,
              ),
              child: Icon(
                name == null
                    ? Icons.person_outline
                    : (isHuman ? Icons.person : Icons.computer),
                color: Colors.white70,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              name ?? 'AWAITING JOIN',
              style: TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.bold,
                color: name == null
                    ? colorScheme.onSurface.withOpacity(0.5)
                    : colorScheme.onSurface,
              ),
              textAlign: TextAlign.center,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
            ),
          ],
        ),
      ),
    );
  }
}
