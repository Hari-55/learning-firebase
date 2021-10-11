# learning-firebase

Hubungkan Aplikasi ke firebase terlebih dahulu
https://console.firebase.google.com/u/0/

kemudian dapatkan google-service.json

letakan pada root : project -> app

Buka build gradle module tambahkan Storage base dan realtime base, dengan path yang dipunya


Untuk menggunakan firestore mohon isi data dulu untuk spinner class yang dipilih dengan format

collection : class -> document: idClass(1..n) -> field : {_class, day, name}
