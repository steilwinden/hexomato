import 'package:hexomato_app/models/node.dart';

enum Player { player1, player2 }

class Game {
  static const int boardSize = 11;

  Game({
    required this.id,
    required this.namePlayer1,
    required this.uuidPlayer1,
    required this.humanPlayer1,
    required this.namePlayer2,
    required this.uuidPlayer2,
    required this.humanPlayer2,
    required this.turn,
    required this.winner,
    required this.board,
    required this.connectionMessage,
  });

  Game.createOnlineGame({
    required String namePlayer,
    required Player turn,
    String? localPlayerUuid,
  }) : this(
         id: null,
         namePlayer1: turn == Player.player1 ? namePlayer : null,
         uuidPlayer1: turn == Player.player1 ? localPlayerUuid : null,
         humanPlayer1: true,
         namePlayer2: turn == Player.player2 ? namePlayer : null,
         uuidPlayer2: turn == Player.player2 ? localPlayerUuid : null,
         humanPlayer2: true,
         turn: turn,
         winner: null,
         board: List.generate(
           boardSize,
           (_) => List.generate(boardSize, (_) => null),
         ),
         connectionMessage: null,
       );

  Game.createSinglePlayerGame({
    required String namePlayer,
    required Player turn,
    String? localPlayerUuid,
  }) : this(
         id: null,
         namePlayer1: turn == Player.player1 ? namePlayer : 'Hex0Mat0',
         uuidPlayer1: turn == Player.player1 ? localPlayerUuid : null,
         humanPlayer1: turn == Player.player1,
         namePlayer2: turn == Player.player2 ? namePlayer : 'Hex0Mat0',
         uuidPlayer2: turn == Player.player2 ? localPlayerUuid : null,
         humanPlayer2: turn == Player.player2,
         turn: turn,
         winner: null,
         board: List.generate(
           boardSize,
           (_) => List.generate(boardSize, (_) => null),
         ),
         connectionMessage: null,
       );

  final int? id;
  final String? namePlayer1;
  final String? uuidPlayer1;
  final bool humanPlayer1;
  final String? namePlayer2;
  final String? uuidPlayer2;
  final bool humanPlayer2;
  final Player turn;
  final Player? winner;
  final List<List<Node?>> board;
  final String? connectionMessage;
}
