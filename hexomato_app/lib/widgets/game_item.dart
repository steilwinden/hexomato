import 'package:flutter/material.dart';
import 'package:hexomato_app/models/game.dart';

class GameItem extends StatelessWidget {
  const GameItem({super.key, required this.game, this.uuidLocalPlayer});

  final Game game;
  final String? uuidLocalPlayer;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 24, vertical: 8),
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
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0),
              child: ShaderMask(
                shaderCallback: (bounds) => LinearGradient(
                  colors: [
                    colorScheme.primaryContainer,
                    colorScheme.secondaryContainer,
                  ],
                ).createShader(bounds),
                child: const Text(
                  'VS',
                  style: TextStyle(fontSize: 24, fontWeight: FontWeight.w900),
                ),
              ),
            ),
            _PlayerBox(
              name: game.namePlayer2,
              isHuman: game.humanPlayer2,
              isLocal:
                  uuidLocalPlayer != null &&
                  uuidLocalPlayer == game.uuidPlayer2,
            ),
          ],
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
  });

  final String? name;
  final bool isHuman;
  final bool isLocal;

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
            color: isLocal
                ? colorScheme.primary
                : colorScheme.surfaceContainerHighest,
            width: isLocal ? 2 : 1,
          ),
          borderRadius: BorderRadius.circular(16.0),
          boxShadow: isLocal
              ? [
                  BoxShadow(
                    color: colorScheme.primary.withValues(alpha: 0.1),
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
                    color: colorScheme.primary,
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
                    ? colorScheme.onSurface.withValues(alpha: 0.5)
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
