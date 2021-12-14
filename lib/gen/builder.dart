import 'package:build/build.dart';
import 'package:eyflutter_core/gen/db_schema_generator.dart';
import 'package:source_gen/source_gen.dart';

Builder dbSchemaBuilder(BuilderOptions options) => LibraryBuilder(DbSchemaGenerator(), generatedExtension: '.db.dart');
