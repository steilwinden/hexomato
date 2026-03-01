import 'dart:math' as math;

import 'package:flutter/material.dart';
import 'package:hexomato_app/models/game.dart';
import 'package:hexomato_app/models/node.dart';
import 'package:hexomato_app/widgets/app_header.dart';

class BoardView extends StatefulWidget {
  const BoardView({super.key, required this.game});

  final Game game;

  @override
  State<BoardView> createState() => _BoardViewState();
}

class _BoardViewState extends State<BoardView> {
  late Game _game;

  @override
  void initState() {
    super.initState();
    _game = widget.game;
  }

  void _onTap(int row, int col) {
    if (_game.winner != null || _game.board[row][col] != null) return;

    setState(() {
      final newNode = Node(player: _game.turn, lastMove: true);

      // Clear previous lastMove
      for (var r = 0; r < Game.boardSize; r++) {
        for (var c = 0; c < Game.boardSize; c++) {
          final node = _game.board[r][c];
          if (node != null && node.lastMove) {
            _game.board[r][c] = Node(
              player: node.player,
              partOfWinnerPath: node.partOfWinnerPath,
              lastMove: false,
            );
          }
        }
      }

      _game.board[row][col] = newNode;

      // Toggle turn (Simulating game logic for now)
      _game = Game(
        id: _game.id,
        namePlayer1: _game.namePlayer1,
        uuidPlayer1: _game.uuidPlayer1,
        humanPlayer1: _game.humanPlayer1,
        namePlayer2: _game.namePlayer2,
        uuidPlayer2: _game.uuidPlayer2,
        humanPlayer2: _game.humanPlayer2,
        turn: _game.turn == Player.player1 ? Player.player2 : Player.player1,
        winner: _game.winner,
        board: _game.board,
        connectionMessage: _game.connectionMessage,
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      body: Stack(
        children: [
          // Background Gradient Blobs
          Positioned(
            top: -100,
            left: -100,
            child: _GlowBlob(color: colorScheme.primary),
          ),
          Positioned(
            bottom: -100,
            right: -100,
            child: _GlowBlob(color: colorScheme.secondary),
          ),
          SafeArea(
            child: Column(
              children: [
                const AppHeader(title: 'MATCH'),
                _BoardHeader(game: _game),
                Expanded(
                  child: Center(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 24.0,
                        vertical: 0,
                      ),
                      child: LayoutBuilder(
                        builder: (context, constraints) {
                          return GestureDetector(
                            onTapUp: (details) {
                              final renderBox =
                                  context.findRenderObject() as RenderBox;
                              final localPoint = renderBox.globalToLocal(
                                details.globalPosition,
                              );
                              final result = _getHexAt(
                                localPoint,
                                constraints.biggest,
                                Game.boardSize,
                              );
                              if (result != null) {
                                _onTap(result.row, result.col);
                              }
                            },
                            child: CustomPaint(
                              size: constraints.biggest,
                              painter: HexBoardPainter(
                                game: _game,
                                theme: theme,
                              ),
                            ),
                          );
                        },
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 24),
              ],
            ),
          ),
        ],
      ),
    );
  }

  _HexCoords? _getHexAt(Offset point, Size size, int boardSize) {
    // Basic hit detection for hexagonal grid
    final double side = _calculateSide(size, boardSize);

    for (int r = 0; r < boardSize; r++) {
      for (int c = 0; c < boardSize; c++) {
        final center = _getHexCenter(r, c, side, size, boardSize);
        final dist = (point - center).distance;
        if (dist < side * 0.9) {
          return _HexCoords(r, c);
        }
      }
    }
    return null;
  }

  double _calculateSide(Size size, int boardSize) {
    final double sideHeight = size.height / (1.5 * boardSize + 0.5);
    final double sideWidth =
        size.width / ((boardSize + (boardSize - 1) * 0.5) * math.sqrt(3));
    return math.min(sideHeight, sideWidth);
  }

  Offset _getHexCenter(
    int row,
    int col,
    double side,
    Size size,
    int boardSize,
  ) {
    final double hexWidth = math.sqrt(3) * side;
    final double hexHeight = 2 * side;

    // Centers of the rhombus grid
    final double totalWidth = (boardSize + (boardSize - 1) * 0.5) * hexWidth;
    final double totalHeight = (boardSize * 0.75 + 0.25) * hexHeight;

    // Center the whole board in the box
    final double offsetX = (size.width - totalWidth) / 2;
    final double offsetY = (size.height - totalHeight) / 2;

    final x =
        offsetX + (col * hexWidth) + (row * hexWidth * 0.5) + (hexWidth / 2);
    final y = offsetY + (row * hexHeight * 0.75) + (hexHeight / 2);

    return Offset(x, y);
  }
}

class _HexCoords {
  final int row;
  final int col;
  _HexCoords(this.row, this.col);
}

class _GlowBlob extends StatelessWidget {
  final Color color;
  const _GlowBlob({required this.color});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 400,
      height: 400,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        gradient: RadialGradient(
          colors: [color.withValues(alpha: 0.1), Colors.transparent],
        ),
      ),
    );
  }
}

