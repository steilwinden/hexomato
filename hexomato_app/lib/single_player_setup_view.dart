import 'package:flutter/material.dart';
import 'package:hexomato_app/models/game.dart';
import 'package:hexomato_app/setup_view.dart';
import 'package:hexomato_app/widgets/game_item.dart';
import 'package:uuid/uuid.dart';

class SinglePlayerSetupView extends StatefulWidget {
  const SinglePlayerSetupView({super.key});

  @override
  State<SinglePlayerSetupView> createState() => _SinglePlayerSetupViewState();
}

class _SinglePlayerSetupViewState extends State<SinglePlayerSetupView> {
  Game? _activeGame;
  final String _uuidLocalPlayer = const Uuid().v4();

  @override
  Widget build(BuildContext context) {
    return SetupView(
      title: 'SINGLE PLAYER',
      isEmpty: _activeGame == null,
      showAddButton: _activeGame == null,
      onAddGame: (name, turn) {
        setState(() {
          _activeGame = Game.createSinglePlayerGame(
            namePlayer: name,
            turn: turn,
            localPlayerUuid: _uuidLocalPlayer,
          );
        });
      },
      child: _activeGame == null
          ? const SizedBox.shrink()
          : Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  GameItem(
                    game: _activeGame!,
                    uuidLocalPlayer: _uuidLocalPlayer,
                  ),
                ],
              ),
            ),
    );
  }
}
