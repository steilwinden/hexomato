import 'package:hexomato_app/models/game.dart';

class Node {
  Node({
    this.lastMove = false,
    this.partOfWinnerPath = false,
    required this.player,
  });

  final bool lastMove;
  final bool partOfWinnerPath;
  final Player player;
}
