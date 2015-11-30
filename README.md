# StreamRecorder

StreamRecorder is a simple command line tool to record HTTP streams (e.g., ShoutCast, IceCast) that contain AAC or MP3 audio.

## Requirements

Java 8

## Usage

    Usage: stream-recorder [options]
      Options:
      * -d, --dir
           Directory to save the recordings
        -h, --hour
           Recording duration in hours (can be combined with minutes)
           Default: 0
        -m, --min
           Recording duration in minutes (can be combined with hours)
           Default: 0
      * -u, --url
           Stream url to record
        -n, -name
           Name of the stream/broadcast. Will be used as the prefix of the file. If
           not set, the information will be derived from the stream headers


Example:

    # java -jar stream-recorder-X.X.X.jar -u "http://127.0.0.1/stream/mp3" -d "C:\recordings" -h 1 -m 30

