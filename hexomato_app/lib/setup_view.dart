import 'package:flutter/material.dart';
import 'package:hexomato_app/add_game_view.dart';
import 'package:hexomato_app/games_view.dart';
import 'package:hexomato_app/models/game.dart';

class SetupView extends StatefulWidget {
  const SetupView({super.key});

  @override
  State<SetupView> createState() => _SetupViewState();
}

class _SetupViewState extends State<SetupView> {
  final List<Game> _registeredGames = [];

  void _openAddGameOverlay() {
    showModalBottomSheet(
      useSafeArea: true,
      isScrollControlled: true,
      context: context,
      builder: (ctx) => AddGameView(onAddGame: _addGame),
    );
  }

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
        duration: const Duration(seconds: 3),
        content: const Text('Game deleted.'),
        action: SnackBarAction(
          label: 'Undo',
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
    Widget mainContent = Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text('Click on '),
        IconButton(
          tooltip: 'Add Game',
          onPressed: _openAddGameOverlay,
          icon: const Icon(Icons.add),
        ),
        Text(' to add a game'),
      ],
    );

    if (_registeredGames.isNotEmpty) {
      mainContent = GamesView(
        games: _registeredGames,
        onRemoveGame: _removeGame,
      );
    }

    return Scaffold(
      appBar: AppBar(
        toolbarHeight: 90,
        title: Center(child: const Text('Hexomato')),
        actions: [
          IconButton(
            tooltip: 'Add Game',
            onPressed: _openAddGameOverlay,
            icon: const Icon(Icons.add),
          ),
        ],
      ),
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              const Color.fromARGB(255, 72, 56, 146),
              const Color.fromARGB(255, 128, 112, 21),
            ],
          ),
        ),
        child: Column(children: [Expanded(child: mainContent)]),
      ),
    );
  }
}