class _BoardHeader extends StatelessWidget {
  final Game game;
  const _BoardHeader({required this.game});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final isPlayer1Turn = game.turn == Player.player1;

    return Padding(
      padding: const EdgeInsets.fromLTRB(48, 6, 48, 0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          _PlayerInfo(
            name: game.namePlayer1 ?? 'Player 1',
            color: colorScheme.primary,
            isActive: isPlayer1Turn,
            isRight: false,
          ),
          const Text(
            'VS',
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.w900,
              color: Colors.white54,
            ),
          ),
          _PlayerInfo(
            name: game.namePlayer2 ?? 'Player 2',
            color: colorScheme.secondary,
            isActive: !isPlayer1Turn,
            isRight: true,
          ),
        ],
      ),
    );
  }
}

class _PlayerInfo extends StatelessWidget {
  final String name;
  final Color color;
  final bool isActive;
  final bool isRight;

  const _PlayerInfo({
    required this.name,
    required this.color,
    required this.isActive,
    required this.isRight,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: isRight
          ? CrossAxisAlignment.end
          : CrossAxisAlignment.start,
      children: [
        AnimatedContainer(
          duration: const Duration(milliseconds: 300),
          width: 64,
          height: 64,
          decoration: BoxDecoration(
            color: color.withValues(alpha: 0.1),
            shape: BoxShape.circle,
            border: Border.all(
              color: isActive ? color : color.withValues(alpha: 0.2),
              width: isActive ? 3 : 1,
            ),
            boxShadow: isActive
                ? [
                    BoxShadow(
                      color: color.withValues(alpha: 0.3),
                      blurRadius: 15,
                      spreadRadius: 2,
                    ),
                  ]
                : null,
          ),
          child: Icon(
            Icons.person,
            color: isActive ? Colors.white : Colors.white38,
            size: 32,
          ),
        ),
        const SizedBox(height: 12),
        Text(
          name,
          style: TextStyle(
            fontSize: 14,
            fontWeight: FontWeight.bold,
            color: isActive ? Colors.white : Colors.white54,
          ),
        ),
      ],
    );
  }
}

class HexBoardPainter extends CustomPainter {
  final Game game;
  final ThemeData theme;

  HexBoardPainter({required this.game, required this.theme});

  @override
  void paint(Canvas canvas, Size size) {
    final colorScheme = theme.colorScheme;
    final int boardSize = Game.boardSize;

    final double side = _calculateSide(size, boardSize);

    final basePaint = Paint()
      ..color = colorScheme.surface
      ..style = PaintingStyle.fill;

    final borderPaint = Paint()
      ..color = Colors.white.withValues(alpha: 0.25)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 2.0;

    // Draw grid
    for (int r = 0; r < boardSize; r++) {
      for (int c = 0; c < boardSize; c++) {
        final center = _getHexCenter(r, c, side, size, boardSize);
        _drawHexagon(canvas, center, side, basePaint, borderPaint);

        // Draw Player Stones
        final node = game.board[r][c];
        if (node != null) {
          _drawStone(canvas, center, side, node, colorScheme);
        }
      }
    }
  }

  double _calculateSide(Size size, int boardSize) {
    final double sideHeight = size.height / (1.5 * boardSize + 0.5);
    final double sideWidth =
        size.width / ((boardSize + (boardSize - 1) * 0.5) * math.sqrt(3));
    return math.min(sideHeight, sideWidth);
  }

