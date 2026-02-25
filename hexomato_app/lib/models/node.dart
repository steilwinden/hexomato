import 'package:hexomato_app/models/game.dart';

class Node {
  Node({
    required this.lastMove,
    required this.partOfWinnerPath,
    required this.player,
  });

  final bool lastMove;
  final bool partOfWinnerPath;
  final Player player;
}
