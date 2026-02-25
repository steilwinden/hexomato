import 'package:flutter/material.dart';
import 'package:hexomato_app/setup_view.dart';

void main() {
  runApp(
    MaterialApp(
      darkTheme: ThemeData.dark().copyWith(
        colorScheme: ColorScheme.fromSeed(
          brightness: Brightness.dark,
          seedColor: const Color.fromARGB(255, 73, 187, 196),
        ),
      ),
      home: const SetupView(),
    ),
  );
}