  Offset _getHexCenter(
    int row,
    int col,
    double side,
    Size size,
    int boardSize,
  ) {
    final double hexWidth = math.sqrt(3) * side;
    final double hexHeight = 2 * side;

    final double totalWidth = (boardSize + (boardSize - 1) * 0.5) * hexWidth;
    final double totalHeight = (boardSize * 0.75 + 0.25) * hexHeight;

    final double offsetX = (size.width - totalWidth) / 2;
    final double offsetY = (size.height - totalHeight) / 2;

    final x =
        offsetX + (col * hexWidth) + (row * hexWidth * 0.5) + (hexWidth / 2);
    final y = offsetY + (row * hexHeight * 0.75) + (hexHeight / 2);
    return Offset(x, y);
  }

  void _drawHexagon(
    Canvas canvas,
    Offset center,
    double side,
    Paint fill,
    Paint stroke,
  ) {
    final path = Path();
    for (int i = 0; i < 6; i++) {
      final angle = math.pi / 3 * i - math.pi / 2;
      final point = Offset(
        center.dx + side * math.cos(angle),
        center.dy + side * math.sin(angle),
      );
      if (i == 0)
        path.moveTo(point.dx, point.dy);
      else
        path.lineTo(point.dx, point.dy);
    }
    path.close();

    // Draw subtle inner shadow/depth
    final shadowPath = Path();
    final shadowInnerSide = side * 0.92;
    for (int i = 0; i < 6; i++) {
      final angle = math.pi / 3 * i - math.pi / 2;
      final point = Offset(
        center.dx + shadowInnerSide * math.cos(angle),
        center.dy + shadowInnerSide * math.sin(angle),
      );
      if (i == 0)
        shadowPath.moveTo(point.dx, point.dy);
      else
        shadowPath.lineTo(point.dx, point.dy);
    }
    shadowPath.close();

    canvas.drawPath(path, fill);
    canvas.drawPath(
      shadowPath,
      Paint()
        ..color = Colors.black.withValues(alpha: 0.15)
        ..style = PaintingStyle.stroke
        ..strokeWidth = 1.0,
    );
    canvas.drawPath(path, stroke);
  }

  void _drawStone(
    Canvas canvas,
    Offset center,
    double side,
    Node node,
    ColorScheme colorScheme,
  ) {
    final color = node.player == Player.player1
        ? colorScheme.primary
        : colorScheme.secondary;

    // Stone Body with depth
    final stonePaint = Paint()
      ..shader = RadialGradient(
        colors: [color.withValues(alpha: 0.9), color.darken(0.2)],
        stops: const [0.4, 1.0],
        center: const Alignment(-0.2, -0.2),
      ).createShader(Rect.fromCircle(center: center, radius: side * 0.7));

    canvas.drawCircle(center, side * 0.7, stonePaint);

    // Realistic highlight (specular)
    final specularPaint = Paint()
      ..shader =
          RadialGradient(
            colors: [
              Colors.white.withValues(alpha: 0.4),
              Colors.white.withValues(alpha: 0.0),
            ],
          ).createShader(
            Rect.fromCircle(
              center: Offset(center.dx - side * 0.3, center.dy - side * 0.3),
              radius: side * 0.3,
            ),
          );

    canvas.drawCircle(
      Offset(center.dx - side * 0.2, center.dy - side * 0.2),
      side * 0.25,
      specularPaint,
    );

    // Stone Rim
    final rimPaint = Paint()
      ..color = Colors.white.withValues(alpha: 0.1)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.0;

    canvas.drawCircle(center, side * 0.7, rimPaint);

    if (node.lastMove) {
      // Pulsing outer glow effect
      final glowPaint = Paint()
        ..color = color.withValues(alpha: 0.4)
        ..style = PaintingStyle.stroke
        ..strokeWidth = 3.0
        ..maskFilter = const MaskFilter.blur(BlurStyle.normal, 6);

      canvas.drawCircle(center, side * 0.75, glowPaint);

      final lastMoveIndicator = Paint()
        ..color = Colors.white
        ..style = PaintingStyle.stroke
        ..strokeWidth = 1.5;

      canvas.drawCircle(center, side * 0.7, lastMoveIndicator);
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}

extension ColorExtension on Color {
  Color darken([double amount = .1]) {
    assert(amount >= 0 && amount <= 1);
    final hsl = HSLColor.fromColor(this);
    final hslDark = hsl.withLightness((hsl.lightness - amount).clamp(0.0, 1.0));
    return hslDark.toColor();
  }
}
