# MusicQuiz   
##Music Quiz using Spotify API            [![Build Status](https://travis-ci.org/DavidPos/MusicQuiz.svg)](https://travis-ci.org/DavidPos/MusicQuiz)


Music quiz that plays a song and you have to guess who it is. Uses the Spotify API for all the information.
[![Home Screen](https://github.com/DavidPos/MusicQuiz/blob/master/ScreenShots/Screenshot_2015-11-05-15-12-12.jpg)]()

#BackLog
Add active search for suggesting playlist as you type in search box.
Cursor adapter for active search to show playlist cover.

#Issues Resolved
Wanted to persist the authentication session to stop the need to login/approve access when you start another quiz. Due to the fact the spotifiy Android API uses an implicit grant flow for authentication this was not possible. 
The issue is well explained here: https://github.com/spotify/android-sdk/issues/5

#Copyright
Copyright [2015] [David Postlethwaite]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
