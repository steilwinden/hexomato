import 'package:english_words/english_words.dart';
import 'package:flutter/material.dart';
import 'package:hexomato_app/models/game.dart';

enum Opponent { computer, human }

class AddGameView extends StatefulWidget {
  const AddGameView({super.key, required this.onAddGame});

  final void Function(Game game) onAddGame;

  @override
  State<AddGameView> createState() {
    return _AddGameViewState();
  }
}

class _AddGameViewState extends State<AddGameView> {
  final _nameController = TextEditingController();
  Player _selectedPlayer = Player.PLAYER_1;
  Opponent _opponent = Opponent.computer;

  String _generateRandomName() {
    final wordPair = generateWordPairs().first;

    final first = wordPair.first;
    final second = wordPair.second;
    final firstCapitalized = '${first[0].toUpperCase()}${first.substring(1)}';
    final secondCapitalized =
        '${second[0].toUpperCase()}${second.substring(1)}';

    return '$firstCapitalized $secondCapitalized';
  }

  void _showDialog() {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('info'),
        content: const Text('please make sure a valid name was entered'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(ctx);
            },
            child: const Text('ok'),
          ),
        ],
      ),
    );
  }

  void _add() {
    if (_nameController.text.trim().isEmpty) {
      _showDialog();
      return;
    }

    widget.onAddGame(
      Game.create(
        namePlayer1: _selectedPlayer == Player.PLAYER_1
            ? _nameController.text
            : _generateRandomName(),
        namePlayer2: _selectedPlayer == Player.PLAYER_2
            ? _nameController.text
            : _generateRandomName(),
        turn: _selectedPlayer,
        playAgainstHuman: _opponent == Opponent.human,
      ),
    );
    Navigator.pop(context);
  }

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: double.infinity,
      child: SingleChildScrollView(
        child: Padding(
          padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
          child: Column(
            children: [
              Column(
                children: [
                  const SizedBox(height: 24),
                  TextField(
                    controller: _nameController,
                    maxLength: 50,
                    decoration: InputDecoration(
                      labelText: 'name',
                      border: OutlineInputBorder(),
                      suffixIcon: IconButton(
                        icon: const Icon(Icons.auto_awesome),
                        tooltip: 'generate',
                        onPressed: () {
                          setState(() {
                            _nameController.text = _generateRandomName();
                          });
                        },
                      ),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Text('start as player'),
                      SizedBox(width: 8),
                      ToggleButtons(
                        isSelected: [
                          _selectedPlayer == Player.PLAYER_1,
                          _selectedPlayer == Player.PLAYER_2,
                        ],
                        onPressed: (index) {
                          setState(() {
                            _selectedPlayer = index == 0
                                ? Player.PLAYER_1
                                : Player.PLAYER_2;
                          });
                        },
                        children: const [
                          Tooltip(
                            message: 'player 1',
                            child: Icon(Icons.looks_one),
                          ),
                          Tooltip(
                            message: 'player 2',
                            child: Icon(Icons.looks_two),
                          ),
                        ],
                      ),
                      const SizedBox(width: 4),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Text('play against'),
                      SizedBox(width: 18),
                      ToggleButtons(
                        isSelected: [
                          _opponent == Opponent.computer,
                          _opponent == Opponent.human,
                        ],
                        onPressed: (index) {
                          setState(() {
                            _opponent = index == 0
                                ? Opponent.computer
                                : Opponent.human;
                          });
                        },
                        children: const [
                          Tooltip(
                            message: 'computer',
                            child: Icon(Icons.computer),
                          ),
                          Tooltip(message: 'human', child: Icon(Icons.person)),
                        ],
                      ),
                    ],
                  ),
                ],
              ),
              Row(
                children: [
                  const Spacer(),
                  TextButton(
                    onPressed: () {
                      Navigator.pop(context);
                    },
                    child: const Text('cancel'),
                  ),
                  ElevatedButton(onPressed: _add, child: const Text('add')),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
