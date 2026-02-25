import 'package:hexomato_app/models/node.dart';

enum Player { PLAYER_1, PLAYER_2 }

class Game {
  Game({
    required this.id,
    required this.namePlayer1,
    required this.humanPlayer1,
    required this.namePlayer2,
    required this.humanPlayer2,
    required this.turn,
    required this.winner,
    required this.board,
    required this.connectionMessage,
  });

  Game.singlePlayer({
    required String namePlayer,
    required Player turn,
    required bool playAgainstHuman,
  }) : this(
         id: null,
         namePlayer1: turn == Player.PLAYER_1 ? namePlayer : null,
         humanPlayer1: turn == Player.PLAYER_1 ? true : playAgainstHuman,
         namePlayer2: turn == Player.PLAYER_2 ? namePlayer : null,
         humanPlayer2: turn == Player.PLAYER_2 ? true : playAgainstHuman,
         turn: turn,
         winner: null,
         board: null,
         connectionMessage: null,
       );

  final int? id;
  final String? namePlayer1;
  final bool humanPlayer1;
  final String? namePlayer2;
  final bool humanPlayer2;
  final Player turn;
  final Player? winner;
  final Node? board;
  final String? connectionMessage;
}
