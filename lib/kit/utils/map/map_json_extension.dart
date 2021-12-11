import 'dart:convert';

extension MapUtilsMapJsonExtension<M, T> on Map<M, T> {
  toJsonString() {
    if (this.isEmpty) {
      return '';
    }
    return json.encode(this);
  }
}
