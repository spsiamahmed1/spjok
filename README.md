# ১০ ঘণ্টা চ্যালেঞ্জ

শুধু Java ও XML-এ তৈরি বাংলা Android পড়াশোনা-ফোকাস অ্যাপ। কোনো Kotlin বা Flutter নেই। Hind Siliguri ফন্ট, glass-style UI, লক্ষ্য বাছাই, এক ঘণ্টার focus ও দুই মিনিটের rest cycle, পানি গণনা, করণীয় তালিকা, প্রতিবেদন এবং সেটিংস রয়েছে।

## ফোন থেকে APK বানানো

1. GitHub অ্যাপে `spsiamahmed1/spjok` রিপোজিটরিটি খুলুন।
2. **Actions** ট্যাবে যান এবং **Android APK** workflow বেছে নিন।
3. **Run workflow** চাপুন। কোড `main`-এ push হলেও workflow নিজে থেকেই চলে।
4. workflow সফল হলে সেই run-এর **Artifacts** অংশ থেকে `ten-hour-challenge-debug-apk` নামের ফাইলটি নামিয়ে নিন। ZIP-এর ভেতরে `app-debug.apk` থাকবে।
5. ফোনে APK খুলে ইনস্টল করুন। Android চাইলে এই উৎস থেকে ইনস্টল করার অনুমতি দিন।

APK তৈরি হতে কয়েক মিনিট লাগতে পারে। Build ব্যর্থ হলে Actions-এর run log-এ error দেখা যাবে।

## অ্যাপের বৈশিষ্ট্য

- ৫, ৮, ১০ বা ১২ ঘণ্টার লক্ষ্য; শুরু সময় থেকে শেষ সময় পরের দিন হলেও সঠিকভাবে দেখায়।
- প্রতিটি সম্পন্ন ফোকাস ঘণ্টার পর ২ মিনিটের বিশ্রাম; লক্ষ্য পূর্ণ হলে সনদ স্ক্রিন।
- সেশন, করণীয় ও প্রতিবেদন SharedPreferences-এ স্থানীয়ভাবে থাকে। অ্যাপ আবার খুললে চলমান সেশন পুনরায় চালু করা যায়।
- বিরতির বিজ্ঞপ্তি, শব্দ ও কম্পন আলাদা করে বন্ধ/চালু করা যায়।
- সাপ্তাহিক চার্ট Android Canvas দিয়ে আঁকা; কোনো তৃতীয়-পক্ষ chart বা confetti library নেই।
- সনদ PNG হিসেবে MediaStore-এ সংরক্ষণ করা যায়।

## প্রযুক্তি

Java 17, Android Gradle Plugin 8.7.3, Gradle 8.9, minSdk 24, targetSdk 34 এবং ViewBinding ব্যবহার করা হয়েছে। Timer, report, persistence ও UI-র জন্য কোনো external runtime dependency নেই।

Hind Siliguri font Google Fonts-এর OFL লাইসেন্সে অন্তর্ভুক্ত।
