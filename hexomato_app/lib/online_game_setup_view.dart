import 'package:flutter/material.dart';
import 'package:hexomato_app/games_view.dart';
import 'package:hexomato_app/models/game.dart';
import 'package:hexomato_app/setup_view.dart';
import 'package:uuid/uuid.dart';

class OnlineGameSetupView extends StatefulWidget {
  const OnlineGameSetupView({super.key});

  @override
  State<OnlineGameSetupView> createState() => _OnlineGameSetupViewState();
}

class _OnlineGameSetupViewState extends State<OnlineGameSetupView> {
  final List<Game> _registeredGames = [];
  final String _uuidLocalPlayer = const Uuid().v4();

  void _addGame(Game game) {
    setState(() {
      _registeredGames.add(game);
    });
  }

  void _removeGame(Game game) {
    final gameIndex = _registeredGames.indexOf(game);
    setState(() {
      _registeredGames.remove(game);
    });
    ScaffoldMessenger.of(context).clearSnackBars();
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        duration: const Duration(seconds: 4),
        behavior: SnackBarBehavior.floating,
        margin: const EdgeInsets.fromLTRB(24, 0, 24, 16),
        backgroundColor: Theme.of(context).colorScheme.surface,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
          side: BorderSide(
            color: Theme.of(context).colorScheme.surfaceContainerHighest,
          ),
        ),
        content: Row(
          children: [
            Icon(
              Icons.delete_rounded,
              color: Theme.of(context).colorScheme.error,
              size: 20,
            ),
            const SizedBox(width: 12),
            const Text(
              'Game removed',
              style: TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        action: SnackBarAction(
          label: 'UNDO',
          textColor: Theme.of(context).colorScheme.primary,
          onPressed: () {
            setState(() {
              _registeredGames.insert(gameIndex, game);
            });
          },
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    Widget mainContent = GamesView(
      games: _registeredGames,
      onRemoveGame: _removeGame,
      uuidLocalPlayer: _uuidLocalPlayer,
    );

    return SetupView(
      title: 'ONLINE GAME',
      isEmpty: _registeredGames.isEmpty,
      onAddGame: (name, turn) {
        _addGame(
          Game.createOnlineGame(
            namePlayer: name,
            turn: turn,
            localPlayerUuid: _uuidLocalPlayer,
          ),
        );
      },
      showAddButton: !_registeredGames.any(
        (game) =>
            game.uuidPlayer1 == _uuidLocalPlayer ||
            game.uuidPlayer2 == _uuidLocalPlayer,
      ),
      child: mainContent,
    );
  }
}
