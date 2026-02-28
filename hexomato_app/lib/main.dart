import 'package:flutter/material.dart';
import 'package:hexomato_app/home_view.dart';

void main() {
  runApp(
    MaterialApp(
      theme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: const Color(0xFF101922),
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFF3B82F6), // Hex Blue
          secondary: Color(0xFFEF4444), // Hex Red
          primaryContainer: Color(0xFF60A5FA), // Light Blue
          secondaryContainer: Color(0xFFF87171), // Light Red
          surface: Color(0xFF1E293B),
          surfaceContainerHighest: Color(0xFF334155),
        ),
      ),
      home: const HomeView(),
    ),
  );
}
