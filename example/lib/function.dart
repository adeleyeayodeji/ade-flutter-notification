import 'dart:convert';
import 'package:http/http.dart' as http;

sendServerNotifcation(String title, String body, String _scanBarcode) async {
  var headers = {
    'Authorization': 'key=YOUR_SERVER_KEY',
    'Content-Type': 'application/json'
  };
  var request = http.Request('POST', Uri.parse('https://fcm.googleapis.com/fcm/send'));
  request.body = json.encode({
    "to": _scanBarcode,
    "mutable_content": true,
    "notification": {
      "title": title,
      "body": body,
      "sound": "default",
      "badge": 1
    },
    "priority": "high",
    "data": {
      "content": {
        "id": 561,
        "channelKey": "basic_channel",
        "title": "New Course Alert!",
        "body": "New courses are now available, Check them out!",
        "notificationLayout": "BigPicture",
        "largeIcon": "https://www.aqskill.com/wp-content/uploads/2023/03/Course-Thubnails-1.jpg",
        "bigPicture": "https://www.aqskill.com/wp-content/uploads/2023/03/Course-Thubnails-1.jpg",
        "showWhen": true,
        "autoCancel": true,
        "privacy": "Private"
      }
    }
  });
  request.headers.addAll(headers);

  http.StreamedResponse response = await request.send();

  if (response.statusCode == 200) {
    print(await response.stream.bytesToString());
  }

  else {
  print(response.reasonPhrase);
  }

}